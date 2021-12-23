package com.angcyo.spring.mybatis.plus.auto.annotation

/**
 * 自动验证数据的有效性
 * [com.angcyo.spring.mybatis.plus.auto.core.AutoParse.parseCheck]
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/06/02
 */

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class AutoCheck(

    /**是否要检查数据是否为空, 为空报异常*/
    val checkNull: Boolean = true,

    /**是否要检查字符串为空, 或者集合为空*/
    val checkEmpty: Boolean = false,

    /**检查字符串/组的长度*/
    val checkLength: Boolean = false,

    /**检查数字的大小*/
    val checkSize: Boolean = false,

    /**值:限制字符串的最小长度, 数字的最小值, 数组的长度*/
    val min: Long = Long.MIN_VALUE,

    /**值:限制字符串的最大长度, 数字的最大值, 数组的长度*/
    val max: Long = Long.MAX_VALUE,

    /**异常时的错误提示, 为空时自动根据异常类型提示*/
    val error: String = ""
)