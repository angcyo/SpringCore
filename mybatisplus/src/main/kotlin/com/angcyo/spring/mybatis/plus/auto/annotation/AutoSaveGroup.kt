package com.angcyo.spring.mybatis.plus.auto.annotation

/**
 * 将多个查询条件, 进行分组处理
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/12/21
 */

@Target( AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class AutoSaveGroup(

    /**最终对应的字段*/
    val group: String = "",

    /**对应的子组*/
    val groups: Array<AutoSaveGroup> = [],

    /**组内每个条件 是否需要用 or 包裹*/
    val or: Boolean = false,
)