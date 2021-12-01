package com.angcyo.spring.security.jwt.provider

import com.angcyo.spring.base.beanOf
import com.angcyo.spring.base.servlet.request
import com.angcyo.spring.redis.Redis
import com.angcyo.spring.security.bean.*
import com.angcyo.spring.security.jwt.loginError
import com.angcyo.spring.security.jwt.token.ResponseAuthenticationToken
import com.angcyo.spring.security.service.AuthService
import com.angcyo.spring.security.service.codeKey
import com.angcyo.spring.security.service.currentClientUuid
import com.angcyo.spring.security.table.AccountTable
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder

/**
 * 基础的用户密码/验证码授权方式
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/05/28
 */

open class DefaultAuthenticationProvider : BaseTokenAuthenticationProvider() {

    /**开始授权*/
    override fun auth(authReqBean: AuthReqBean): Authentication? {

        //查询到的账号列表
        var accountList: List<AccountTable>? = null
        var result: Authentication? = null
        val authService = beanOf<AuthService>()
        val redis = beanOf<Redis>()
        val grantType = authReqBean.grantType?.lowercase()

        //-------------------------验证码检查---------------------------

        request()?.let {
            val codeKey = it.codeKey()
            //优先验证图形验证码, 如果有.  或者传入了图形验证码
            if (!authReqBean.imageCode.isNullOrEmpty() ||
                redis.hasKey(authService.imageCodeKey(codeKey, CodeType.Login.value))
            ) {
                if (authReqBean.imageCode?.lowercase() !=
                    authService.getImageCode(codeKey, CodeType.Login.value)?.lowercase()
                ) {
                    //如果获取了图形验证码, 但是不匹配
                    loginError("验证码不正确")
                }
            }
        }

        //-------------------------帐号检查---------------------------

        if (grantType == GrantType.Password.value) {
            //密码登录方式

            if (authReqBean.account.isNullOrEmpty() || authReqBean.password.isNullOrEmpty()) {
                throw  UsernameNotFoundException("账号或密码不正确")
            }

            //获取帐号
            accountList = authService.accountService.autoList(AccountQueryParam().apply {
                name = authReqBean.account
            })
        } else if (grantType == GrantType.Code.value) {
            //验证码登录方式

            val uuid = currentClientUuid()
            val account = authReqBean.account
            if (uuid.isNullOrEmpty()) {
                error("无效的客户端")
            }
            if (account.isNullOrEmpty()) {
                error("无效的账号")
            }

            val code = authService.getSendCode(uuid, account, CodeType.Login.value)?.lowercase()
            if (code == null || code != authReqBean.code?.lowercase()) {
                error("验证码不正确")
            }

            //获取帐号
            accountList = authService.accountService.autoList(AccountQueryParam().apply {
                name = authReqBean.account
            })
        }

        //------------------------用户判断------------------------------

        //帐号存在
        if (!accountList.isNullOrEmpty()) {
            if (accountList.isEmpty()) {
                throw  UsernameNotFoundException("账号或密码不正确")
            }

            val account = accountList.first()
            //通过帐号, 获取对应的用户
            val user = authService.userService.autoList(UserQueryParam().apply {
                id = account.userId
            }).firstOrNull() ?: throw  UsernameNotFoundException("账号不存在")

            //通过用户, 匹配对应的密码
            if ((user.state ?: 0) < 0) {
                throw  UsernameNotFoundException("账号不可用[${user.state}]")
            }

            var userDetail: UserDetail? = null

            //验证
            if (grantType == GrantType.Password.value) {
                //密码匹配通过, 颁发Token
                val passwordEncoder = beanOf<PasswordEncoder>()
                if (passwordEncoder.matches(authReqBean.password, user.password)) {
                    //密码对上了

                    userDetail = UserDetail().apply {
                        userTable = user
                    }
                } else {
                    throw  UsernameNotFoundException("账号或密码不正确")
                }
            } else if (grantType == GrantType.Code.value) {
                //验证码登录成功

                userDetail = UserDetail().apply {
                    userTable = user
                }
            }

            //返回
            if (userDetail != null) {
                //自动填充值
                authService.userService.autoFill(userDetail)
                //返回token
                result = ResponseAuthenticationToken(userDetail)
            }
        }

        return result
    }
}