package com.angcyo.java.mail

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021-7-7
 */

@Configuration
@ComponentScan(basePackages = ["com.angcyo.java.mail"])
@EnableConfigurationProperties
class MailAutoConfiguration
