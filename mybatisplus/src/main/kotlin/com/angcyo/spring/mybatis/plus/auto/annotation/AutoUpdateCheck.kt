package com.angcyo.spring.mybatis.plus.auto.annotation

/**
 * 更新数据时, 自动检查指定的数据是否已存在
 * 不存在不能更新, 并异常.
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/06/02
 */

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class AutoUpdateCheck(

    /**[com.angcyo.spring.mybatis.plus.auto.annotation.AutoCheck.checkNull]*/
    val checkNull: Boolean = true,

    /**[com.angcyo.spring.mybatis.plus.auto.annotation.AutoWhere.value]*/
    val value: WhereEnum = WhereEnum.eq,

    /**[com.angcyo.spring.mybatis.plus.auto.annotation.AutoWhere.column]*/
    val column: String = "",

    /**异常时的错误提示*/
    val error: String = ""
)