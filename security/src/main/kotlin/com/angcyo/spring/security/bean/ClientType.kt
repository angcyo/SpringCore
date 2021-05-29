package com.angcyo.spring.security.bean

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/05/28
 */

/**客户端的类型*/
sealed class ClientType(val value: String) {
    object Android : ClientType("android")
    object Ios : ClientType("ios")
    object Web : ClientType("web")
}