package com.angcyo.spring.base.json

import com.angcyo.spring.base.json.JacksonEx.mapper
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/11/10
 *
 * https://www.cnblogs.com/lone5wolf/p/10940869.html
 */

object JacksonEx {
    val mapper = ObjectMapper().apply {
        //registerModule(LocalDateTimeSerializer(DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_FORMATTER), Locale.CHINA))
        //registerModule(LocalDateTimeDeserializer())
        registerModule(JavaTimeModule())
    }
}

/**任意对象, 转成json字符串*/
fun Any?.toJackson(): String? {
    return this?.run {
        try {
            mapper.writeValueAsString(this)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

fun <T> String?.fromJackson(clazz: Class<T>): T? {
    return this?.run {
        try {
            mapper.readValue<T>(this, clazz)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

