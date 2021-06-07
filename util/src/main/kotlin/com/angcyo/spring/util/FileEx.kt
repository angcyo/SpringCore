package com.angcyo.spring.util

import org.springframework.util.DigestUtils
import org.springframework.util.ResourceUtils
import java.io.FileInputStream
import java.io.InputStream

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/06/07
 */

/**获取文件的md5值*/
fun String.fileMd5(): String? {
    var result: String? = null
    try {
        val inputStream = FileInputStream(ResourceUtils.getFile(this))
        result = DigestUtils.md5DigestAsHex(inputStream)
    } catch (e: Exception) {
        e.printStackTrace()
        L.e(e.message)
    }
    return result
}

fun InputStream.md5() = try {
    DigestUtils.md5DigestAsHex(this)
} catch (e: Exception) {
    e.printStackTrace()
    L.e(e.message)
    null
}