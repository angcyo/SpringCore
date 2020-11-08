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

import com.angcyo.spring.log.core.isBinaryContent
import com.angcyo.spring.log.core.isMultipart
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletRequestWrapper

/**
 * 2020-11-6
 * https://github.com/isrsal/spring-mvc-logger
 *
 * getReader() has already been called for this request
 * */

@Deprecated("getReader() has already been called for this request")
class RequestWrapper3(request: HttpServletRequest) : HttpServletRequestWrapper(request) {

/*    val bytes: ByteArray = try {
        val result = inputStream.readBytes()
        result
    } catch (e: Exception) {
        byteArrayOf()
    }*/

    val bytes: ByteArray? = try {
        if (isMultipart() || isBinaryContent()) {
            null
        } else {
            java.io.InputStreamReader(inputStream).readText().toByteArray()
        }
    } catch (e: Exception) {
        null
    }

    /*   override fun getInputStream(): ServletInputStream {
           return ByteArrayInputStream()
           //return super.getInputStream()
       }*/

    fun toByteArray(): ByteArray? {
        return bytes
    }
}