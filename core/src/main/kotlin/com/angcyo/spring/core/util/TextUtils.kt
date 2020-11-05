package com.angcyo.spring.core.util

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/11/05
 */

object TextUtils {

    fun isEmpty(str: CharSequence?): Boolean {
        return str == null || str.isEmpty()
    }

    fun equals(a: CharSequence?, b: CharSequence?): Boolean {
        if (a === b) return true
        var length: Int = 0
        return if (a != null && b != null && a.length.also { length = it } == b.length) {
            if (a is String && b is String) {
                a == b
            } else {
                for (i in 0 until length) {
                    if (a[i] != b[i]) return false
                }
                true
            }
        } else false
    }
}