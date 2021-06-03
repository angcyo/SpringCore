package com.angcyo.spring.security.bean

/**
 * 验证码类型
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/06/03
 */
sealed class CodeType(val value: Int) {

    /**注册时的验证码类型*/
    object Register : CodeType(1)

    /**登录时的验证码类型*/
    object Login : CodeType(2)
}
