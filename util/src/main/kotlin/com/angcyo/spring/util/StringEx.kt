package com.angcyo.spring.util

import java.net.URLDecoder
import java.net.URLEncoder
import java.util.regex.Pattern
import kotlin.reflect.KCallable

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/02/27
 */

fun String.encode(enc: String = "UTF-8"): String = URLEncoder.encode(this, enc)

fun String.decode(enc: String = "UTF-8"): String = URLDecoder.decode(this, enc)

/**驼峰转下划线*/
fun String.humpToLine() = HumpUtils.humpToLine(this)

/**下划线转驼峰*/
fun String.lineToHump() = HumpUtils.lineToHump(this)

fun KCallable<*>.queryColumn() = name.queryColumn()

fun String.queryColumn() = humpToLine()

/**05840C88275286B3A75A833A09306425 32位*/
fun String?.md5(): String? {
    return this?.toByteArray(Charsets.UTF_8)?.encrypt()?.toHexString()
}

/**如果是负数, 则反向取值
 * 如果大于size, 则取模*/
fun String.getSafe(index: Int): Char? {
    val newIndex = if (index < 0) {
        length + index
    } else {
        index
    }
    val size = length
    if (newIndex >= size) {
        return getOrNull(newIndex % size)
    }
    return getOrNull(newIndex)
}

fun CharSequence?.patternList(
    regex: String?,
    orNoFind: String? = null /*未找到时, 默认*/
): MutableList<String> {
    return this.patternList(regex?.toPattern(), orNoFind)
}

/**获取字符串中所有匹配的数据(部分匹配), 更像是contains的关系*/
fun CharSequence?.patternList(
    pattern: Pattern?,
    orNoFind: String? = null /*未找到时, 默认*/
): MutableList<String> {
    val result = mutableListOf<String>()
    if (this == null) {
        return result
    }
    pattern?.let {
        val matcher = it.matcher(this)
        var isFind = false
        while (matcher.find()) {
            isFind = true
            result.add(matcher.group())
        }
        if (!isFind && orNoFind != null) {
            result.add(orNoFind)
        }
    }
    return result
}

/**[CharSequence]中是否包含指定[text]
 * [match] 是否是全匹配, 否则包含即可*/
fun CharSequence?.have(text: CharSequence?, match: Boolean = false): Boolean {
    if (text == null) {
        return false
    }
    if (this == null) {
        return false
    }
    if (this.str() == text.str()) {
        return true
    }
    return try {
        val regex = text.toString().toRegex()
        if (match) {
            this.matches(regex)
        } else {
            this.contains(regex)
        }
    } catch (e: Exception) {
        //java.util.regex.PatternSyntaxException: Missing closing bracket in character class near index 19
        e.printStackTrace()
        false
    }
}