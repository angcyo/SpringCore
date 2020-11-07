package com.angcyo.spring.security.controller

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/11/07
 *
 * 注册需要用的参数
 */

data class RegisterBean(
        var username: String? = null,
        var password: String? = null,
)