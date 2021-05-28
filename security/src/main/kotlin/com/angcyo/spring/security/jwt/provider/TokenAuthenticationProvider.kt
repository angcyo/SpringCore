package com.angcyo.spring.security.jwt.provider

import com.angcyo.spring.security.bean.AuthReqBean
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.core.Authentication

/**
 * 基础的用户授权方式
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/05/28
 */

abstract class TokenAuthenticationProvider : AuthenticationProvider {

    override fun supports(authentication: Class<*>?): Boolean {
        if (authentication == null) {
            return false
        }
        return RequestAuthenticationToken::class.java
            .isAssignableFrom(authentication)
    }

    override fun authenticate(authentication: Authentication?): Authentication? {
        if (authentication is RequestAuthenticationToken) {
            return auth(authentication.authReqBean)
        }
        return null
    }

    /**授权*/
    abstract fun auth(authReqBean: AuthReqBean): Authentication?

}