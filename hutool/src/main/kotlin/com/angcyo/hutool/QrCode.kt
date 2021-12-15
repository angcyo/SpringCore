package com.angcyo.hutool

import cn.hutool.core.img.ImgUtil
import cn.hutool.extra.qrcode.QrCodeUtil
import cn.hutool.extra.qrcode.QrConfig
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import java.awt.image.BufferedImage
import java.io.File

/**
 * 二维码工具类
 *
 * https://www.hutool.cn/docs/#/extra/%E4%BA%8C%E7%BB%B4%E7%A0%81%E5%B7%A5%E5%85%B7-QrCodeUtil
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/12/15
 */

/**二维码默认配置*/
fun qrConfig(): QrConfig {
    val logoSize = 60
    val size = 300
    return QrConfig(size, size).apply {
        //容错率
        errorCorrection = ErrorCorrectionLevel.H
        //logo的缩放比例
        ratio = logoSize / size
        //logo的路径
        //setImg()
    }
}

/**返回二维码图片
 * [BufferedImage]*/
fun String.toQrCodeImage(init: QrConfig.() -> Unit = {}): BufferedImage {
    return QrCodeUtil.generate(this, qrConfig().apply(init))
}

/**保存二维码图片到路径
 * [BufferedImage]*/
fun String.toQrCodeFile(targetFile: File, init: QrConfig.() -> Unit = {}): File {
    val image = toQrCodeImage(init)
    ImgUtil.write(image, targetFile)
    return targetFile
}



