package com.angcyo.spring.security.jwt.provider

import org.springframework.security.authentication.AuthenticationProvider

/**
 * 定义一个授权提供器接口[AuthenticationProvider]
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/05/27
 */

interface IAuthenticationProvider {

    /**自定义需要额外的[AuthenticationProvider]*/
    fun getAuthenticationProvider()
}