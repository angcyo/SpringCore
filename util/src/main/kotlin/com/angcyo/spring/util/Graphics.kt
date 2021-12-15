package com.angcyo.spring.util

import java.awt.*
import java.awt.image.BufferedImage

/**
 * java 绘制类
 * [java.awt.Graphics2D]
 *
 * 文本绘制时, 文本左下角为原点
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/12/15
 */

class Graphics(val width: Int, val height: Int, imageType: Int = BufferedImage.TYPE_INT_ARGB) {

    //图片数据对象
    val image: BufferedImage

    //绘图对象
    val graphics: Graphics2D

    //当前绘制到的位置
    var _left: Int = 0
    var _top: Int = 0

    val _defaultFont = defaultFont

    init {
        image = BufferedImage(width, height, imageType)
        graphics = image.graphics as Graphics2D

        graphics.font = _defaultFont
    }

    fun wrap(dsl: () -> Unit) {
        val font = graphics.font
        val color = graphics.color

        dsl()

        //恢复属性
        graphics.font = font
        graphics.color = color
    }

    fun font(size: Int = _defaultFont.size): Font {
        return Font(_defaultFont.name, _defaultFont.style, size)
    }

    fun fontMetrics(font: Font? = null): FontMetrics {
        return if (font == null) graphics.fontMetrics else graphics.getFontMetrics(font)
    }

    /**偏移位置*/
    fun offset(dx: Int = 0, dy: Int = 0) {
        _left += dx
        _top += dy
    }

    /**测量文本的宽度
     * 一个字母大约8px, 一个中文14px*/
    fun textWidth(str: String, font: Font? = null): Int {
        return fontMetrics(font).stringWidth(str)
    }

    /**文本的高度
     * 19px*/
    fun textHeight(font: Font? = null): Int {
        return fontMetrics(font).height
    }

    /**文本下沉的高度*/
    fun descent(font: Font? = null): Int {
        return fontMetrics(font).descent
    }

    /**根据配置, 绘制文本.
     * 以文本左上角为原点*/
    fun drawString(
        str: String,
        color: Color = graphics.color,
        size: Int = _defaultFont.size,
        gravity: Int = Gravity.CENTER_HORIZONTAL
    ) {
        val font = font(size)

        val textWidth = textWidth(str, font)
        val textHeight = textHeight(font)
        val left = _left
        val top = _top + textHeight
        val y = top - descent(font)

        val x = when (gravity) {
            Gravity.CENTER_HORIZONTAL -> (width - left) / 2 - textWidth / 2
            Gravity.RIGHT -> width - textWidth
            else -> left
        }

        wrap {
            graphics.color = color
            graphics.font = font
            graphics.drawString(str, x, y)
        }

        _left = x + textWidth
        _top += textHeight
    }

    /**绘制一个矩形*/
    fun drawRect(width: Int, height: Int) {
        graphics.drawRect(_left, _top, width, height)
        _left += width
        _top += height
    }

    /**圆角矩形*/
    fun drawRect(width: Int, height: Int, arcWidth: Int, arcHeight: Int) {
        graphics.drawRoundRect(_left, _top, width, height, arcWidth, arcHeight)
        _left += width
        _top += height
    }

    /**绘制一个填充矩形*/
    fun fillRect(width: Int, height: Int) {
        graphics.fillRect(_left, _top, width, height)
        _left += width
        _top += height
    }

    /**圆角矩形*/
    fun fillRect(width: Int, height: Int, arcWidth: Int, arcHeight: Int) {
        graphics.fillRoundRect(_left, _top, width, height, arcWidth, arcHeight)
        _left += width
        _top += height
    }

    /**绘制背景颜色*/
    fun drawBackground(color: Color = Color.WHITE) {
        wrap {
            graphics.color = color
            graphics.fillRect(0, 0, width, height)
        }
    }

    /**绘制图片*/
    fun drawImage(
        image: Image, imageWidth: Int, imageHeight: Int, gravity: Int = Gravity.CENTER_HORIZONTAL
    ) {

        val left = _left
        val top = _top

        val x = when (gravity) {
            Gravity.CENTER_HORIZONTAL -> (width - left) / 2 - imageWidth / 2
            Gravity.RIGHT -> width - imageWidth
            else -> left
        }
        graphics.drawImage(image, x, top, imageWidth, imageHeight, null) // 绘制切割后的图
        _left = x + imageWidth
        _top += imageHeight
    }

    /**新行开始绘制*/
    fun newRow() {
        _left = 0
    }

    /**新列开始绘制*/
    fun newColumn() {
        _top = 0
    }

    fun test(image: BufferedImage) {
        drawBackground(Color.WHITE)
        drawString("中国人angcyo", Color.RED)
        newRow()
        drawString("中国人angcyo", Color.BLUE, 22)
        newRow()
        drawImage(image, image.width, image.height)
        newRow()
        drawString("中国人angcyo", Color.GREEN, 30)
    }
}

//<editor-fold desc="静态测量方法">

var defaultFont = Font("微软雅黑", Font.PLAIN, 14)

fun font(size: Int = defaultFont.size): Font {
    return Font(defaultFont.name, defaultFont.style, size)
}

fun fontMetrics(font: Font? = null): FontMetrics {
    val image = BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB)
    val graphics = image.graphics as Graphics2D
    return if (font == null) graphics.fontMetrics else graphics.getFontMetrics(font)
}

/**测量文本的宽度
 * 一个字母大约8px, 一个中文14px*/
fun String.textWidth(size: Int = defaultFont.size): Int {
    return fontMetrics(font(size)).stringWidth(this)
}

/**文本的高度
 * 19px*/
fun textHeight(size: Int = defaultFont.size): Int {
    return fontMetrics(font(size)).height
}

//</editor-fold desc="静态测量方法">