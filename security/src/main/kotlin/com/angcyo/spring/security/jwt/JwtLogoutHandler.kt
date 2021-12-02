package com.angcyo.spring.security.jwt

import com.angcyo.spring.base.beanOf
import com.angcyo.spring.base.data.ok
import com.angcyo.spring.base.json.toJackson
import com.angcyo.spring.base.servlet.send
import com.angcyo.spring.security.service.AuthService
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

class JwtLogoutHandler : LogoutHandler {

    override fun logout(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication?
    ) {
        val userDetail = currentUserOrNull()
        if (userDetail != null) {
            val authService = beanOf(AuthService::class.java)
            authService._logoutEnd(userDetail)
        }

        //清除上下文
        SecurityContextHolder.clearContext()

        /*//清除redis
        request.authPair()?.apply {
            val authService = AuthService::class.java.bean()
            authService._logoutEnd(first)
        }*/

        //发送给客户端
        if (!response.isCommitted) {
            response.send(true.ok<Boolean>().toJackson())
        }
    }
}