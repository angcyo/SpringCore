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

import java.io.PrintWriter

/**
 * 2020-11-6
 * https://github.com/isrsal/spring-mvc-logger
 * */
class TeePrintWriter(main: PrintWriter, var branch: PrintWriter) : PrintWriter(main, true) {
    override fun write(buf: CharArray, off: Int, len: Int) {
        super.write(buf, off, len)
        super.flush()
        branch.write(buf, off, len)
        branch.flush()
    }

    override fun write(s: String, off: Int, len: Int) {
        super.write(s, off, len)
        super.flush()
        branch.write(s, off, len)
        branch.flush()
    }

    override fun write(c: Int) {
        super.write(c)
        super.flush()
        branch.write(c)
        branch.flush()
    }

    override fun flush() {
        super.flush()
        branch.flush()
    }
}