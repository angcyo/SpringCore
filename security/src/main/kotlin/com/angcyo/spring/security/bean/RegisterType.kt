package com.angcyo.spring.security.bean

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/05/28
 */

/**注册类型*/
sealed class RegisterType(val value: String)
object AndroidType : RegisterType("android")
object IosType : RegisterType("ios")
object WebType : RegisterType("web")