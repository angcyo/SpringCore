package com.angcyo.spring.mybatis.plus.auto.annotation

/**
 * 标识当前属性将当做自动更新时的条件
 *  需要配合[com.angcyo.spring.mybatis.plus.auto.annotation.Query]使用.
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/06/02
 */

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class AutoUpdateBy
