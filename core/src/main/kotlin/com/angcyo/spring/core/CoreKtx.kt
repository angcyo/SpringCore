package com.angcyo.spring.core

import java.sql.Date
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/11/04
 */

/**
 * 根据Bean的名字, 扩展获取Bean对象
 * */
inline fun <reified T> String.bean() = Core.getBean<T>(this)

/**
 * 根据Class, 扩展获取Bean对象
 * */
fun <T> Class<T>.bean() = Core.getBean(this)

/*----------------------------------------------------------------------------------*/

/**当前的[Date]对象*/
fun nowDate() = Date(nowTime())

/**当前的时间13位毫秒数*/
fun nowTime() = System.currentTimeMillis()

fun nowTimeString(pattern: String = "yyyy-MM-dd HH:mm:ss.SSS"): String {
    return nowTime().fullTime(pattern)
}

/**时间全格式输出*/
fun Long.fullTime(pattern: String = "yyyy-MM-dd HH:mm:ss.SSS"): String {
    return toTime(pattern)
}

/**格式化时间输出*/
fun Long.toTime(pattern: String = "yyyy-MM-dd HH:mm"): String {
    val format: SimpleDateFormat = SimpleDateFormat.getDateInstance() as SimpleDateFormat
    format.applyPattern(pattern)
    return format.format(java.util.Date(this))
}

/**2020-11-05 15:07:16.265363*/
fun LocalDateTime.toTime(pattern: String = "yyyy-MM-dd HH:mm"): String {
    return format(DateTimeFormatter.ofPattern(pattern, Locale.CHINA))
}

/*----------------------------------------------------------------------------------*/

fun uuid() = UUID.randomUUID().toString()