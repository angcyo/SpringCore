package com.angcyo.spring.mybatis.plus.auto

import com.angcyo.spring.mybatis.plus.auto.extension.AutoParseException
import com.baomidou.mybatisplus.core.toolkit.ReflectionKit
import org.springframework.util.ReflectionUtils
import java.lang.reflect.AnnotatedElement
import java.lang.reflect.Field
import java.lang.reflect.Method

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/05/29
 */

inline fun Class<*>.eachField(each: (Field) -> Unit) {
    for (field in ReflectionKit.getFieldList(this)) {
        each(field)
    }
}

/**快速获取注解类*/
fun <Auto : Annotation> AnnotatedElement.annotation(
    annotationClass: Class<Auto>,
    dsl: Auto.() -> Unit = {}
): Auto? {
    val auto = getDeclaredAnnotation(annotationClass)
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

/**获取对象中所有指定注解的字段*/
inline fun <reified Auto : Annotation> Any.annotations(checkNullValue: Boolean = false): List<Field> {
    val result = mutableListOf<Field>()
    for (field in ReflectionKit.getFieldList(this.javaClass)) {
        val annotation = field.annotation<Auto>()
        if (annotation != null) {
            if (checkNullValue) {
                if (field.get(this) != null) {
                    result.add(field)
                }
            } else {
                result.add(field)
            }
        }
    }
    return result
}

/**是否有指定的注解*/
inline fun <reified Auto : Annotation> Any.haveAnnotation(checkNullValue: Boolean = false): Boolean {
    var have = false
    for (field in ReflectionKit.getFieldList(this.javaClass)) {
        val annotation = field.annotation<Auto>()
        if (annotation != null) {
            have = if (checkNullValue) {
                field.get(this) != null
            } else {
                true
            }
        }
        if (have) {
            break
        }
    }
    return have
}

/**反射获取去对象的方法*/
fun Any.getMethod(methodName: String, clz: Class<*> = javaClass): Method? {
    var searchType: Class<*>? = clz
    while (searchType != null) {
        val methods = if (searchType.isInterface) searchType.methods else ReflectionUtils.getDeclaredMethods(searchType)
        for (method in methods) {
            if (methodName == method.name) {
                return method
            }
        }
        searchType = searchType.superclass
    }
    return null
}

/**反射调用对象的方法*/
fun Any.invokeMethod(methodName: String, vararg args: Any?): Any? = invokeMethodClass(methodName, javaClass, *args)

fun Any.invokeMethodClass(methodName: String, clz: Class<*> = javaClass, vararg args: Any?): Any? =
    ReflectionUtils.invokeMethod(getMethod(methodName, clz)!!, this, *args)

/**
 * 从一个对象中, 获取指定的成员对象
 */
fun Any?.getMember(member: String): Any? {
    return ReflectionKit.getFieldValue(this, member)
}

/***/
fun Any.setMember(member: String, value: Any?): Boolean {
    val cls: Class<*> = this.javaClass
    val fieldMaps = ReflectionKit.getFieldMap(cls)
    return try {
        val field = fieldMaps[member]
        field!!.isAccessible = true
        field.set(this, value)
        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}

fun Any.isList() = isClass(List::class.java)

fun <T> Any.isClass(cls: Class<T>) = if (this is Field) {
    type.isAssignableFrom(cls)
} else {
    javaClass.isAssignableFrom(cls)
}

inline fun parseError(message: Any, cause: Throwable? = null): Nothing =
    throw AutoParseException(message.toString(), cause)

