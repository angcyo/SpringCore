package com.angcyo.spring.security.controller

import com.angcyo.spring.util.L
import io.swagger.annotations.ApiModelProperty
import org.springframework.validation.Errors
import org.springframework.validation.Validator

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/11/09
 */

class CustomValidator : Validator {
    override fun supports(clazz: Class<*>): Boolean {
        return clazz.name.startsWith("com.angcyo.spring")
    }

    override fun validate(target: Any, errors: Errors) {
        val clazz = target.javaClass
        for (field in clazz.declaredFields) {
            for (annotation in field.annotations) {
                L.w(annotation)
                L.w(annotation is ApiModelProperty)
            }
        }
    }
}