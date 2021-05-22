package com.angcyo.spring.swagger

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import springfox.documentation.builders.ApiInfoBuilder
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.service.ApiInfo
import springfox.documentation.service.Contact
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/11/07
 *
 * Swagger2
 * https://blog.csdn.net/shaixinxin/article/details/107622204
 *
 * Swagger3
 * https://blog.csdn.net/weixin_43740223/article/details/108491386
 * https://blog.csdn.net/wangzhihao1994/article/details/108408420
 */

@Configuration
class Swagger3Configuration {

    companion object {
        //api接口包扫描路径
        val SWAGGER_SCAN_BASE_PACKAGE = "com.angcyo"
        val VERSION = "1.0.0"

        var SWAGGER_TITLE = "Swagger2"
        var SWAGGER_DES = "Swagger2 接口文档"
        var SWAGGER_LICENSES = "http://www.baidu.com"
    }

    @Autowired
    lateinit var swaggerProperties: SwaggerProperties

    @Bean
    fun createRestApi(): Docket {
        return Docket(DocumentationType.OAS_30)
            .enable(swaggerProperties.enable)
            .apiInfo(apiInfo())
            .select()
            //.apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation::class.java))
            .apis(RequestHandlerSelectors.basePackage("com.angcyo.spring"))
            //.paths(PathSelectors.regex("/public.*"))
            .paths(PathSelectors.any()) // 可以根据url路径设置哪些请求加入文档，忽略哪些请求
            .build()
    }

    private fun apiInfo(): ApiInfo {
        return ApiInfoBuilder()
            .title(swaggerProperties.applicationName ?: SWAGGER_TITLE) //设置文档的标题
            .description(swaggerProperties.applicationDescription ?: SWAGGER_DES) // 设置文档的描述
            .version(swaggerProperties.applicationVersion ?: VERSION) // 设置文档的版本信息-> 1.0.0 Version information
            .contact(Contact("angcyo", "https://www.angcyo.com", "angcyo@126.com"))
            .termsOfServiceUrl(SWAGGER_LICENSES) // 设置文档的License信息->1.3 License information
            .build()
    }
}