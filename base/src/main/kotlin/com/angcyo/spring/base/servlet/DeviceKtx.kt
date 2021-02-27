package com.angcyo.spring.base.servlet

import com.angcyo.spring.base.util.decode
import javax.servlet.http.HttpServletRequest

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/02/27
 */

fun HttpServletRequest.androidId() = getHeader("androidId")?.decode()

fun HttpServletRequest.deviceInfo() = getHeader("deviceInfo")?.decode()