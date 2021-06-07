package com.angcyo.spring.swagger

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

/**
 * 配置属性
 * https://blog.csdn.net/wangzhihao1994/article/details/108408420
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/05/22
 */

@Configuration
@ConfigurationProperties("swagger")
//@PropertySource(value = ["classpath:application.properties"], encoding = "utf-8")
class SwaggerProperties {

    /**是否开启swagger，生产环境一般关闭，所以这里定义一个变量*/
    var enable: Boolean = true

    /**项目应用名*/
    var applicationName: String? = null

    /**项目版本信息*/
    var applicationVersion: String? = null

    /**项目描述信息*/
    var applicationDes: String? = null

    /**声明全局参数配置, 放在请求头中
     * [key] 表示key
     * [value] 表示key的描述
     * 只能是[String]类型*/
    var header: Map<String, String>? = null

}