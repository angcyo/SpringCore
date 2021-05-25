package com.angcyo.spring.util

import kotlin.math.floor

/**
 * Created by yu on 2017/8/2.
 * https://github.com/shalousun/ApplicationPower
 */
object PrettyMemoryUtil {

    private const val UNIT = 1024

    /**
     * @param byteSize 字节
     * @return size of memory
     */
    fun prettyByteSize(byteSize: Long): String {
        var size = 1.0 * byteSize
        var type = "B"
        if (floor(size / UNIT).toInt() <= 0) { //不足1KB
            type = "B"
            return format(size, type)
        }
        size /= UNIT
        if (floor(size / UNIT).toInt() <= 0) { //不足1MB
            type = "KB"
            return format(size, type)
        }
        size /= UNIT
        if (floor(size / UNIT).toInt() <= 0) { //不足1GB
            type = "MB"
            return format(size, type)
        }
        size /= UNIT
        if (floor(size / UNIT).toInt() <= 0) { //不足1TB
            type = "GB"
            return format(size, type)
        }
        size /= UNIT
        if (floor(size / UNIT).toInt() <= 0) { //不足1PB
            type = "TB"
            return format(size, type)
        }
        size /= UNIT
        if (floor(size / UNIT).toInt() <= 0) {
            type = "PB"
            return format(size, type)
        }
        return ">PB"
    }

    private fun format(size: Double, type: String): String {
        var precision = 0
        precision = when {
            size * 1000 % 10 > 0 -> 3
            size * 100 % 10 > 0 -> 2
            size * 10 % 10 > 0 -> 1
            else -> 0
        }
        val formatStr = "%." + precision + "f"
        return when (type) {
            "KB" -> String.format(formatStr, size) + "KB"
            "MB" -> String.format(formatStr, size) + "MB"
            "GB" -> String.format(formatStr, size) + "GB"
            "TB" -> String.format(formatStr, size) + "TB"
            "PB" -> String.format(formatStr, size) + "PB"
            else -> String.format(formatStr, size) + "B"
        }
    }
}