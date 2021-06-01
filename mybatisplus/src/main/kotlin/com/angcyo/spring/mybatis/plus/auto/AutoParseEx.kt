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

/**是否有指定的注解*/
inline fun <reified Auto : Annotation> Any.haveAnnotation(): Boolean {
    var have = false
    for (field in ReflectionKit.getFieldList(this.javaClass)) {
        val annotation = field.annotation<Auto>()
        if (annotation != null && field.get(this) != null) {
            have = true
        }
        if (have) {
            break
        }
    }
    return have
}

/**
 * 从一个对象中, 获取指定的成员对象
 */
fun Any?.getMember(member: String): Any? {
    return this?.run { this.getMember(this.javaClass, member) }
}

fun Any?.getMember(
    cls: Class<*>,
    member: String
): Any? {
    var result: Any? = null
    try {
        var cl: Class<*>? = cls
        while (cl != null) {
            try {
                val memberField = cl.getDeclaredField(member)
                //memberField.isAccessible = true
                ReflectionUtils.makeAccessible(memberField)
                result = memberField[this]
                return result
            } catch (e: NoSuchFieldException) {
                cl = cl.superclass
            }
        }
    } catch (e: Exception) {
        //L.i("错误:" + cls.getSimpleName() + " ->" + e.getMessage());
    }
    return result
}

fun Any.isList() = if (this is Field) {
    type.isAssignableFrom(List::class.java)
} else {
    javaClass.isAssignableFrom(List::class.java)
}