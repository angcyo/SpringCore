package com.angcyo.spring.base.data

import com.angcyo.spring.util.getSexByIdCard

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

/**[com.angcyo.spring.base.data.SexType]*/
fun String.getSexTypeByIdCard() = when (getSexByIdCard()) {
    1 -> SexType.Male.value
    0 -> SexType.Female.value
    else -> SexType.Unknown.value
}