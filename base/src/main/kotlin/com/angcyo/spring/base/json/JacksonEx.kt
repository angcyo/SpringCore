package com.angcyo.spring.base.json

import com.angcyo.spring.base.json.JacksonEx.ignorePropertyMapper
import com.angcyo.spring.base.json.JacksonEx.mapper
import com.angcyo.spring.base.json.JacksonEx.onlyPropertyMapper
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/11/10
 *
 * https://www.cnblogs.com/lone5wolf/p/10940869.html
 */

object JacksonEx {

    const val DEFAULT_FILTER = "JacksonFilter"

    val mapper = ObjectMapper().apply {
        //registerModule(LocalDateTimeSerializer(DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_FORMATTER), Locale.CHINA))
        //registerModule(LocalDateTimeDeserializer())
        registerModule(JavaTimeModule())

        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    }

    /**忽略指定的属性, 不序列化*/
    fun ignorePropertyMapper(vararg property: String) = ObjectMapper().apply {
        registerModule(JavaTimeModule())
        setFilterProvider(SimpleFilterProvider().apply {
            addFilter(DEFAULT_FILTER, SimpleBeanPropertyFilter.serializeAllExcept(*property))
        })
    }

    /**只序列化指定的属性字段*/
    fun onlyPropertyMapper(vararg property: String) = ObjectMapper().apply {
        registerModule(JavaTimeModule())
        setFilterProvider(SimpleFilterProvider().apply {
            addFilter(DEFAULT_FILTER, SimpleBeanPropertyFilter.filterOutAllExcept(*property))
        })
    }
}

/**任意对象, 转成json字符串
 * [@JsonFilter(JacksonEx.DEFAULT_FILTER)]*/
fun Any?.toJackson(ignoreProperty: Array<out String>? = null, onlyProperty: Array<out String>? = null): String? {
    return this?.run {
        try {
            (if (!onlyProperty.isNullOrEmpty()) {
                onlyPropertyMapper(*onlyProperty)
            } else if (!ignoreProperty.isNullOrEmpty()) {
                ignorePropertyMapper(*ignoreProperty)
            } else {
                mapper
            }).writeValueAsString(this)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

/**[@JsonFilter(JacksonEx.DEFAULT_FILTER)]*/
fun Any?.toJacksonIgnore(vararg property: String) = toJackson(ignoreProperty = property)

/**[@JsonFilter(JacksonEx.DEFAULT_FILTER)]*/
fun Any?.toJacksonOnly(vararg property: String) = toJackson(onlyProperty = property)

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

