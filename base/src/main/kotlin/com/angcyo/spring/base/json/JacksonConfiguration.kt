package com.angcyo.spring.base.json

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule
import org.springframework.context.annotation.Configuration


/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/11/13
 *
 * https://stackoverflow.com/questions/9382094/jsonfilter-throws-jsonmappingexception-can-not-resolve-beanpropertyfilter
 *
 * https://blog.csdn.net/qq_34789577/article/details/109035014
 */

@Configuration
class JacksonConfiguration(objectMapper: ObjectMapper) {
    init {
        objectMapper.apply {
            setFilterProvider(SimpleFilterProvider().setFailOnUnknownId(false))
            registerModule(ParameterNamesModule())
            registerModule(Jdk8Module())
            registerModule(JavaTimeModule())
        }
    }
}