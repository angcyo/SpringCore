package com.angcyo.spring.base

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/06/15
 */

/**性别*/
sealed class SexType(val value: Int) {
    /**男*/
    object Male : SexType(1)

    /**女*/
    object Female : SexType(2)
}