package com.angcyo.spring.swagger

import com.angcyo.spring.util.L
import com.angcyo.spring.util.getLocalHost
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.stereotype.Component

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/05/25
 */

@Component
class SwaggerDocLogRunner : ApplicationRunner, ApplicationContextAware {

    @Autowired
    lateinit var swaggerProperties: SwaggerProperties

    lateinit var context: ApplicationContext

    override fun run(args: ApplicationArguments?) {
        if (swaggerProperties.enable) {
            val port = (context as ConfigurableApplicationContext).environment.getProperty("server.port")
            val host = "http://${getLocalHost()}:${port}"
            L.w(buildString {
                append("Swagger 文档地址:")
                append("\n${host}/swagger-ui/index.html")
                append("\n${host}/doc.html")
            })
        }
    }

    override fun setApplicationContext(applicationContext: ApplicationContext) {
        context = applicationContext
    }
}