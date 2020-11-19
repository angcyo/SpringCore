package com.angcyo.spring.base.data

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/11/19
 *
 * 性别
 */
sealed class Sex(val value: Int, val label: String)

object Secret : Sex(-1, "保密")
object Man : Sex(1, "男")
object Woman : Sex(0, "女")
