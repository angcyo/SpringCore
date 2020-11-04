package com.angcyo.spring.core

import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.stereotype.Component

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/11/04
 */

@Component
class Core : ApplicationContextAware {

    companion object {
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