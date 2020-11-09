/*
 * spring-mvc-logger logs requests/responses
 *
 * Copyright (c) 2013. Israel Zalmanov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.angcyo.spring.log.core.wrapper

import org.apache.commons.io.output.TeeOutputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.PrintWriter
import javax.servlet.ServletOutputStream
import javax.servlet.ServletResponse
import javax.servlet.WriteListener
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpServletResponseWrapper

/**
 * 2020-11-6
 * https://github.com/isrsal/spring-mvc-logger
 * */
class ResponseWrapper(response: HttpServletResponse) : HttpServletResponseWrapper(response) {
    private val bos = ByteArrayOutputStream()
    private val writer = PrintWriter(bos)
    var isWrite = false

    init {
        characterEncoding = Charsets.UTF_8.toString()
    }

    override fun getResponse(): ServletResponse {
        return this
    }

    @Throws(IOException::class)
    override fun getOutputStream(): ServletOutputStream {
        return object : ServletOutputStream() {
            override fun isReady(): Boolean {
                return false
            }

            override fun setWriteListener(writeListener: WriteListener) {}

            private val tee = TeeOutputStream(super@ResponseWrapper.getOutputStream(), bos)

            @Throws(IOException::class)
            override fun write(b: Int) {
                isWrite = true
                tee.write(b)
            }
        }
    }

    @Throws(IOException::class)
    override fun getWriter(): PrintWriter {
        return TeePrintWriter(super.getWriter(), writer)
    }

    override fun flushBuffer() {
        super.flushBuffer()
        //bos.flush()
        //writer.flush()
    }

    var _contentLengthLong: Long = -1

    override fun setContentLength(len: Int) {
        super.setContentLength(len)
    }

    override fun setContentLengthLong(length: Long) {
        super.setContentLengthLong(length)
        _contentLengthLong = length
    }

    override fun setContentType(type: String?) {
        super.setContentType(type)
    }

    fun toByteArray(): ByteArray {
        //flushBuffer()
        return bos.toByteArray()
    }
}