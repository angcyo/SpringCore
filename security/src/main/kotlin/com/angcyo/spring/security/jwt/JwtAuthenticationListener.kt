package com.angcyo.spring.security.jwt

import com.angcyo.spring.base.servlet.request
import com.angcyo.spring.security.SecurityConstants
import com.angcyo.spring.security.bean.CodeType
import com.angcyo.spring.security.jwt.event.AuthenticationTokenEvent
import com.angcyo.spring.security.service.AuthService
import com.angcyo.spring.security.service.currentClientUuid
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationListener
import org.springframework.stereotype.Component

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/06/03
 */

@Component
class JwtAuthenticationListener : ApplicationListener<AuthenticationTokenEvent> {

    @Autowired
    lateinit var authService: AuthService

    override fun onApplicationEvent(event: AuthenticationTokenEvent) {
        currentClientUuid()?.let {
            if (request()?.servletPath != SecurityConstants.AUTH_REGISTER_URL) {
                authService.clearImageCode(it, CodeType.Register.value)
            }
            if (request()?.servletPath != SecurityConstants.AUTH_LOGIN_URL) {
                authService.clearImageCode(it, CodeType.Login.value)
            }
        }
    }
}