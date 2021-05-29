package com.angcyo.spring.security.bean

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/05/29
 */

/**授权的类型*/
sealed class GrantType(val value: String) {

    /**使用密码的方式登录*/
    object Password : GrantType("password")

    /**使用验证码登录*/
    object Code : GrantType("code")
}
