package com.angcyo.spring.security.jwt.provider

import com.angcyo.spring.security.bean.AuthReqBean
import org.springframework.security.core.Authentication

/**
 * 基础的用户密码授权方式
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/05/28
 */

class UsernamePasswordAuthenticationProvider : TokenAuthenticationProvider() {
    override fun auth(authReqBean: AuthReqBean): Authentication? {
        return null
    }
}