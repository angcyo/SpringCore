package com.angcyo.spring.util

import java.util.*

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/12/08
 */

var chars: Array<String> = arrayOf(
    "a", "b", "c", "d", "e", "f",
    "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s",
    "t", "u", "v", "w", "x", "y", "z", "0", "1", "2", "3", "4", "5",
    "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", "I",
    "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
    "W", "X", "Y", "Z"
)

/**生成短的uuid*/
fun generateShortUuid(length: Int = 8): String {
    val shortBuffer = StringBuffer()
    val uuid: String = UUID.randomUUID().toString().replace("-", "") //32位
    for (i in 0 until length) {
        val ii = i % 8
        val str = uuid.substring(ii * 4, ii * 4 + 4)
        val x = str.toInt(16)
        shortBuffer.append(chars[x % 0x3E])
    }
    return shortBuffer.toString()
}