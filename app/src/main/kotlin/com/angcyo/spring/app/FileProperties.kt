package com.angcyo.spring.app

import com.angcyo.spring.security.SecurityConfiguration
import com.angcyo.spring.util.connectSeparatorBoth
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct

/**
 * [com.angcyo.spring.app.service.FilePathService]
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/06/07
 */

@Component
@ConfigurationProperties(prefix = "file")
class FileProperties {

    /**文件默认保存路径*/
    var uploadDir: String? = "uploads"

    /**生成的office文档保存路径*/
    var officeDir: String? = "office"

    /**公共文件保存地址, 无需Token*/
    var filesDir: String? = "files"

    /**开放的文件下载目录, 无需Token*/
    var downloadsDir: String? = "downloads"

    @PostConstruct
    fun init() {
        SecurityConfiguration.configSecurityWhiteList {
            filesDir?.connectSeparatorBoth()?.let { add("${it}**") }
            downloadsDir?.connectSeparatorBoth()?.let { add("${it}**") }
        }
    }
}