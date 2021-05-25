package com.angcyo.spring.log.core.wrapper

import com.angcyo.spring.util.L
import java.io.*
import java.nio.charset.Charset
import javax.servlet.ReadListener
import javax.servlet.ServletInputStream
import javax.servlet.ServletRequest
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletRequestWrapper


/**
 * 2020-11-6
 * https://blog.csdn.net/qq_24138151/article/details/106839127
 * https://blog.csdn.net/jy02268879/article/details/84243950
 * */

@Deprecated("2020-11-6")
class RequestWrapper2(request: HttpServletRequest) : HttpServletRequestWrapper(request) {
    val body: ByteArray

    init {
        body = getBodyString(request).toByteArray(Charset.forName("UTF-8"))
    }

    @Throws(IOException::class)
    override fun getReader(): BufferedReader {
        return BufferedReader(InputStreamReader(inputStream))
    }

    @Throws(IOException::class)
    override fun getInputStream(): ServletInputStream {

        //这里从body里面直接读了，没有去读inputStream了，很巧妙的方式
        val bais = ByteArrayInputStream(body)
        return object : ServletInputStream() {
            @Throws(IOException::class)
            override fun read(): Int {
                return bais.read()
            }

            override fun isFinished(): Boolean {
                return false
            }

            override fun isReady(): Boolean {
                return false
            }

            override fun setReadListener(readListener: ReadListener) {}
        }
    }

    fun getBodyString(request: ServletRequest): String {
        val sb = StringBuilder()
        var inputStream: InputStream? = null
        var reader: BufferedReader? = null
        try {
            inputStream = request.inputStream
            reader = BufferedReader(InputStreamReader(inputStream, Charset.forName("UTF-8")))
            var line: String? = ""
            while (reader.readLine().also { line = it } != null) {
                sb.append(line)
            }
        } catch (e: IOException) {
            L.w("处理异常", e)
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close()
                } catch (e: IOException) {
                    L.w("处理异常", e)
                }
            }
            if (reader != null) {
                try {
                    reader.close()
                } catch (e: IOException) {
                    L.w("处理异常", e)
                }
            }
        }
        return sb.toString()
    }
}