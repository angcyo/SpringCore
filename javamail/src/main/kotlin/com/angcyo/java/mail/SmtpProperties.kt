package com.angcyo.java.mail

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/07/07
 */

@Component
@ConfigurationProperties(prefix = "smtp")
class SmtpProperties {

    var host: String? = "smtp.126.com"

    var port: Int = 25

    var username: String? = null

    var passwrod: String? = null
}