package com.angcyo.spring.security.jwt

import org.springframework.security.core.AuthenticationException

/**
 * 登录异常
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/11/02
 */
class LoginException(msg: String) : AuthenticationException(msg)