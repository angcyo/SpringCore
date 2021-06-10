package com.angcyo.spring.mybatis.plus.auto.annotation

/**
 * 删除数据时, 自动检查指定的数据是否已存在
 * 不存在不能删除, 否则异常.
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/06/02
 */

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class AutoDeleteCheck(

    /**[com.angcyo.spring.mybatis.plus.auto.annotation.AutoCheck.checkNull]*/
    val checkNull: Boolean = true,

    /**[com.angcyo.spring.mybatis.plus.auto.annotation.AutoWhere.value]*/
    val value: WhereEnum = WhereEnum.eq,

    /**[com.angcyo.spring.mybatis.plus.auto.annotation.AutoWhere.column]*/
    val column: String = "",

    /**异常时的错误提示*/
    val error: String = ""
)