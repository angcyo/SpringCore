package com.angcyo.spring.security.jwt.provider

import com.angcyo.spring.security.entity.AuthEntity
import com.angcyo.spring.security.entity.toAuthorities
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.crypto.password.PasswordEncoder

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/11/07
 *
 * https://blog.csdn.net/qq_35915384/article/details/80227274
 */

class JwtAuthenticationProvider(val passwordEncoder: PasswordEncoder) : AuthenticationProvider {

    /**
     * 验证登录信息,若登陆成功,设置 Authentication
     */
    @Throws(AuthenticationException::class)
    override fun authenticate(authentication: Authentication): Authentication {
        val principal = authentication.principal
        val credentials = authentication.credentials
        if (principal is AuthEntity && credentials is String) {
            if (passwordEncoder.matches(credentials, principal.password)) {
                return UsernamePasswordAuthenticationToken(principal, credentials, principal.roles.toAuthorities())
            }
        }
        return authentication
    }

    /**
     * 当前 Provider 是否支持对该类型的凭证提供认证服务
     */
    override fun supports(authentication: Class<*>): Boolean {
        return UsernamePasswordAuthenticationToken::class.java
            .isAssignableFrom(authentication)
    }

    /* override fun supports(authentication: Class<*>): Boolean {
        return UsernamePasswordAuthenticationToken::class.java == authentication
    }*/
}