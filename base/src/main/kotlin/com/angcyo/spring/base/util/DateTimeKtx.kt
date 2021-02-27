package com.angcyo.spring.base.util

import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/02/27
 */


/**https://blog.csdn.net/u014044812/article/details/79231738*/
fun LocalDateTime.toSecond(): Long = LocalDateTime.now().toEpochSecond(ZoneOffset.of("+8"))

/**毫秒数*/
fun LocalDateTime.toTime(): Long = LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli()

fun LocalDateTime.toDate(): Date = Date.from(this.toInstant(ZoneOffset.of("+8")))

/**毫秒转[LocalDateTime]*/
fun Long.toLocalDateTime(): LocalDateTime = Date(this).toInstant().atOffset(ZoneOffset.of("+8")).toLocalDateTime()