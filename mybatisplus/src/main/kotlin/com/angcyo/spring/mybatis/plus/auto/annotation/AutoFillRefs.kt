package com.angcyo.spring.mybatis.plus.auto.annotation

/**
 * [AutoFillRef]
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2022-1-18
 */

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class AutoFillRefs(

    /**引用的位置*/
    val refs: Array<String> = []
)
