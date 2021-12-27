package com.angcyo.spring.util

import java.awt.Font
import java.awt.FontMetrics
import java.awt.Graphics2D
import java.awt.image.BufferedImage

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/10/27
 */

/**默认的字体*/
val DEFAULT_FONT = Font("微软雅黑", Font.PLAIN, 16)

val DEFAULT_GRAPHICS: Graphics2D
    get() = BufferedImage(0, 0, BufferedImage.TYPE_INT_ARGB).graphics as Graphics2D

/**测量文本的宽度*/
fun String.textWidth(font: Font = DEFAULT_FONT): Int {
//    val metrics: FontDesignMetrics = FontDesignMetrics.getMetrics(font)
//    var width = 0
//
//    for (i in indices) {
//        width += metrics.charWidth(get(i))
//    }
//    return width
    val metrics: FontMetrics = DEFAULT_GRAPHICS.getFontMetrics(font)
    return metrics.stringWidth(this)
}

/**测量文本的高度*/
fun textHeight(font: Font = DEFAULT_FONT): Int {
    val metrics: FontMetrics = DEFAULT_GRAPHICS.getFontMetrics(font)
    return metrics.height
}