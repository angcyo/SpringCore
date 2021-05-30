package com.angcyo.spring.security.jwt

import org.springframework.security.core.AuthenticationException

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/05/30
 */
class PermissionException(msg: String) : AuthenticationException(msg)