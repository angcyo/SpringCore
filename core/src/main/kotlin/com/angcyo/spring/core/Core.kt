package com.angcyo.spring.core

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
class Core : ApplicationContextAware {

    companion object {

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

    override fun setApplicationContext(applicationContext: ApplicationContext) {
        Core.applicationContext = applicationContext
    }
}