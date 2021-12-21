package com.angcyo.spring.mybatis.plus.auto.annotation

import io.swagger.v3.oas.annotations.extensions.Extension

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

    /**[com.angcyo.spring.mybatis.plus.auto.annotation.AutoQuery.where]*/
    val where: WhereEnum = WhereEnum.eq,

    /**[where] 是否需要用 or 包裹*/
    val isOr: Boolean = false,

    /**可以指定对应的查询列名
     * [com.angcyo.spring.mybatis.plus.auto.annotation.AutoQuery.column]*/
    val column: String = "",

    /**[com.angcyo.spring.mybatis.plus.auto.annotation.AutoCheck.checkNull]
     *[com.angcyo.spring.mybatis.plus.auto.annotation.AutoQuery.checkNull]*/
    val checkNull: Boolean = true,

    /**有错误时, 但是不提示错误. */
    val ignoreError: Boolean = false,

    /**数据已存在时的错误提示
     *[com.angcyo.spring.mybatis.plus.auto.annotation.AutoQuery.nullError]
     */
    val existError: String = "",

    /**当当前属性为null时, 需要设置的默认值, 暂且只支持简单数据类型*/
    val defaultValue: String = "",

    /**所在组的名称, 一个字段支持在多个组中*/
    val groups: Array<String> = []
)