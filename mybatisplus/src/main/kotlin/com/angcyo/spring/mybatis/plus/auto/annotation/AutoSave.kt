package com.angcyo.spring.mybatis.plus.auto.annotation

/**
 * 保存数据时, 自动检查指定的数据是否已存在
 * 不存在才保存, 否则异常.
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/06/02
 */

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class AutoSave(

    /**[com.angcyo.spring.mybatis.plus.auto.annotation.AutoQuery.value]*/
    val value: WhereEnum = WhereEnum.eq,

    /**[com.angcyo.spring.mybatis.plus.auto.annotation.AutoQuery.column]*/
    val column: String = "",

    /**[com.angcyo.spring.mybatis.plus.auto.annotation.AutoCheck.checkNull]
     *[com.angcyo.spring.mybatis.plus.auto.annotation.AutoQuery.checkNull]*/
    val checkNull: Boolean = true,

    /**空异常时的错误提示
     *[com.angcyo.spring.mybatis.plus.auto.annotation.AutoQuery.nullError]
     */
    val nullError: String = ""
)