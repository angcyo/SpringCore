package com.angcyo.spring.security

import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler
import org.springframework.stereotype.Component
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/11/07
 */

@Component
class SecurityLogoutSuccessHandler : LogoutSuccessHandler {
    override fun onLogoutSuccess(request: HttpServletRequest,
                                 response: HttpServletResponse,
                                 authentication: Authentication) {
        SecurityContextHolder.clearContext()
    }
}