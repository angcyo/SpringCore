package com.angcyo.spring.security

import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.logout.LogoutHandler
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/11/07
 */

class SecurityLogoutHandler : LogoutHandler {
    override fun logout(request: HttpServletRequest,
                        response: HttpServletResponse,
                        authentication: Authentication) {
        assert(authentication != null)
    }
}