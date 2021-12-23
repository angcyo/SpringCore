package com.angcyo.spring.mybatis.plus.auto.annotation

import com.angcyo.spring.mybatis.plus.auto.AutoType

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/12/23
 */

@Target(AnnotationTarget.TYPE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class Group(

    /**自动查询的语句类型*/
    val type: AutoType = AutoType.QUERY,

    /**跳过查询字段为空时的sql*/
    val jumpEmpty: Boolean = false,

    /**注解中, 不能嵌套自己, 也不能相互依赖.
     * 这里使用字符串解析
     *
     * (g1)|(g2)
     * g1&g2
     * (g1 & g2) | (g1 & g3)
     * */
    val pattern: String = "",
)
