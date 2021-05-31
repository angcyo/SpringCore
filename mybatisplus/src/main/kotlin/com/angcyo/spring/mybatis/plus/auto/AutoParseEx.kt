package com.angcyo.spring.mybatis.plus.auto

import com.baomidou.mybatisplus.core.toolkit.ReflectionKit
import org.springframework.util.ReflectionUtils
import java.lang.reflect.AnnotatedElement
import java.lang.reflect.Field

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/05/29
 */

/**快速获取注解类*/
inline fun <reified Auto : Annotation> AnnotatedElement.annotation(dsl: Auto.() -> Unit = {}): Auto? {
    val auto = getDeclaredAnnotation(Auto::class.java)
    return if (auto != null) {
        //isAccessible = true
        if (this is Field) {
            ReflectionUtils.makeAccessible(this)
        }
        auto.dsl()
        auto
    } else {
        null
    }
}

/**枚举对象中所有包含指定注解的字段*/
inline fun <reified Auto : Annotation> Any.eachAnnotation(dsl: Auto.(field: Field) -> Unit) {
    ReflectionKit.getFieldList(this.javaClass).forEach { field ->
        field.annotation<Auto> {
            this.dsl(field)
        }
    }
}
