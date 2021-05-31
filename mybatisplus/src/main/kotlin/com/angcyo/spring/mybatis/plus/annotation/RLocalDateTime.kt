package com.angcyo.spring.mybatis.plus.annotation

import com.angcyo.spring.base.Base
import com.fasterxml.jackson.annotation.JsonFormat
import org.springframework.format.annotation.DateTimeFormat

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/11/05
 */

@JsonFormat(pattern = Base.DEFAULT_DATE_TIME_FORMATTER, timezone = "GMT+8")
@DateTimeFormat(pattern = Base.DEFAULT_DATE_TIME_FORMATTER)
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.TYPE, AnnotationTarget.FIELD, AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY)
annotation class RLocalDateTime
