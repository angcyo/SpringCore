package com.angcyo.spring.core.log

import com.angcyo.spring.core.log.wrapper.RequestWrapper
import com.angcyo.spring.core.log.wrapper.RequestWrapper2
import com.angcyo.spring.core.log.wrapper.ResponseWrapper
import com.angcyo.spring.core.log.wrapper.ResponseWrapper2
import com.angcyo.spring.core.toString
import com.angcyo.spring.core.util.IPUtil
import com.angcyo.spring.core.util.trimAll
import com.angcyo.spring.core.uuid
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

                //请求体
                if (request is RequestWrapper) {
                    val bytes = request.toByteArray()
                    appendLine()
                    append("body(${DataSize.ofBytes(bytes.size.toLong())})↓")
                    if (bytes.isNotEmpty()) {
                        appendLine()
                        if (!request.isMultipart() && !request.isBinaryContent()) {
                            try {
                                append(bytes.toString(request.characterEncoding).trimAll())
                            } catch (e: Exception) {
                                append(e.stackTraceToString())
                            }
                        } else {
                            append("binary body.")
                        }
                    }
                } else if (request is RequestWrapper2) {
                    val bytes = request.body
                    appendLine()
                    append("body(${DataSize.ofBytes(bytes.size.toLong())})↓")
                    if (bytes.isNotEmpty()) {
                        appendLine()
                        if (!request.isMultipart() && !request.isBinaryContent()) {
                            try {
                                append(bytes.toString(request.characterEncoding).trimAll())
                            } catch (e: Exception) {
                                append(e.stackTraceToString())
                            }
                        } else {
                            append("binary body.")
                        }
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

                //返回体
                if (response is ResponseWrapper) {
                    val bytes = response.toByteArray()
                    appendLine()
                    append("body(${DataSize.ofBytes(bytes.size.toLong())})↓")
                    if (bytes.isNotEmpty()) {
                        appendLine()
                        if (!response.isMultipart() && !response.isBinaryContent()) {
                            try {
                                append(bytes.toString(response.characterEncoding).trimAll())
                            } catch (e: Exception) {
                                append(e.stackTraceToString())
                            }
                        } else {
                            append("binary body.")
                        }
                    }
                } else if (response is ResponseWrapper2) {
                    val bytes = response.responseData
                    appendLine()
                    append("body(${DataSize.ofBytes(bytes.size.toLong())})↓")
                    if (bytes.isNotEmpty()) {
                        appendLine()
                        if (!response.isMultipart() && !response.isBinaryContent()) {
                            try {
                                append(bytes.toString(response.characterEncoding).trimAll())
                            } catch (e: Exception) {
                                append(e.stackTraceToString())
                            }
                        } else {
                            append("binary body.")
                        }
                    }
                }
                appendLine()
            }
        }
        return builder.toString()
    }

    /**包装一下, 请求 返回日志输出*/
    fun wrap(requestId: Long,
             request: HttpServletRequest,
             response: HttpServletResponse,
             action: (request: HttpServletRequest, response: HttpServletResponse) -> Unit) {
        // 开始时间
        val startTime = System.currentTimeMillis()
        val uuid = uuid()

        val requestWrapper = RequestWrapper(request)
        //val requestWrapper = RequestWrapper2(request)
        val responseWrapper = ResponseWrapper(response)
        //val responseWrapper = ResponseWrapper2(response)

        try {
            val requestBuilder = StringBuilder().apply {
                appendLine()
                append("-->$uuid $requestId")
            }

            L.ih(requestWrapper.log(requestBuilder))

            //chain
            action(requestWrapper, responseWrapper)
        } catch (e: Exception) {
            L.db(e.stackTraceToString())
        } finally {
            val responseBuilder = StringBuilder().apply {
                appendLine()
                append("<--$uuid")
                append(" ")
                append(responseWrapper.status)
                append(" ${System.currentTimeMillis() - startTime}ms")
            }

            L.ih(responseWrapper.log(responseBuilder))
        }
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