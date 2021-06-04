package com.angcyo.spring.redis

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.json.PackageVersion
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.node.ObjectNode
import java.io.IOException

/**
 * https://juejin.cn/post/6872636051237240846
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/06/04
 */
class CustomModule : SimpleModule(PackageVersion.VERSION) {

    companion object {
        val customSerializer = CustomSerializer()
    }

    init {
        addSerializer(ObjectNode::class.java, customSerializer)
    }

    class CustomSerializer : JsonSerializer<Any>() {
        @Throws(IOException::class)
        override fun serialize(value: Any, gen: JsonGenerator, serializers: SerializerProvider?) {
            if (value is ObjectNode) {
                gen.writeStartObject(this)
                gen.writeStringField("@class", value.javaClass.name) //加上@class信息
                val iterator: Iterator<Map.Entry<String, JsonNode>> = value.fields()
                while (iterator.hasNext()) {
                    val field: Map.Entry<String, JsonNode> = iterator.next()
                    val v: JsonNode = field.value
                    if (v.isArray && v.isEmpty(serializers)) {
                        continue
                    }
                    gen.writeFieldName(field.key)
                    v.serialize(gen, serializers)
                }
                gen.writeEndObject()
            }
        }
    }

}