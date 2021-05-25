package com.angcyo.spring.base

import java.util.*


/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/11/04
 */

/**
 * 根据Bean的名字, 扩展获取Bean对象
 * */
inline fun <reified T> String.bean() = Base.getBean<T>(this)

/**
 * 根据Class, 扩展获取Bean对象
 * */
fun <T> Class<T>.bean() = Base.getBean(this)

/**获取[application.properties]中, 配置的值*/
fun String.propertyValue() = Base.applicationContext.environment.getProperty(this)