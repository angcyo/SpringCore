package com.angcyo.spring.log.core

import com.angcyo.spring.base.servlet.address
import com.angcyo.spring.base.servlet.bytes
import com.angcyo.spring.base.servlet.param
import com.angcyo.spring.log.core.ServletLog.KEY_REQUEST_TRACE_ID
import com.angcyo.spring.log.core.wrapper.*
import com.angcyo.spring.util.*
import org.springframework.util.unit.DataSize
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/11/06
 */

object ServletLog {

    /**日志追踪id*/
    val KEY_REQUEST_TRACE_ID = "requestTraceId"

    /**是否要强制记录日志
     * "true"
     * "false"*/
    val KEY_REQUEST_LOG = "requestLog"

    /**100ms 很慢的请求, 10秒的慢请求*/
    var REQUEST_LONG_TIME = 10_000

    /**保存请求id*/
    val logRequestUuid = ThreadLocal<String>()
    //val logRequestId = ThreadLocal<String>()

    val ignoreUriList = mutableListOf<String>()

    /**添加需要忽略log的url地址匹配规则
     * 只匹配servletPath路径即可*/
    fun addLogIgnore(uriList: List<String>) {
        uriList.forEach {
            val uri = it.replace("*", ".*")
            if (!ignoreUriList.contains(uri)) {
                ignoreUriList.add(uri)
            }
        }
    }

    /**包装一下, 请求 返回日志输出*/
    fun wrap(
        requestId: Long,
        request: HttpServletRequest,
        response: HttpServletResponse?,
        requestBuilder: StringBuilder = StringBuilder(),
        responseBuilder: StringBuilder = StringBuilder(),
        wrap: Boolean = true,
        action: (
            request: HttpServletRequest, response: HttpServletResponse?,
            requestBuilder: StringBuilder?, responseBuilder: StringBuilder?
        ) -> Unit
    ) {

        val requestLog = request.param(KEY_REQUEST_LOG)
        if (requestLog.isNullOrEmpty()) {
            //未指定, 智能判断
            if (ignoreUriList.isNotEmpty()) {
                if (ignoreUriList.matcherOf(request.servletPath)) {
                    //需要忽略的请求
                    action(request, response, null, null)
                    return
                }
            }

            if (!request.contentType.have("json")) {
                //只有json数据, 才记录log
                action(request, response, null, null)
                return
            }
        } else if (requestLog == "false") {
            //不需要日志
            action(request, response, null, null)
            return
        }

        // 开始时间
        val startTime = System.currentTimeMillis()
        val uuid = request.requestTraceId() ?: uuid()
        logRequestUuid.set(uuid)

        //将id返回回去
        response?.setHeader(KEY_REQUEST_TRACE_ID, uuid)

        val requestWrapper = if (wrap) RequestWrapper(request) else request
        //val requestWrapper = RequestWrapper2(request)
        //val responseWrapper = if (wrap && response != null) ResponseWrapper3(response) else response
        //val responseWrapper = ResponseWrapper2(response)
        val responseWrapper = if (wrap && response != null) ResponseWrapper(response) else response

        try {
            requestBuilder.apply {
                appendLine()
                append("-->$uuid $requestId")
            }

            //在之前之前打印Request Body, 可能为空. 所以这里放在后面执行
            //L.ih(requestWrapper.log(requestBuilder))

            //chain
            action(requestWrapper, responseWrapper, null, null)
        } catch (e: Exception) {
            L.dbError(e.stackTraceToString())
            throw e
        } finally {
            val nowTime = System.currentTimeMillis()
            val duration = nowTime - startTime
            responseBuilder.apply {
                appendLine()
                append("<--$uuid")
                append(" ")
                append(responseWrapper?.status)
                append(" ${duration}ms")
            }

            requestWrapper.log(requestBuilder, true)
            responseWrapper?.log(responseBuilder)

            //打印
            action(requestWrapper, responseWrapper, requestBuilder, responseBuilder)

            val address = "${request.address()} ${nowTimeString()}"

            if (duration > REQUEST_LONG_TIME) {
                //慢请求
                L._logDb.warn(">${REQUEST_LONG_TIME}ms", uuid, request.servletPath, "${duration}ms", address)
            }
            //请求日志
            L._logDb.info(buildString {
                appendLine(nowTimeString())
                appendLine(requestBuilder)
                append(responseBuilder)
            }, uuid, request.servletPath, "${duration}ms", address)
        }
    }

    /**打印请求体*/
    fun logRequest(
        request: ServletRequest,
        builder: StringBuilder = StringBuilder(),
        readStream: Boolean = false
    ): String {
        builder.apply {
            if (request is HttpServletRequest) {
                //入站
                appendLine()
                append(request.method)
                append(" ")
                append(request.requestURL.toString().decode())
                append(" ")
                append(IPUtil.getIpAddress(request))
                append(" ${request.localAddr}/${request.remoteAddr}")

                //session
                request.getSession(false)?.apply {
                    appendLine()
                    append(id)
                    append(" ${creationTime.fullTime()}/${lastAccessedTime.fullTime("HH:mm:ss.SSS")}")
                    attributeNames.iterator().forEach {
                        appendLine()
                        append(it)
                        append(":")
                        append(getAttribute(it))
                    }
                }

                //参数
                request.queryString?.let {
                    appendLine()
                    append(it)
                }
                /*request.parameterMap.onEach {
                    appendLine()
                    append(it.key)
                    append(":")
                    append(it.value.toList().toString())
                }*/

                val bodySize: Long = request.contentLengthLong
                appendLine()
                append("body(${DataSize.ofBytes(bodySize)} ${bodySize.prettyByteSize()})")
                if (request.isMultipart() || request.isBinaryContent()) {
                    append("↓")
                    appendLine()
                    append("binary body.")
                } else if (bodySize != -1L) {
                    fun _log(bytes: ByteArray?) {
                        val size = bytes?.size?.toLong() ?: -1

                        if (size > 0) {
                            append(" $size")
                            append("↓")
                            appendLine()

                            if (bytes?.isNotEmpty() == true) {
                                try {
                                    val body = bytes.string(request.characterEncoding)
                                    append(body)
                                } catch (e: Exception) {
                                    append(e.stackTraceToString())
                                }
                            }
                        }
                    }

                    //请求体
                    when (request) {
                        is RequestWrapper -> {
                            val bytes = request.toByteArray(readStream)
                            _log(bytes)
                        }
                        is RequestWrapper2 -> {
                            val bytes = request.body
                            _log(bytes)
                        }
                        is RequestWrapper3 -> {
                            val bytes = request.toByteArray()
                            _log(bytes)
                        }
                        else -> {
                            val bytes = request.bytes()
                            _log(bytes)
                        }
                    }
                } else {
                    //appendLine()
                }

                //请求头
                request.headerNames.iterator().forEach {
                    appendLine()
                    append(it)
                    append(":")
                    append(request.getHeader(it))
                }

                appendLine()
            }
        }
        return builder.toString()
    }

    fun logResponse(response: ServletResponse, builder: StringBuilder = StringBuilder()): String {
        builder.apply {
            if (response is HttpServletResponse) {
                //返回头
                response.headerNames.iterator().forEach {
                    appendLine()
                    append(it)
                    append(":")
                    append(response.getHeader(it))
                }

                fun _log(bytes: ByteArray?) {
                    val size = bytes?.size?.toLong() ?: -1
                    if (size > 0) {
                        appendLine()
                        append("body(${DataSize.ofBytes(size)} ${size.prettyByteSize()})↓")
                        if (bytes?.isNotEmpty() == true) {
                            appendLine()
                            if (!response.isMultipart() && !response.isBinaryContent()) {
                                try {
                                    val body = bytes.string(response.characterEncoding)
                                    append(body)
                                } catch (e: Exception) {
                                    append(e.stackTraceToString())
                                }
                            } else {
                                append("binary body.")
                            }
                        }
                    } else {
                        //append("body empty.")
                    }
                }

                //返回体
                when (response) {
                    is ResponseWrapper -> {
                        val bytes = response.toByteArray()
                        _log(bytes)
                    }
                    is ResponseWrapper2 -> {
                        val bytes = response.responseData
                        _log(bytes)
                    }
                    is ResponseWrapper3 -> {
                        val bytes = response.string().toByteArray()
                        _log(bytes)
                    }
                }
                appendLine()
            }
        }
        return builder.toString()
    }
}

/**二进制请求*/
fun HttpServletRequest.isBinaryContent(): Boolean {
    return if (contentType == null) {
        false
    } else contentType.startsWith("image") ||
            contentType.startsWith("video") ||
            contentType.startsWith("audio")
}

fun HttpServletResponse.isBinaryContent(): Boolean {
    return if (contentType == null) {
        false
    } else contentType.startsWith("image") ||
            contentType.startsWith("video") ||
            contentType.startsWith("audio")
}

/**多部分请求*/
fun HttpServletRequest.isMultipart(): Boolean {
    return contentType != null && contentType.startsWith("multipart/form-data")
}

fun HttpServletResponse.isMultipart(): Boolean {
    return contentType != null && contentType.startsWith("multipart/form-data")
}

/**请求追踪的id*/
fun HttpServletRequest.requestTraceId() = param(KEY_REQUEST_TRACE_ID)

fun ServletRequest.log(builder: StringBuilder = StringBuilder(), readStream: Boolean = false): String =
    ServletLog.logRequest(this, builder, readStream)

fun ServletResponse.log(builder: StringBuilder = StringBuilder()): String = ServletLog.logResponse(this, builder)