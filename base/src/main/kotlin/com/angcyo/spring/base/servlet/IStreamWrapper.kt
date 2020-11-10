package com.angcyo.spring.base.servlet

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/11/10
 */
interface IStreamWrapper {
    fun toByteArray(needRead: Boolean = false): ByteArray?
}