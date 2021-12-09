package com.angcyo.spring.base.data

import com.angcyo.spring.util.getSexByIdCard

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/06/15
 */

/**性别*/
sealed class SexType(val value: Int, val label: String) {
    /**男*/
    object Male : SexType(1, "男")

    /**女*/
    object Female : SexType(2, "女")

    /**保密*/
    object Secret : SexType(0, "保密")

    object Unknown : SexType(-1, "未知")
}

fun Int.toSex() = when (this) {
    SexType.Male.value -> SexType.Male
    SexType.Female.value -> SexType.Female
    SexType.Secret.value -> SexType.Secret
    else -> SexType.Unknown
}

fun SexType.toStr() = this.label

/**[com.angcyo.spring.base.data.SexType]*/
fun String.getSexTypeByIdCard() = when (getSexByIdCard()) {
    1 -> SexType.Male.value
    2 -> SexType.Female.value
    0 -> SexType.Secret.value
    else -> SexType.Unknown.value
}