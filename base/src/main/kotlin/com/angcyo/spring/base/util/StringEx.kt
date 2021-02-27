package com.angcyo.spring.base.util

import java.net.URLDecoder
import java.net.URLEncoder

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/02/27
 */

fun String.encode(enc: String = "UTF-8"): String = URLEncoder.encode(this, enc)

fun String.decode(enc: String = "UTF-8"): String = URLDecoder.decode(this, enc)