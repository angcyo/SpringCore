package com.angcyo.spring.base.json

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider
import org.springframework.context.annotation.Configuration


/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/11/13
 *
 * https://stackoverflow.com/questions/9382094/jsonfilter-throws-jsonmappingexception-can-not-resolve-beanpropertyfilter
 */

@Configuration
class JacksonConfiguration(objectMapper: ObjectMapper) {
    init {
        objectMapper.setFilterProvider(SimpleFilterProvider().setFailOnUnknownId(false))
    }
}