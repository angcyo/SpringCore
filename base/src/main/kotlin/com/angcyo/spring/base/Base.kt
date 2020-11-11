package com.angcyo.spring.base

import com.angcyo.spring.base.util.L
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

        const val DEFAULT_DATE_TIME_FORMATTER = "yyyy-MM-dd HH:mm:ss"

        /**保存应用程序上下文对象*/
        lateinit var applicationContext: ApplicationContext

        fun <T> getBean(name: String): T? {
            if (applicationContext.containsBean(name)) {
                return applicationContext.getBean(name) as T
            }
            return null
        }

        fun <T> getBean(requiredType: Class<T>): T? {
            return applicationContext.getBean(requiredType)
        }
    }

    /**[org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext]*/
    override fun setApplicationContext(applicationContext: ApplicationContext) {
        Base.applicationContext = applicationContext
        val activeProfiles = applicationContext.environment.activeProfiles
        L.isDebug = activeProfiles.contains("dev") || activeProfiles.contains("pre")
    }
}