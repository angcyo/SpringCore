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
package com.angcyo.spring.core.log.wrapper

import org.apache.commons.io.input.TeeInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import javax.servlet.ReadListener
import javax.servlet.ServletInputStream
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletRequestWrapper

/**
 * 2020-11-6
 * https://github.com/isrsal/spring-mvc-logger
 * */

class RequestWrapper(request: HttpServletRequest) : HttpServletRequestWrapper(request) {
    private val bos = ByteArrayOutputStream()

    @Throws(IOException::class)
    override fun getInputStream(): ServletInputStream {
        return object : ServletInputStream() {
            override fun isFinished(): Boolean {
                return false
            }

            override fun isReady(): Boolean {
                return false
            }

            override fun setReadListener(readListener: ReadListener) {}
            private val tee = TeeInputStream(super@RequestWrapper.getInputStream(), bos)

            @Throws(IOException::class)
            override fun read(): Int {
                return tee.read()
            }
        }
    }

    fun toByteArray(): ByteArray {
        return bos.toByteArray()
    }
}