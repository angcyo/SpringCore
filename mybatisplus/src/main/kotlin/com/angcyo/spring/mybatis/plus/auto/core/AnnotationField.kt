package com.angcyo.spring.mybatis.plus.auto.core

import java.lang.reflect.Field

/**
 * 注解字段
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/12/24
 */
data class AnnotationField<A : Annotation>(val obj: Any, val field: Field, val annotation: A)