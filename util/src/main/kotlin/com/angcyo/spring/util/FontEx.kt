package com.angcyo.spring.util

import sun.font.FontDesignMetrics
import java.awt.Font

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/10/27
 */

/**默认的字体*/
val DEFAULT_FONT = Font("微软雅黑", Font.PLAIN, 16)

/**测量文本的宽度*/
fun String.textWidth(font: Font = DEFAULT_FONT): Int {
    val metrics: FontDesignMetrics = FontDesignMetrics.getMetrics(font)
    var width = 0

    for (i in 0 until length) {
        width += metrics.charWidth(get(i))
    }
    return width
}

/**测量文本的高度*/
fun String.textHeight(font: Font = DEFAULT_FONT): Int {
    val metrics: FontDesignMetrics = FontDesignMetrics.getMetrics(font)
    return metrics.height
}