package com.angcyo.spring.base

import com.angcyo.spring.base.util.PrettyMemoryUtil
import java.nio.charset.Charset
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
inline fun <reified T> String.bean() = Base.getBean<T>(this)

/**
 * 根据Class, 扩展获取Bean对象
 * */
fun <T> Class<T>.bean() = Base.getBean(this)

/**获取[application.properties]中, 配置的值*/
fun String.propertyValue() = Base.applicationContext.environment.getProperty(this)

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

fun ByteArray.string(charset: Charset = Charset.defaultCharset()) = String(this, charset)
fun ByteArray.string(charset: String?) = String(this, Charset.forName(charset ?: "UTF-8"))

/**一天对应多少毫秒 [86400_000]*/
val oneDay: Long get() = 1000L * oneDaySec

/**一天对应多少秒 [86400_000]*/
val oneDaySec: Long get() = 60 * 60 * 24L

fun Long.prettyByteSize() = PrettyMemoryUtil.prettyByteSize(this)

/*----------------------------------------------------------------------------------*/

fun Any?.str(): String {
    return if (this is String) {
        this
    } else {
        this.toString()
    }
}

/**如果为空, 则执行[action].
 * 原样返回*/
fun <T> T?.elseNull(action: () -> Unit = {}): T? {
    if (this == null) {
        action()
    }
    return this
}