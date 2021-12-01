package com.angcyo.spring.security.jwt

import org.springframework.security.core.AuthenticationException

/**
 * 登录异常
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/11/02
 */

/**[com.angcyo.spring.security.jwt.IAuthorizationHandle.onDoUnsuccessfulAuthentication]*/
class LoginException(msg: String, cause: Throwable? = null) : AuthenticationException(msg, cause)

/**登录异常*/
inline fun loginError(message: Any, cause: Throwable? = null): Nothing = throw LoginException(message.toString(), cause)
