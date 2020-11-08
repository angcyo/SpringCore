package com.angcyo.spring.log.core

import com.angcyo.spring.base.prettyByteSize
import com.angcyo.spring.base.servlet.bytes
import com.angcyo.spring.base.string
import com.angcyo.spring.base.util.IPUtil
import com.angcyo.spring.base.util.L
import com.angcyo.spring.base.util.trimAll
import com.angcyo.spring.base.uuid
import com.angcyo.spring.log.core.wrapper.*
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

    /**包装一下, 请求 返回日志输出*/
    fun wrap(requestId: Long,
             request: HttpServletRequest,
             response: HttpServletResponse?,
             requestBuilder: StringBuilder = StringBuilder(),
             responseBuilder: StringBuilder = StringBuilder(),
             wrap: Boolean = true,
             action: (request: HttpServletRequest, response: HttpServletResponse?) -> Unit) {
        // 开始时间
        val startTime = System.currentTimeMillis()
        val uuid = uuid()

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

            L.ih(requestWrapper.log(requestBuilder))

            //chain
            action(requestWrapper, responseWrapper)
        } catch (e: Exception) {
            L.db(e.stackTraceToString())
        } finally {
            responseBuilder.apply {
                appendLine()
                append("<--$uuid")
                append(" ")
                append(responseWrapper?.status)
                append(" ${System.currentTimeMillis() - startTime}ms")
            }

            L.ih(responseWrapper?.log(responseBuilder))
        }
    }

    /**打印请求体*/
    fun logRequest(request: ServletRequest, builder: StringBuilder = StringBuilder()): String {
        builder.apply {
            if (request is HttpServletRequest) {
                //入站
                appendLine()
                append(request.method)
                append(" ")
                append(request.requestURL.toString())
                append(" ")
                append(IPUtil.getIpAddress(request))
                append(" ${request.localAddr}/${request.remoteAddr}")

                //参数
                appendLine()
                append(request.queryString)
                request.parameterMap.onEach {
                    appendLine()
                    append(it.key)
                    append(":")
                    append(it.value.toList().toString())
                }

                fun _log(bytes: ByteArray?) {
                    appendLine()
                    val size = bytes?.size?.toLong() ?: -1
                    append("body(${DataSize.ofBytes(size)} ${size.prettyByteSize()})↓")
                    if (bytes?.isNotEmpty() == true) {
                        appendLine()
                        if (!request.isMultipart() && !request.isBinaryContent()) {
                            try {
                                append(bytes.string(request.characterEncoding).trimAll())
                            } catch (e: Exception) {
                                append(e.stackTraceToString())
                            }
                        } else {
                            append("binary body.")
                        }
                    }
                }

                //请求体
                when (request) {
                    is RequestWrapper -> {
                        val bytes = request.toByteArray()
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
                    appendLine()
                    val size = bytes?.size?.toLong() ?: -1
                    append("body(${DataSize.ofBytes(size)} ${size.prettyByteSize()})↓")
                    if (bytes?.isNotEmpty() == true) {
                        appendLine()
                        if (!response.isMultipart() && !response.isBinaryContent()) {
                            try {
                                append(bytes.string(response.characterEncoding).trimAll())
                            } catch (e: Exception) {
                                append(e.stackTraceToString())
                            }
                        } else {
                            append("binary body.")
                        }
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

fun ServletRequest.log(builder: StringBuilder = StringBuilder()): String = ServletLog.logRequest(this, builder)

fun ServletResponse.log(builder: StringBuilder = StringBuilder()): String = ServletLog.logResponse(this, builder)