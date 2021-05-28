package com.angcyo.spring.security.jwt.provider

import com.angcyo.spring.security.bean.AuthReqBean
import org.springframework.security.authentication.AbstractAuthenticationToken

/**
 * 使用此令牌进行授权
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/05/28
 */
class RequestAuthenticationToken(val authReqBean: AuthReqBean) : AbstractAuthenticationToken(null) {

    override fun getCredentials(): Any? {
        return null
    }

    override fun getPrincipal(): Any? {
        return null
    }
}