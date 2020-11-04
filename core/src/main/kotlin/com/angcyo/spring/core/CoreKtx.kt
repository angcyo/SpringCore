package com.angcyo.spring.core

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/11/04
 */

/**
 * 根据Bean的名字, 扩展获取Bean对象
 * */
inline fun <reified T> String.bean() = Core.getBean<T>(this)

/**
 * 根据Class, 扩展获取Bean对象
 * */
fun <T> Class<T>.bean() = Core.getBean(this)