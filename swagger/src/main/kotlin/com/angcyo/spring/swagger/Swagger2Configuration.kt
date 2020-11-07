package com.angcyo.spring.swagger

import com.angcyo.spring.swagger.Swagger3Configuration.Companion.SWAGGER_DES
import com.angcyo.spring.swagger.Swagger3Configuration.Companion.SWAGGER_LICENSES
import com.angcyo.spring.swagger.Swagger3Configuration.Companion.SWAGGER_SCAN_BASE_PACKAGE
import com.angcyo.spring.swagger.Swagger3Configuration.Companion.SWAGGER_TITLE
import com.angcyo.spring.swagger.Swagger3Configuration.Companion.VERSION
import org.springframework.context.annotation.Bean
import springfox.documentation.builders.ApiInfoBuilder
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.service.ApiInfo
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/11/07
 *
 * https://blog.csdn.net/shaixinxin/article/details/107622204
 */

/*@Configuration
@EnableSwagger2*/
@Deprecated("[Swagger3Configuration]")
class Swagger2Configuration {

    @Bean("Docket2")
    fun createRestApi(): Docket {
        return Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage(SWAGGER_SCAN_BASE_PACKAGE))
                .paths(PathSelectors.any()) // 可以根据url路径设置哪些请求加入文档，忽略哪些请求
                .build()
    }

    private fun apiInfo(): ApiInfo {
        return ApiInfoBuilder()
                .title(SWAGGER_TITLE) //设置文档的标题
                .description(SWAGGER_DES) // 设置文档的描述
                .version(VERSION) // 设置文档的版本信息-> 1.0.0 Version information
                .termsOfServiceUrl(SWAGGER_LICENSES) // 设置文档的License信息->1.3 License information
                .build()
    }
}