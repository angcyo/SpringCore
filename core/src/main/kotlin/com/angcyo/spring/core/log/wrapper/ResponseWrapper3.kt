package com.angcyo.spring.core.log.wrapper

import java.io.IOException
import java.io.PrintWriter
import java.io.Writer
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpServletResponseWrapper


/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/11/07
 *
 * https://stackoverflow.com/questions/3242236/capture-and-log-the-response-body
 */

@Deprecated("getWriter() has already been called for this response")
class ResponseWrapper3(response: HttpServletResponse) : HttpServletResponseWrapper(response) {

    /*var baos: ByteArrayOutputStream = ByteArrayOutputStream()
    val ps: PrintStream = PrintStream(baos)

    @Throws(IOException::class)
    override fun getOutputStream(): ServletOutputStream? {
        return DelegatingServletOutputStream(TeeOutputStream(super.getOutputStream(), ps))
    }

    @Throws(IOException::class)
    override fun getWriter(): PrintWriter? {
        return PrintWriter(DelegatingServletOutputStream(TeeOutputStream(super.getOutputStream(), ps)))
    }*/

    private val writer: CopyPrintWriter = CopyPrintWriter(response.writer)

    @Throws(IOException::class)
    override fun getWriter(): PrintWriter? {
        return writer
    }

    fun string() = writer.getCopy()

    class CopyPrintWriter(writer: Writer) : PrintWriter(writer) {
        private val copy = StringBuilder()
        override fun write(c: Int) {
            copy.append(c.toChar()) // It is actually a char, not an int.
            super.write(c)
        }

        override fun write(chars: CharArray, offset: Int, length: Int) {
            copy.append(chars, offset, length)
            super.write(chars, offset, length)
        }

        override fun write(string: String, offset: Int, length: Int) {
            copy.append(string, offset, length)
            super.write(string, offset, length)
        }

        fun getCopy(): String {
            return copy.toString()
        }
    }
}