package com.angcyo.spring.security.jwt.event

import com.angcyo.spring.security.jwt.token.ResponseAuthenticationToken
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent

/**
 * 授权成功后的通知事件
 * [com.angcyo.spring.security.jwt.IAuthorizationHandle.onDoSuccessfulAuthentication]
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/06/08
 */
class AuthenticationTokenEvent(val token: ResponseAuthenticationToken) :
    InteractiveAuthenticationSuccessEvent(token, ResponseAuthenticationToken::class.java) {
    override fun getSource(): ResponseAuthenticationToken {
        return super.getSource() as ResponseAuthenticationToken
    }
}