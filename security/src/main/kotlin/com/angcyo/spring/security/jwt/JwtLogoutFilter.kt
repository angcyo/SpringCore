package com.angcyo.spring.security.jwt

import com.angcyo.spring.security.SecurityConstants
import org.springframework.security.web.authentication.logout.LogoutFilter
import org.springframework.security.web.authentication.logout.LogoutHandler
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/11/07
 */

@Deprecated("使用[SecurityConfiguration]中配置")
class JwtLogoutFilter(logoutSuccessHandler: LogoutSuccessHandler,
                      vararg handlers: LogoutHandler)
    : LogoutFilter(logoutSuccessHandler, *handlers) {
    init {
        //设置授权接口地址
        setFilterProcessesUrl(SecurityConstants.AUTH_LOGOUT_URL)
    }

    override fun requiresLogout(request: HttpServletRequest, response: HttpServletResponse): Boolean {
        return super.requiresLogout(request, response)
    }

    override fun doFilter(req: ServletRequest, res: ServletResponse, chain: FilterChain) {
        super.doFilter(req, res, chain)
    }
}