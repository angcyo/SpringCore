package com.angcyo.spring.mybatis.plus.auto.annotation

/**
 * 暂且只做标识使用, 用来标识当前的方法被自动填充引用[AutoFill],
 *
 * 修改方法的时候需要注意哦
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/12/24
 */

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class AutoFillRef(

    /**引用的位置*/
    val ref: String = ""
)
