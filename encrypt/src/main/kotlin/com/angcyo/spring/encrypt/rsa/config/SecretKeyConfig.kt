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
    var privateKey: String? = null
    var publicKey: String? = null
    var charset = "UTF-8"
    var isOpen = true
    var isShowLog = false
}