package com.angcyo.spring.swagger

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

/**
 * 配置属性
 * https://blog.csdn.net/wangzhihao1994/article/details/108408420
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/05/22
 */

@Component
@ConfigurationProperties("swagger")
class SwaggerProperties {

    /**是否开启swagger，生产环境一般关闭，所以这里定义一个变量*/
    var enable: Boolean = true

    /**项目应用名*/
    var applicationName: String? = null

    /**项目版本信息*/
    var applicationVersion: String? = null

    /**项目描述信息*/
    var applicationDescription: String? = null

}