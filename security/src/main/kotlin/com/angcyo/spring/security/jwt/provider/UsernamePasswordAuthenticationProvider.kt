package com.angcyo.spring.security.jwt.provider

import com.angcyo.spring.base.beanOf
import com.angcyo.spring.security.bean.AccountQueryParam
import com.angcyo.spring.security.bean.AuthReqBean
import com.angcyo.spring.security.bean.GrantType
import com.angcyo.spring.security.bean.UserQueryParam
import com.angcyo.spring.security.jwt.token.ResponseAuthenticationToken
import com.angcyo.spring.security.service.AuthService
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder

/**
 * 基础的用户密码授权方式
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/05/28
 */

class UsernamePasswordAuthenticationProvider : BaseTokenAuthenticationProvider() {
    override fun auth(authReqBean: AuthReqBean): Authentication? {

        if (authReqBean.grantType == GrantType.Password.value) {
            if (authReqBean.account.isNullOrEmpty() || authReqBean.password.isNullOrEmpty()) {
                throw  UsernameNotFoundException("账号或密码不正确")
            }

            val authService = beanOf<AuthService>()
            //获取帐号
            val accountList = authService.accountService.list(AccountQueryParam().apply {
                name = authReqBean.account
            })
            if (accountList.isEmpty()) {
                throw  UsernameNotFoundException("账号或密码不正确")
            }

            val account = accountList.first()
            //通过帐号, 获取对应的用户
            val user = authService.userService.list(UserQueryParam().apply {
                id = account.userId
            }).firstOrNull() ?: throw  UsernameNotFoundException("账号不存在")

            //通过用户, 匹配对应的密码
            if (user.state ?: 0 < 0) {
                throw  UsernameNotFoundException("账号不可用[${user.state}]")
            }

            //密码匹配通过, 颁发Token
            val passwordEncoder = beanOf<PasswordEncoder>()
            if (passwordEncoder.matches(authReqBean.password, user.password)) {
                //密码对上了
                return ResponseAuthenticationToken(user)
            } else {
                throw  UsernameNotFoundException("账号或密码不正确")
            }
        }

        return null
    }
}