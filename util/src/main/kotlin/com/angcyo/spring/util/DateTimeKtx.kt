package com.angcyo.spring.util

import com.angcyo.spring.util.Constant.DEFAULT_DATE_TIME_FORMATTER
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.temporal.Temporal
import java.time.temporal.TemporalAccessor
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

/**2020-11-05 15:07:16.265363*/
fun LocalDateTime.toTime(pattern: String = "yyyy-MM-dd HH:mm"): String {
    return format(pattern.toDateTimeFormatter())
}

fun String.toDateTimeFormatter() = DateTimeFormatter.ofPattern(this, Locale.CHINA)

fun String.parse(pattern: String): TemporalAccessor = pattern.toDateTimeFormatter().parse(this)

fun String.toLocalDateTime(pattern: String = DEFAULT_DATE_TIME_FORMATTER): LocalDateTime {
    val format: DateTimeFormatter = pattern.toDateTimeFormatter()
    return LocalDateTime.parse(this, format)
}

fun Temporal.toLocalDateTime() = when (this) {
    is LocalDateTime -> this
    is LocalDate -> LocalDateTime.of(this, LocalTime.of(0, 0, 0, 0))
    is LocalTime -> LocalDateTime.of(LocalDate.now(), this)
    else -> LocalDateTime.from(this)
}
