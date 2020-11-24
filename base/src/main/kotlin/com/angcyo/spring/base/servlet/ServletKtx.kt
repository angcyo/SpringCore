package com.angcyo.spring.base.servlet

import com.angcyo.spring.base.data.error
import com.angcyo.spring.base.json.fromJson
import com.angcyo.spring.base.json.toJson
import com.angcyo.spring.base.string
import javax.servlet.ServletRequest
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/11/07
 */

/**读取请求体字符串数据*/
fun ServletRequest.body() = if (contentLength > 0)
    if (this is IStreamWrapper)
        this.toByteArray(true)?.string()
    else
        inputStream.readBytes().string()
else null

fun ServletRequest.bytes() = if (contentLength > 0)
    if (this is IStreamWrapper)
        this.toByteArray(true)
    else
        inputStream.readBytes()
else null

fun <T> ServletRequest.fromJson(classOfT: Class<T>): T? {
    return if (contentLengthLong > 0) {
        body()?.fromJson<T>(classOfT)
    } else {
        null
    }
}

/**写入返回体消息*/
fun HttpServletResponse.send(message: String?,
                             code: Int = HttpServletResponse.SC_OK,
                             type: String = "application/json") {
    status = code
    contentType = type
    characterEncoding = "UTF-8"
    if (message == null) {
        setContentLengthLong(-1)
        writer.close()
    } else {
        setContentLengthLong(message.toByteArray().size.toLong())
        writer.use {
            it.print(message)
        }
    }
}

/**发送一个Rest格式的错误数据*/
fun HttpServletResponse.sendError(message: String?) {
    send(message.error<String>().toJson())
}

@Throws(IllegalStateException::class)
fun HttpServletResponse.throwError(message: String?) {
    send(message.error<String>().toJson())
    error(message ?: "throwError")
}

fun HttpServletResponse.send(bytes: ByteArray?,
                             code: Int = HttpServletResponse.SC_OK,
                             type: String = "application/json") {
    status = code
    contentType = type
    characterEncoding = "UTF-8"
    if (bytes == null) {
        setContentLengthLong(-1)
        writer.close()
    } else {
        setContentLengthLong(bytes.size.toLong())
        outputStream.use {
            it.write(bytes)
        }
    }
}

/**获取请求参数, 优先从参数中获取, 其次从请求头中获取*/
fun HttpServletRequest.param(key: String): String? {
    return getParameter(key) ?: return getHeader(key)
}