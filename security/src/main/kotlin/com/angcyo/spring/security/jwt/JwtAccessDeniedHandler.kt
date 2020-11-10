package com.angcyo.spring.security.jwt

import com.angcyo.spring.base.util.L
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.web.access.AccessDeniedHandler
import java.io.IOException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * 2020-11-07
 * @author shuang.kou
 * @description AccessDeineHandler 用来解决认证过的用户访问无权限资源时的异常
 * https://github.com/SpringStudioIst/spring-security-jwt-guide
 */
class JwtAccessDeniedHandler : AccessDeniedHandler {
    /**
     * 当用户尝试访问需要权限才能的REST资源而权限不足的时候，
     * 将调用此方法发送403响应以及错误信息
     */
    @Throws(IOException::class)
    override fun handle(request: HttpServletRequest, response: HttpServletResponse, accessDeniedException: AccessDeniedException) {
        val exception = AccessDeniedException("Sorry you don not enough permissions to access it!")
        response.sendError(HttpServletResponse.SC_FORBIDDEN, exception.message)

        L.e("[JwtAccessDeniedHandler] 权限不足: ${request.requestURL}")
    }
}