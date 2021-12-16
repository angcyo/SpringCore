package com.angcyo.spring.util

import java.awt.image.BufferedImage
import java.awt.image.RenderedImage
import java.io.ByteArrayOutputStream
import java.io.IOException
import javax.imageio.ImageIO

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/12/16
 */

/**[BufferedImage]转[ByteArray]*/
fun RenderedImage.toByteArray(formatName: String = "png"): ByteArray {
    val out = ByteArrayOutputStream()
    try {
        ImageIO.write(this, formatName, out);
    } catch (e: IOException) {
        //log.error(e.getMessage());
    }
    return out.toByteArray()
}
