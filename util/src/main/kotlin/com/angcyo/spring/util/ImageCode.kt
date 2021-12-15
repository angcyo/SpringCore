package com.angcyo.spring.util

import java.awt.Color
import java.awt.Font
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.util.*
import javax.imageio.ImageIO
import kotlin.random.Random.Default.nextInt


/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/11/08
 *
 * 图片验证码生成
 */

object ImageCode {

    private val FONT_TYPES =
        arrayOf("\u5b8b\u4f53", "\u65b0\u5b8b\u4f53", "\u9ed1\u4f53", "\u6977\u4f53", "\u96b6\u4e66")

    //验证码生成池
    val CODE_CHAR = "abcdefghijklmnpqrstuvwxyzABCDEFGHIJKLMNPQRSTUVWXYZ123456789"

    /**
     * https://github.com/lingd3/Captcha
     * 返回 code 和 图片
     * */
    fun generate(length: Int = 4, width: Int = 80, height: Int = 28): Pair<String, ByteArray> {
        val image = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
        val graphics: Graphics2D = image.graphics as Graphics2D

        //背景
        fillBackground(graphics, width, height);

        //字典池, 去掉难分辨的字符O0
        val ch = CODE_CHAR.toCharArray()
        val len = ch.size

        val textWidth = 15

        var index: Int
        val sb = StringBuffer()
        for (i in 0 until length) {
            index = nextInt(len)
            //设置RGB颜色算法参数
            graphics.color = Color(nextInt(88), nextInt(188), nextInt(255))
            /*Color(nextInt(50, 100),
            nextInt(50, 100),
            nextInt(50, 100))*/

            //设置字体大小，类型
            graphics.font = Font(FONT_TYPES[nextInt(FONT_TYPES.size)], Font.BOLD, 26)
            //设置x y 坐标
            val c = ch[index]
            graphics.drawString(
                c.toString(),
                textWidth * i + nextInt(5, 10),
                19 + nextInt(2, 10)
            )
            sb.append(c)
        }

        val code = sb.toString()
        val out = ByteArrayOutputStream()
        ImageIO.write(image, "JPG", out)

        return code to out.toByteArray()
    }

    /**创建一个验证码*/
    fun generateCode(length: Int = 4): String {
        //字典池, 去掉难分辨的字符O0
        val ch = CODE_CHAR.toCharArray()
        val len = ch.size

        var index: Int
        val sb = StringBuffer()
        for (i in 0 until length) {
            index = nextInt(len)
            val c = CODE_CHAR[index]
            sb.append(c)
        }
        return sb.toString()
    }

    /**
     * 设置背景颜色及大小，干扰线
     * https://blog.csdn.net/qq_37651267/article/details/99305573
     * @param graphics
     * @param width
     * @param height
     */
    private fun fillBackground(graphics: Graphics, width: Int, height: Int) {
        // 填充背景
        graphics.color = Color.WHITE
        //设置矩形坐标x y 为0
        graphics.fillRect(0, 0, width, height)

        // 加入干扰线条
        for (i in 0..7) {
            //设置随机颜色算法参数
            graphics.color = randomColor(40, 150)
            val random = Random()
            val x = random.nextInt(width)
            val y = random.nextInt(height)
            val x1 = random.nextInt(width)
            val y1 = random.nextInt(height)
            graphics.drawLine(x, y, x1, y1)
        }
    }

    /**
     * 设置字符颜色大小
     *
     * @param g
     * @param randomStr
     */
    private fun createCharacter(g: Graphics, randomStr: String) {
        val charArray = randomStr.toCharArray()
        for (i in charArray.indices) {
            //设置RGB颜色算法参数
            g.color = Color(
                nextInt(50, 100),
                nextInt(50, 100),
                nextInt(50, 100)
            )
            //设置字体大小，类型
            g.font = Font(FONT_TYPES[nextInt(FONT_TYPES.size)], Font.BOLD, 26)
            //设置x y 坐标
            g.drawString(charArray[i].toString(), 15 * i + 5, 19 + nextInt(8))
        }
    }
}