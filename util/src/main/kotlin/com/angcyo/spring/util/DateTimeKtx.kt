package com.angcyo.spring.util

import com.angcyo.spring.util.Constant.DEFAULT_DATE_TIME_FORMATTER
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/02/27
 */

/**https://blog.csdn.net/u014044812/article/details/79231738*/
/**秒数*/
fun LocalDateTime.toSecond(): Long = toEpochSecond(ZoneOffset.of("+8"))

/**毫秒数*/
fun LocalDateTime.toTime(): Long = toMillisecond()

fun LocalDateTime.toMillisecond(): Long = toInstant(ZoneOffset.of("+8")).toEpochMilli()

fun LocalDateTime.toDate(): Date = Date.from(toInstant(ZoneOffset.of("+8")))

/**毫秒转[LocalDateTime]*/
fun Long.toLocalDateTime(): LocalDateTime = Date(this).toInstant().atOffset(ZoneOffset.of("+8")).toLocalDateTime()

fun LocalDateTime.toPattern(pattern: String = DEFAULT_DATE_TIME_FORMATTER) = toTime(pattern)
