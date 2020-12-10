package com.angcyo.spring.base

import com.angcyo.spring.base.util.PrettyMemoryUtil
import org.springframework.beans.BeanUtils
import java.nio.charset.Charset
import java.sql.Date
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.random.Random.Default.nextInt

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

/**从文本的后面, 一个字符一个字符剔除, 并循环调用剩下的字符串¬
 * [sub]剩下的文本
 * [drop]被剔除的文本*/
inline fun CharSequence.eachDropLast(action: (sub: CharSequence, drop: CharSequence) -> Unit) {
    for (i in 0 until length) {
        val sub = subSequence(0, length - i)
        val drop = subSequence(length - i, length)
        action(sub, drop)
    }
}

/**从文本的前面, 一个字符一个字符剔除, 并循环调用剩下的字符串¬
 * [sub]剩下的文本
 * [drop]被剔除的文本*/
inline fun CharSequence.eachDropFirst(action: (sub: CharSequence, drop: CharSequence) -> Unit) {
    for (i in 0 until length) {
        val sub = subSequence(i, length)
        val drop = subSequence(0, i)
        action(sub, drop)
    }
}

/**限制字符允许的最大字符串
 * [dropEnd]  超出范围后, 丢掉后的字符串, 否则丢掉前面的字符串*/
fun CharSequence.maxLength(maxLength: Int, dropEnd: Boolean = true): CharSequence {
    return if (length > maxLength) {
        if (dropEnd) {
            subSequence(0, maxLength)
        } else {
            subSequence(length - maxLength, length)
        }
    } else {
        this
    }
}

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

/**将2020-11-24 14:13:50转换成毫秒数*/
fun String.toTime(pattern: String = "yyyy-MM-dd HH:mm:ss"): Long {
    val format: SimpleDateFormat = SimpleDateFormat.getDateInstance() as SimpleDateFormat
    format.applyPattern(pattern)
    var time = 0L
    try {
        time = format.parse(this)?.time ?: 0
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return time
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

/**复制对象*/
fun <T : Any> T.copyTo(obj: T): T {
    BeanUtils.copyProperties(this, obj)
    return obj
}

/*----------------------------------------------------------------------------------*/

fun Collection<*>.lastIndex() = size - 1

/**从当前列表中, 随机获取指定数量的数据*/
fun <T> List<T>.randomList(count: Int): List<T> {
    val indexMap = hashMapOf<Int, String>()
    val result = mutableListOf<T>()
    val list = this
    if (list.size <= count) {
        //原始数量不够时
        result.addAll(list)
    } else {
        while (indexMap.size < count) {
            val randomIndex = nextInt(0, list.size)
            if (!indexMap.containsKey(randomIndex)) {
                //随机获取
                indexMap[randomIndex] = ""
                result.add(list[randomIndex])
            }
        }
    }
    return result
}

/*----------------------------------------------------------------------------------*/

fun <T> Optional<T>?.getOrNull(): T? {
    return if (this?.isPresent == true) {
        this.get()
    } else {
        null
    }
}