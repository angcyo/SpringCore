package com.angcyo.spring.base

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

/**
 * 应用程序配置
 * [org.springframework.boot.autoconfigure.web.ServerProperties]
 *
 * https://hello.blog.csdn.net/article/details/104575745
 *
 * ```
 * //指定配置文件的地址
 * @PropertySource("classpath:application.properties")
 * //指定配置文件的前缀
 * ```
 *
 * bean name生成规则:
 * https://blog.csdn.net/weixin_33910385/article/details/89688284
 *
 * ```
 * val app: ApplicationProperties = getBean(ApplicationProperties::class.java)
 * val app: ApplicationProperties = getBean("applicationProperties") as ApplicationProperties
 * ```
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/05/25
 */

@Component
@ConfigurationProperties(prefix = "app")
class ApplicationProperties {

    /**项目应用名*/
    var name: String? = null

    /**应用构建时间*/
    var time: String? = nowTimeString()
}