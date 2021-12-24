package com.angcyo.spring.base

import com.angcyo.spring.util.L
import org.springframework.beans.BeansException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.ApplicationArguments
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.stereotype.Component


/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/11/04
 *
 * [ApplicationContext]上下文感知
 */

@Component
class Base : ApplicationContextAware {

    companion object {

        /**保存应用程序上下文对象*/
        lateinit var applicationContext: ApplicationContext

        @Throws(BeansException::class)
        fun <T> getBean(name: String): T? {
            if (applicationContext.containsBean(name)) {
                return applicationContext.getBean(name) as T
            }
            return null
        }

        @Throws(BeansException::class)
        fun <T> getBean(requiredType: Class<T>): T {
            return applicationContext.getBean(requiredType)
        }
    }

    //https://www.cnblogs.com/yy3b2007com/p/8734486.html
    @Autowired
    lateinit var applicationArguments: ApplicationArguments

    /**[org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext]*/
    override fun setApplicationContext(applicationContext: ApplicationContext) {
        Base.applicationContext = applicationContext
        val activeProfiles = applicationContext.environment.activeProfiles
        //applicationContext.environment.getProperty("angcyo2")
        L.isDebug = activeProfiles.contains("dev") || activeProfiles.contains("pre")
    }
}

/**[ApplicationContext]*/
fun app() = Base.applicationContext