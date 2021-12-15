package com.angcyo.hutool

import cn.hutool.core.img.ImgUtil
import cn.hutool.core.io.FileUtil
import java.awt.Image
import java.awt.image.BufferedImage
import java.io.File

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/10/14
 */

fun String.toFile(): File = FileUtil.file(this)

/**保存图片到指定文件*/
fun Image.save(targetFile: File): File {
    ImgUtil.write(this, targetFile)
    return targetFile
}

/**[Image]转[BufferedImage]*/
fun Image.toBufferedImage(): BufferedImage {
    return ImgUtil.toBufferedImage(this)
}

/**[File]转[BufferedImage]*/
fun File.toBufferedImage(): BufferedImage {
    return ImgUtil.read(this)
}

/**[String]转[BufferedImage]*/
fun String.toBufferedImage(): BufferedImage {
    return toFile().toBufferedImage()
}
