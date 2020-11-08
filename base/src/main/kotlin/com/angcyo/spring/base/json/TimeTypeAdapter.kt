package com.angcyo.spring.base.json

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.TypeAdapter
import com.google.gson.TypeAdapterFactory
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import java.text.ParseException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/11/04
 *
 * [com.google.gson.internal.bind.DateTypeAdapter]
 * [com.google.gson.internal.bind.TimeTypeAdapter]
 */

class LocalDateTimeTypeAdapter : TypeAdapter<LocalDateTime>() {
    private val format: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    companion object {
        val FACTORY: TypeAdapterFactory = object : TypeAdapterFactory {
            override fun <T> create(gson: Gson?, typeToken: TypeToken<T>): TypeAdapter<T>? {
                return if (typeToken.rawType === LocalDateTime::class.java) LocalDateTimeTypeAdapter() as TypeAdapter<T> else null
            }
        }
    }

    @Synchronized
    override fun read(`in`: JsonReader): LocalDateTime? {
        if (`in`.peek() === JsonToken.NULL) {
            `in`.nextNull()
            return null
        }
        return try {
            LocalDateTime.parse(`in`.nextString(), format)
        } catch (e: ParseException) {
            throw JsonSyntaxException(e)
        }
    }

    @Synchronized
    override fun write(out: JsonWriter, value: LocalDateTime?) {
        out.value(if (value == null) null else format.format(value))
    }
}