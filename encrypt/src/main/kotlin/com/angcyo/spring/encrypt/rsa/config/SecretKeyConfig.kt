package com.angcyo.spring.encrypt.rsa.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

/**
 * Author:Bobby
 * DateTime:2019/4/9
 */
@ConfigurationProperties(prefix = "rsa.encrypt")
@Configuration
class SecretKeyConfig {

    /**私钥, 用于解密*/
    var privateKey: String? = null

    /**公钥, 用于加密*/
    var publicKey: String? = null

    /**字符集*/
    var charset = "UTF-8"

    /**激活加密*/
    var isOpen = true

    /**显示日志*/
    var isShowLog = false

    /**服务器与客户端的时间间隔, 间隔太长不允许请求, 秒
     * 默认十分钟*/
    var securityTimeGap: Long = 10 * 60

    /**字段key*/
    var securityCodeKey: String = "sign"

    /**安全码, 不能超过13位*/
    var securityCode: String = "angcyo"
}