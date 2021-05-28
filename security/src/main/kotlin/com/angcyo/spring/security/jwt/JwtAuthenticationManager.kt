package com.angcyo.spring.security.jwt

import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component

/**
 *
 * 自定义的授权管理, 用来分配不同的授权方式.
 *
 * 比如: 帐号密码登录/邮箱手机验证码登录/第三方授权登录等
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/05/28
 */

@Component
class JwtAuthenticationManager : AuthenticationManager {

    /**自定义的授权提供者*/
    var customAuthenticationProviderList = mutableListOf<AuthenticationProvider>()

    /**默认的授权提供者*/
    var defaultAuthenticationProviderList = mutableListOf<AuthenticationProvider>()

    override fun authenticate(authentication: Authentication?): Authentication? {
        var result: Authentication? = null

        //1:
        if (result == null) {
            for (provider in customAuthenticationProviderList) {
                if (provider.supports(authentication?.javaClass)) {
                    result = provider.authenticate(authentication)
                } else {
                    continue
                }
                if (result != null) {
                    break
                }
            }
        }

        //2:
        if (result == null) {
            for (provider in defaultAuthenticationProviderList) {
                if (provider.supports(authentication?.javaClass)) {
                    result = provider.authenticate(authentication)
                } else {
                    continue
                }
                if (result != null) {
                    break
                }
            }
        }

        return result ?: authentication
    }
}