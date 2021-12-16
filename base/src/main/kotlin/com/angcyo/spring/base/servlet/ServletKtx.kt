package com.angcyo.spring.base.servlet

import com.angcyo.spring.base.data.error
import com.angcyo.spring.util.decode
import com.angcyo.spring.util.json.fromJson
import com.angcyo.spring.util.json.toJson
import com.angcyo.spring.util.string
import com.angcyo.spring.util.toByteArray
import org.springframework.http.MediaType
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import java.awt.image.RenderedImage
import javax.servlet.ServletRequest
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/11/07
 */

//<editor-fold desc="ServletRequest扩展">

/**https://blog.csdn.net/justlpf/article/details/88523638
 * https://www.cnblogs.com/wade-luffy/p/8867144.html
 * */
fun requestAttributes() = RequestContextHolder.currentRequestAttributes()

fun servletRequestAttributes(): ServletRequestAttributes? {
    val attributes = requestAttributes()
    if (attributes is ServletRequestAttributes) {
        return attributes
    }
    return null
}

/**获取当前的[HttpServletRequest]*/
fun request() = servletRequestAttributes()?.request

/**获取当前的[HttpServletResponse]*/
fun response() = servletRequestAttributes()?.response

/**读取请求体字符串数据*/
fun ServletRequest.body() = if (contentLength > 0) if (this is IStreamWrapper) this.toByteArray(true)?.string()
else inputStream.readBytes().string()
else null

fun ServletRequest.bytes() = if (contentLength > 0) if (this is IStreamWrapper) this.toByteArray(true)
else inputStream.readBytes()
else null

fun <T> ServletRequest.fromJson(classOfT: Class<T>): T? {
    return if (contentLengthLong > 0) {
        body()?.fromJson<T>(classOfT)
    } else {
        null
    }
}

/**获取请求参数, 优先从参数中获取, 其次从请求头中获取*/
fun ServletRequest.param(key: String): String? {
    val parameter = getParameter(key)
    if (parameter == null) {
        if (this is HttpServletRequest) {
            return getHeader(key)
        }
    }
    return parameter
}

/**当前请求, 是否来自管理员. 特殊处理*/
fun ServletRequest.isAdmin(): Boolean {
    return param("debug").isTruthy()
}

fun String?.isTruthy() = this == "truthy"

/**请求地址, ip地址和本地ip地址*/
fun ServletRequest.address(): String {
    return "$remoteAddr:$remotePort/$localAddr:$localPort/$localName"
}

/**请求地址, url地址*/
fun HttpServletRequest.requestUrl(): String {
    return requestURL.toString().decode()
}

/**将相对路径转换成url全路径
 * file/downloadFile/664dde66-0eea-4eec-8791-a7081d5863d1.png
 * ->
 * http://localhost:9203/file/downloadFile/664dde66-0eea-4eec-8791-a7081d5863d1.png
 * */
fun String.toServletUri(): String {
    return ServletUriComponentsBuilder.fromCurrentContextPath().path(this).toUriString()
}

//</editor-fold desc="ServletRequest扩展">

//<editor-fold desc="ServletResponse扩展">

/**写入返回体消息*/
fun HttpServletResponse.send(
    message: String?,
    code: Int = HttpServletResponse.SC_OK,
    type: String = "application/json"
) {
    characterEncoding = "UTF-8"
    send(message?.toByteArray(Charsets.UTF_8), code, type)
}

fun HttpServletResponse.send(
    bytes: ByteArray?,
    code: Int = HttpServletResponse.SC_OK,
    type: String = "application/json"
) {
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

/**发送一个Rest格式的错误数据*/
fun HttpServletResponse.sendError(message: String?) {
    send(message.error<String>().toJson())
}

@Throws(IllegalStateException::class)
fun HttpServletResponse.throwError(message: String?) {
    send(message.error<String>().toJson())
    error(message ?: "throwError")
}

/**重定向
 * response.sendRedirect("/redirect/index?base=r2");
 * https://www.cnblogs.com/yihuihui/p/11650078.html*/
fun HttpServletResponse.redirect(location: String) {
    sendRedirect(location)
}

fun HttpServletResponse.sendFileBase() {
    //设置响应头
    setHeader("Pragma", "no-cache")
    //设置响应头
    setHeader("Cache-Control", "no-cache")
    //在代理服务器端防止缓冲
    setDateHeader("Expires", 0)
    //允许CORS跨域请求
    setHeader("Access-Control-Allow-Origin", "*")
}

/**返回一个文件数据, "image/jpeg"
 * [MediaType.APPLICATION_OCTET_STREAM]*/
fun HttpServletResponse.send(file: ByteArray, type: String = MediaType.IMAGE_PNG_VALUE) {
    sendFileBase()
    //设置响应内容类型
    send(bytes = file, type = type)
}

fun HttpServletResponse.send(file: RenderedImage, type: String = MediaType.IMAGE_PNG_VALUE) {
    sendFileBase()
    //设置响应内容类型
    send(file.toByteArray(type.substring(type.lastIndexOf("/") + 1)), type = type)
}

//</editor-fold desc="ServletResponse扩展">