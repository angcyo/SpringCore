package com.angcyo.spring.mybatis.plus.auto.annotation

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/12/23
 */

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class AutoQueryGroup(
    val queries: Array<Group> = [],
)
