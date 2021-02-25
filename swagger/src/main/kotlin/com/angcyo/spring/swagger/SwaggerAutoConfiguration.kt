package com.angcyo.spring.swagger

import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import springfox.documentation.oas.annotations.EnableOpenApi

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/11/07
 *
 * https://www.jianshu.com/p/c79f6a14f6c9
 * http://localhost:888/swagger-ui/
 *
 * [@ApiIgnore] 可以忽略文档生成
 */

@Configuration
@EnableOpenApi
@ComponentScan(basePackages = ["com.angcyo.spring.swagger"])
class SwaggerAutoConfiguration
