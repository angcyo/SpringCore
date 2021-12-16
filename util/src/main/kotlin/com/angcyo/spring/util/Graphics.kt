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

        /*val ge = GraphicsEnvironment.getLocalGraphicsEnvironment()
        val allFonts = ge.allFonts
        val fontFamilyNames = ge.availableFontFamilyNames*/

        graphics.font = _defaultFont
        graphics.color = "#040404".toAwtColor()
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

    /**自动换行绘制多行文本*/
    fun drawMultiString(
        str: String,
        color: Color = graphics.color,
        size: Int = _defaultFont.size,
        marginLeft: Int = 0,
        marginRight: Int = 0,
        gravity: Int = Gravity.CENTER_HORIZONTAL
    ) {
        val list = str.textLineList(width - marginLeft - marginRight, size)
        list.forEachIndexed { index, s ->
            _left += marginLeft
            drawString(s, color, size, gravity)
            if (list.lastIndex != index) {
                newRow()
            }
        }
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
        image: Image,
        imageWidth: Int,
        imageHeight: Int,
        gravity: Int = Gravity.CENTER_HORIZONTAL
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
}

//<editor-fold desc="静态测量方法">

var defaultFont = Font("Default"/*"微软雅黑"*/, Font.PLAIN, 14)

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

/**自动换行文本*/
fun String.textLineList(width: Int, size: Int = defaultFont.size): List<String> {
    val result = mutableListOf<String>()
    getListText(fontMetrics(font(size)), this, width, result)
    return result
}

/**
 * 递归 切割字符串
 * @param fg
 * @param text
 * @param widthLength
 * @param result
 */
private fun getListText(fg: FontMetrics, text: String, widthLength: Int, result: MutableList<String>) {
    var _text = text
    val ba = _text
    var b = true
    var i = 1
    while (b) {
        if (fg.stringWidth(_text) > widthLength) {
            _text = _text.substring(0, _text.length - 1)
            i++
        } else {
            b = false
        }
    }
    if (i != 1) {
        result.add(ba.substring(0, ba.length - i))
        getListText(fg, ba.substring(ba.length - i), widthLength, result)
    } else {
        result.add(_text)
    }
}

//</editor-fold desc="静态测量方法">