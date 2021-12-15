package com.angcyo.spring.app

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/06/07
 */

@Component
@ConfigurationProperties(prefix = "file")
class FileProperties {

    /**文件默认保存路径*/
    var uploadDir: String? = null

    /**生成的office文档保存路径*/
    var officeDir: String? = null

    /**公共文件保存地址*/
    var filesDir: String? = null
}