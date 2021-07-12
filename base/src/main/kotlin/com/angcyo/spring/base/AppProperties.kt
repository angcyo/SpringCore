package com.angcyo.spring.base

import com.angcyo.spring.swagger.SwaggerProperties
import com.angcyo.spring.util.nowTimeString
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct

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
 * val app: AppProperties = getBean(AppProperties::class.java)
 * val app: AppProperties = getBean("appProperties") as AppProperties
 * ```
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/05/25
 */

@Component
@ConfigurationProperties(prefix = "app")
class AppProperties {

    /**项目应用名*/
    var name: String? = null

    /**项目全名*/
    var fullName: String? = null

    /**应用构建时间*/
    var time: String? = nowTimeString()

    /**是否允许多端登录, 否则统一时间只能一个设备登录
     * [com.angcyo.spring.security.jwt.JwtLoginFilter.successfulAuthentication]*/
    var multiLogin: Boolean = true

    /**
     * 是否激活uri权限控制
     * [com.angcyo.spring.security.jwt.JwtPermissionListener]
     * */
    var enablePermission: Boolean = false

    /**默认token有效时长, 秒*/
    var tokenTime: Long = 60 * 60 * 24L

    /**默认验证码登录有效时长, 秒*/
    var codeTime: Long = 5 * 60

    /**redis 缓存数据的时长, 秒*/
    var dataCacheTime: Long = 24 * 60 * 60 //24小时

    //------------------------swagger-------------------

    @Autowired
    lateinit var swaggerProperties: SwaggerProperties

    @PostConstruct
    fun init() {
        name?.let {
            swaggerProperties.title = "欢迎访问%s接口文档".format(it)
        }
    }

    /**分配一个缓存key*/
    fun key(key: String): String {
        return "${name ?: "Default"}.${key}"
    }
}