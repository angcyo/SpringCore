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
}