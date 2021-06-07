package com.angcyo.spring.base.aspect

/**
 *
 * 标记需要打印方法调用耗时
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/06/02
 */

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class LogMethodTime(
    /**描述信息*/
    val des: String = ""
)
