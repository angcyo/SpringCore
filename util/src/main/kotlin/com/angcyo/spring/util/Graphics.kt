package com.angcyo.spring.util

import com.angcyo.spring.util.Graphics.Companion.defaultFont
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

    companion object {

        var defaultFont = Font("Default"/*"Default" "微软雅黑"*/, Font.PLAIN, 14)

        //字体优先选择, 需要在系统中安装字体
        val priorityFamilyNames = listOf(
            "JetBrains Mono",
            "微软雅黑",
            "Microsoft YaHei UI",
            "SansSerif",
            "Dialog",
            "Monospaced"
        ) // "DejaVu Sans Mono",

        init {
            val ge = GraphicsEnvironment.getLocalGraphicsEnvironment()
            //val allFonts = ge.allFonts
            val fontFamilyNames = ge.availableFontFamilyNames

            //Window
            //Arial,Arial Black,Bahnschrift,Calibri,Calibri Light,Cambria,Cambria Math,Candara,Candara Light,Comic Sans MS,Consolas,Constantia,Corbel,Corbel Light,Courier New,
            // DejaVu Sans Mono,Dialog,DialogInput,Ebrima,Franklin Gothic Medium,Gabriola,Gadugi,Georgia,HoloLens MDL2 Assets,Impact,Ink Free,Javanese Text,
            // jdFontAwesome,jdFontCustom,jdIcoFont,jdIcoMoonFree,jdiconfontA,jdiconfontB,jdiconfontC,jdiconfontD,JdIonicons,Leelawadee UI,Leelawadee UI Semilight,Lucida Bright,
            // Lucida Console,Lucida Sans,Lucida Sans Typewriter,Lucida Sans Unicode,Malgun Gothic,Malgun Gothic Semilight,Marlett,Microsoft Himalaya,Microsoft JhengHei,
            // Microsoft JhengHei Light,Microsoft JhengHei UI,Microsoft JhengHei UI Light,Microsoft New Tai Lue,Microsoft PhagsPa,Microsoft Sans Serif,
            // Microsoft Tai Le,Microsoft YaHei UI,Microsoft YaHei UI Light,Microsoft Yi Baiti,MingLiU-ExtB,MingLiU_HKSCS-ExtB,Mongolian Baiti,Monospaced,MS Gothic,MS PGothic,
            // MS UI Gothic,MT Extra,MV Boli,Myanmar Text,Nirmala UI,Nirmala UI Semilight,Palatino Linotype,PMingLiU-ExtB,SansSerif,Segoe MDL2 Assets,Segoe Print,
            // Segoe Script,Segoe UI,Segoe UI Black,Segoe UI Emoji,Segoe UI Historic,Segoe UI Light,Segoe UI Semibold,Segoe UI Semilight,Segoe UI Symbol,Serif,
            // SimSun-ExtB,Sitka Banner,Sitka Display,Sitka Heading,Sitka Small,Sitka Subheading,Sitka Text,Sylfaen,Symbol,Tahoma,TeamViewer15,Times New Roman,Trebuchet MS,
            // Verdana,Webdings,Wingdings,Yu Gothic,Yu Gothic Light,Yu Gothic Medium,Yu Gothic UI,Yu Gothic UI Light,Yu Gothic UI Semibold,Yu Gothic UI Semilight,
            // 仿宋,宋体,微软雅黑,微软雅黑 Light,新宋体,楷体,等线,等线 Light,黑体

            //CentOS
            //Bitstream Charter,Cantarell,Courier 10 Pitch,Cursor,DejaVu Sans Mono,Dialog,DialogInput,Monospaced,SansSerif,Serif,Utopia
            L.i(fontFamilyNames.joinToString(","))

            //筛选字体
            val name = fontFamilyNames.find { priorityFamilyNames.contains(it) } ?: "Default"
            defaultFont = Font(name, defaultFont.style, defaultFont.size)
        }
    }

    //图片数据对象
    val image: BufferedImage

    //绘图对象
    val graphics: Graphics2D

    //当前绘制到的位置
    var _left: Int = 0
    var _top: Int = 0

    var _defaultFont = defaultFont

    init {
        image = BufferedImage(width, height, imageType)
        graphics = image.graphics as Graphics2D

        graphics.font = _defaultFont
        graphics.color = "#222222".toAwtColor()
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