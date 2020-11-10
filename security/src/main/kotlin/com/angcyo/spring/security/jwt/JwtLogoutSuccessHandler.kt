package com.angcyo.spring.security.jwt

import com.angcyo.spring.base.util.L
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler
import org.springframework.stereotype.Component
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/11/07
 *
 * [org.springframework.security.web.authentication.logout.LogoutFilter.doFilter]
 */

@Component
class JwtLogoutSuccessHandler : LogoutSuccessHandler {

    override fun onLogoutSuccess(request: HttpServletRequest,
                                 response: HttpServletResponse,
                                 authentication: Authentication?) {
        L.i("[SecurityLogoutSuccessHandler] 注销成功: ${request.requestURL}")
    }
}