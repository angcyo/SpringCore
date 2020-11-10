package com.angcyo.spring.security

import com.angcyo.spring.base.data.ok
import com.angcyo.spring.base.json.toJackson
import com.angcyo.spring.base.servlet.send
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.logout.LogoutHandler
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/11/07
 *
 * 开始注销登录用户
 */

class SecurityLogoutHandler : LogoutHandler {
    override fun logout(request: HttpServletRequest,
                        response: HttpServletResponse,
                        authentication: Authentication?) {
        SecurityContextHolder.clearContext()
        if (!response.isCommitted) {
            response.send(true.ok<Boolean>().toJackson())
        }
    }
}