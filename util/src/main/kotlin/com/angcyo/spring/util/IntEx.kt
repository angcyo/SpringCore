package com.angcyo.spring.util

import java.math.BigDecimal

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/03/01
 */

fun String?.toBigLongOrNull(): Long? {
    return this?.toLongOrNull() ?: try {
        BigDecimal(this).toLong()
    } catch (e: Exception) {
        null
    }
}