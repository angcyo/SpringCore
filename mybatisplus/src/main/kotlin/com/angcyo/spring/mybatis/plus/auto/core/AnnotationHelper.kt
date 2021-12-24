package com.angcyo.spring.mybatis.plus.auto.core

import com.baomidou.mybatisplus.core.toolkit.ReflectionKit
import org.springframework.core.annotation.AnnotationUtils
import org.springframework.util.ReflectionUtils

/**
 * 注解解析助手
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/12/24
 */
object AnnotationHelper {

    /**获取对象[obj], 声明了指定注解的字段集合.
     * 支持注解上的注解
     * */
    fun <A : Annotation> parseAnnotations(obj: Any, annotationClass: Class<A>): List<AnnotationField<A>> {
        val result = mutableListOf<AnnotationField<A>>()

        ReflectionKit.getFieldList(obj.javaClass).forEach { field ->
            ReflectionUtils.makeAccessible(field)
            val annotation = AnnotationUtils.findAnnotation(field, annotationClass)
            if (annotation != null) {
                result.add(AnnotationField(obj, field, annotation))
            }
        }

        return result
    }
}