package com.angcyo.spring.base.util

import java.util.regex.Pattern

/**
 *
 *
 * 驼峰转下划线，下划线转驼峰工具类
 *
 * @author aLiang
 * @date 2020年11月12日 下午6:36:10
 */
object HumpUtils {
    val HUMP_PATTERN = Pattern.compile("[A-Z]")
    private val LINE_PATTERN = Pattern.compile("_(\\w)")

    /**
     * 如果开头是下划线，则从第二个字符开始剪切
     */
    const val START_INDEX = 1

    /**
     * 驼峰转下划线
     *
     * @param str
     * @return
     */
    fun humpToLine(str: String): String {
        val matcher = HUMP_PATTERN.matcher(str)
        val sb = StringBuffer()
        while (matcher.find()) {
            matcher.appendReplacement(sb, "_" + matcher.group(0).toLowerCase())
        }
        matcher.appendTail(sb)
        var result = sb.toString()
        if (result.startsWith("_")) {
            result = result.substring(START_INDEX)
        }
        return result
    }

    /**
     * 下划线转驼峰
     *
     * @param str
     * @return String
     */
    fun lineToHump(str: String): String {
        val matcher = LINE_PATTERN.matcher(str)
        val sb = StringBuffer()
        while (matcher.find()) {
            matcher.appendReplacement(sb, matcher.group(1).toUpperCase())
        }
        matcher.appendTail(sb)
        return sb.toString()
    }
}