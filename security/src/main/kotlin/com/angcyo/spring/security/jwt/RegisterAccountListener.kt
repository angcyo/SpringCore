package com.angcyo.spring.security.jwt

import com.angcyo.spring.security.bean.CodeType
import com.angcyo.spring.security.jwt.event.RegisterAccountEvent
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
class RegisterAccountListener : ApplicationListener<RegisterAccountEvent> {

    @Autowired
    lateinit var authService: AuthService

    override fun onApplicationEvent(event: RegisterAccountEvent) {
        currentClientUuid()?.apply {
            authService.clearImageCode(this, CodeType.Register.value)
            val target = event.bean.account
            target?.let {
                authService.clearSendCode(this, target, CodeType.Register.value)
            }
        }
    }
}