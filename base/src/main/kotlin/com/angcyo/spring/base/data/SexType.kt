package com.angcyo.spring.base.data

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

    object Unknown : SexType(-1)
}

fun Int.toSex() = when (this) {
    SexType.Male.value -> SexType.Male
    SexType.Female.value -> SexType.Female
    else -> SexType.Unknown
}

fun SexType.toStr() = when (this) {
    SexType.Male -> "男"
    SexType.Female -> "女"
    SexType.Unknown -> "未知"
}