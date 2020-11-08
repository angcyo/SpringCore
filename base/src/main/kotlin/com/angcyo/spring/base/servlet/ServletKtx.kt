package com.angcyo.spring.base.servlet

import com.angcyo.spring.base.string
import javax.servlet.ServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/11/07
 */

/**读取请求体字符串数据*/
fun ServletRequest.body() = if (contentLength >= 0) inputStream.readBytes().string() else null

fun ServletRequest.bytes() = if (contentLength >= 0) inputStream.readBytes() else null

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