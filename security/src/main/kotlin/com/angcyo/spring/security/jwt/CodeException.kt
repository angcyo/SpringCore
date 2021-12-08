package com.angcyo.spring.security.jwt

import org.springframework.security.authentication.BadCredentialsException

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/12/08
 */

class CodeException(msg: String, cause: Throwable? = null) : BadCredentialsException(msg, cause)

/**验证码错误, 需要刷新验证码*/
inline fun codeError(message: Any, cause: Throwable? = null): Nothing = throw CodeException(message.toString(), cause)