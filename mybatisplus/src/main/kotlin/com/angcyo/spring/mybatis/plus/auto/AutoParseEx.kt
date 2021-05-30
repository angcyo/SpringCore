package com.angcyo.spring.mybatis.plus.auto

import com.baomidou.mybatisplus.core.toolkit.ReflectionKit
import org.springframework.util.ReflectionUtils
import java.lang.reflect.Field

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/05/29
 */

/**快速获取注解类*/
inline fun <reified Auto : Annotation> Field.annotation(dsl: Auto.() -> Unit) {
    val auto = getDeclaredAnnotation(Auto::class.java)
    if (auto != null) {
        //isAccessible = true
        ReflectionUtils.makeAccessible(this)
        auto.dsl()
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
