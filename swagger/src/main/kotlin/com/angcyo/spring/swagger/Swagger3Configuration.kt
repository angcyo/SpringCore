package com.angcyo.spring.swagger

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import springfox.documentation.builders.ApiInfoBuilder
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.service.*
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spi.service.contexts.SecurityContext
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
 *
 * //springfox.documentation.swagger-ui.enabled=true
 * https://www.cnblogs.com/architectforest/p/13470170.html
 */

@Configuration
class Swagger3Configuration {

    companion object {
        //api接口包扫描路径
        var SWAGGER_SCAN_BASE_PACKAGE = "com.angcyo.spring"
        var VERSION = "1.0.0"

        var SWAGGER_TITLE = "欢迎访问接口文档"
        var SWAGGER_DES = "Swagger3 接口文档"
        var SWAGGER_LICENSES = "https://www.github.com/angcyo"
    }

    @Autowired
    lateinit var swaggerProperties: SwaggerProperties

    /**全局请求参数配置*/
    fun globalRequestParameters(): List<RequestParameter> {
        val result = mutableListOf<RequestParameter>()

        //2021-10-12 每个接口都显示这个东西, 有点浪费界面资源, 统一放在描述中显示
        /*swaggerProperties.header?.forEach { entry ->
            val key = entry.key
            val value = entry.value

            result.add(
                RequestParameterBuilder().apply {
                    name(key)
                    description(value)
                    required(false)
                    hidden(true) //隐藏
                    `in`(ParameterType.HEADER)
                    query {
                        it.model {
                            it.scalarModel(ScalarType.STRING)
                        }
                    }
                }.build()
            )
        }*/

        return result
    }

    /**https://blog.csdn.net/H_233/article/details/103129250*/
    @Bean
    fun createRestApi(): Docket {
        return Docket(DocumentationType.OAS_30)
            //.directModelSubstitute(LocalDateTime::class.java, String::class.java)
            //.directModelSubstitute(LocalDate::class.java, String::class.java)
            //.directModelSubstitute(LocalTime::class.java, String::class.java)
            //.directModelSubstitute(ZonedDateTime::class.java, String::class.java)
            .groupName("default")
            .enable(swaggerProperties.enable)
            .apiInfo(apiInfo())
            .select()
            //.apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation::class.java))
            // 包扫描范围（对指定的包下进行扫描，如果标注有相关swagger注解，则生成相应文档）
            .apis(RequestHandlerSelectors.basePackage(SWAGGER_SCAN_BASE_PACKAGE))
            //.paths(PathSelectors.regex("/public.*"))
            // 过滤掉哪些path不用生成swagger
            .paths(PathSelectors.any()) // 可以根据url路径设置哪些请求加入文档，忽略哪些请求
            .build()
            // 忽略该参数在swagger上的显示
            .ignoredParameterTypes()
            // 配置swagger接口安全校验规则
            .securitySchemes(securitySchemes())
            // 配置swagger接口安全校验上下文中的信息（包含安全权限与安全校验生效的接口路径）
            .securityContexts(securityContexts())
            //全局参数
            .globalRequestParameters(globalRequestParameters())
    }

    private fun apiInfo(): ApiInfo {

        //des 支持md文档格式
        val des = buildString {
            appendln(swaggerProperties.des ?: SWAGGER_DES)
            swaggerProperties.header?.apply {
                appendln("   `统一接口请求头->`")
                appendLine("```")
                forEach { entry ->
                    val key = entry.key
                    val value = entry.value
                    appendln("  [$key]:$value")
                }
                appendLine("```")
            }
        }

        return ApiInfoBuilder()
            .title(swaggerProperties.title ?: SWAGGER_TITLE) //设置文档的标题
            .description(des) // 设置文档的描述
            .version(swaggerProperties.version ?: VERSION) // 设置文档的版本信息-> 1.0.0 Version information
            .contact(Contact("angcyo", "https://www.angcyo.com", "angcyo@126.com"))
            //.license()
            //.licenseUrl()
            .termsOfServiceUrl(swaggerProperties.url ?: SWAGGER_LICENSES) // 设置文档的License信息->1.3 License information
            .build()
    }

    private fun securitySchemes(): List<ApiKey> {
        return listOf(ApiKey("Authorization", "Authorization", "header"))
    }

    private fun securityContexts(): List<SecurityContext> {
        return listOf(
            SecurityContext.builder()
                .securityReferences(defaultAuth())
                .forPaths(PathSelectors.any())
                .build()
        )
    }

    private fun defaultAuth(): List<SecurityReference> {
        return listOf(SecurityReference("Authorization", arrayOf(AuthorizationScope("global", "accessEverything"))))
    }
}