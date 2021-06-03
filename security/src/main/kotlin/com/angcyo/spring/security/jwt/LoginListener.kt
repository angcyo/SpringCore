package com.angcyo.spring.security.jwt

import com.angcyo.spring.security.bean.CodeType
import com.angcyo.spring.security.jwt.token.ResponseAuthenticationToken
import com.angcyo.spring.security.service.AuthService
import com.angcyo.spring.security.service.currentClientUuid
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationListener
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent
import org.springframework.stereotype.Component

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/06/03
 */

@Component
class LoginListener : ApplicationListener<InteractiveAuthenticationSuccessEvent> {

    @Autowired
    lateinit var authService: AuthService

    override fun onApplicationEvent(event: InteractiveAuthenticationSuccessEvent) {
        if (event.authentication is ResponseAuthenticationToken) {
            currentClientUuid()?.let {
                authService.clearImageCode(it, CodeType.Register.value)
                authService.clearImageCode(it, CodeType.Login.value)
            }
        }
    }
}