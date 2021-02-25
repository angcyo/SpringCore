package com.angcyo.spring.log.core

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/11/05
 *
 * 定义一个注解, 表示需要记录log
 */

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class RecordLog(
    val des: String = ""
)