package com.angcyo.spring.app.third

import com.github.promeg.pinyinhelper.Pinyin

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/11/18
 *
 * https://github.com/promeG/TinyPinyin
 */

object PY {
}

/**将输入字符串转为拼音，转换过程中会使用之前设置的用户词典，以字符为单位插入分隔符. 小写*/
fun String.pinyin(separator: String = "") = Pinyin.toPinyin(this, separator).toLowerCase()

/**获取首字母*/
fun String.py(separator: String = ""): String {
    val result = mutableListOf<String>()
    toCharArray().forEach {
        result.add(it.toPinyin().toLowerCase().first().toString())
    }
    return result.joinToString(separator)
}

fun Char.toPinyin() = Pinyin.toPinyin(this)
fun Char.isChinese() = Pinyin.isChinese(this)