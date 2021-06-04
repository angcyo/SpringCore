package com.angcyo.spring.security.jwt.token

import com.angcyo.spring.security.bean.UserDetail
import org.springframework.security.authentication.AbstractAuthenticationToken

/**
 * 授权成功后, 需要返回的Token
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/05/29
 */
class ResponseAuthenticationToken(val userDetail: UserDetail) : AbstractAuthenticationToken(null) {

    override fun getCredentials(): Any? {
        return null
    }

    override fun getPrincipal(): Any? {
        return null
    }
}