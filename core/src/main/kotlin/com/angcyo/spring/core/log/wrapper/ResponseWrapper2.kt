package com.angcyo.spring.core.log.wrapper

import java.io.*
import javax.servlet.ServletOutputStream
import javax.servlet.WriteListener
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpServletResponseWrapper

/**
 * @program: springboot
 * @description:
 * @author: Sid
 * @date: 2018-11-19 11:55
 * @since: 1.0
 */

@Deprecated("2020-11-6")
class ResponseWrapper2(resp: HttpServletResponse?) : HttpServletResponseWrapper(resp) {
    /**
     * This class implements an output stream in which the data is written into a byte array.
     * The buffer automatically grows as data is written to it. The data can be retrieved using toByteArray() and toString().
     * Closing a ByteArrayOutputStream has no effect. The methods in this class can be called after the stream has been closed without generating an IOException.
     */
    private var buffer: ByteArrayOutputStream = ByteArrayOutputStream() //输出到byte array
    private var out: ServletOutputStream
    private var writer: PrintWriter

    init {
        // 真正存储数据的流
        out = WrapperOutputStream(buffer)
        writer = PrintWriter(OutputStreamWriter(buffer, this.characterEncoding))
    }

    /**
     * 重载父类获取outputstream的方法
     */
    @Throws(IOException::class)
    override fun getOutputStream(): ServletOutputStream {
        return out
    }

    /**
     * 重载父类获取writer的方法
     */
    @Throws(UnsupportedEncodingException::class)
    override fun getWriter(): PrintWriter {
        return writer
    }

    /**
     * 重载父类获取flushBuffer的方法
     */
    @Throws(IOException::class)
    override fun flushBuffer() {
        out.flush()
        writer.flush()
    }

    override fun reset() {
        buffer.reset()
    }

    /**
     * 将out、writer中的数据强制输出到WapperedResponse的buffer里面，否则取不到数据
     */
    @get:Throws(IOException::class)
    val responseData: ByteArray
        get() {
            flushBuffer()
            return buffer.toByteArray()
        }

    /**
     * 内部类，对ServletOutputStream进行包装
     */
    private inner class WrapperOutputStream(stream: ByteArrayOutputStream?) : ServletOutputStream() {
        private var bos: ByteArrayOutputStream? = null

        @Throws(IOException::class)
        override fun write(b: Int) {
            bos!!.write(b)
        }

        @Throws(IOException::class)
        override fun write(b: ByteArray) {
            bos!!.write(b, 0, b.size)
        }

        override fun isReady(): Boolean {
            return false
        }

        override fun setWriteListener(listener: WriteListener) {}

        init {
            bos = stream
        }
    }
}