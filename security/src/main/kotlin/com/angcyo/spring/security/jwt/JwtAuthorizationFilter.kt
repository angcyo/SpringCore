package com.angcyo.spring.security.jwt

import com.angcyo.spring.base.servlet.param
import com.angcyo.spring.security.SecurityConstants
import com.angcyo.spring.security.bean.UserQueryParam
import com.angcyo.spring.security.jwt.token.ResponseAuthenticationToken
import com.angcyo.spring.security.service.AuthService
import com.angcyo.spring.util.L
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * 2020-11-06
 * https://dev.to/kubadlo/spring-security-with-jwt-3j76
 * https://github.com/SpringStudioIst/spring-security-jwt-guide
 *
 * 验证传过来的Token是否有效
 * 从请求头中[com.angcyo.spring.security.SecurityConstants.TOKEN_HEADER]获取token
 * */
class JwtAuthorizationFilter(
    authenticationManager: AuthenticationManager?,
    val authService: AuthService
) : BasicAuthenticationFilter(authenticationManager) {

    /**请求拦截, 验证Token*/
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authentication = getAuthentication(request)
        if (authentication == null) {
            SecurityContextHolder.clearContext()
        } else {
            SecurityContextHolder.getContext().authentication = authentication
        }
        filterChain.doFilter(request, response)
    }

    /**获取授权*/
    private fun getAuthentication(request: HttpServletRequest): ResponseAuthenticationToken? {
        val token = request.getHeader(SecurityConstants.TOKEN_HEADER)

        //1. token检查
        var authentication: ResponseAuthenticationToken? = JWT.parseToken(token)?.run {
            val userId = first
            if (authService._checkTokenValid(userId, token)) {
                val user = authService.userService.list(UserQueryParam().apply {
                    id = userId.toLongOrNull()
                }).firstOrNull()
                if (user == null) {
                    null
                } else {
                    ResponseAuthenticationToken(user)
                }
            } else {
                null
            }
        }

        //未传递token
        val accept = request.getHeader(HttpHeaders.ACCEPT)
        accept?.let {
            if (it.startsWith("image") || it.startsWith("video")) {
                //访问媒体, 给一个临时的token
                authentication = ResponseAuthenticationToken(authService.tempUserTable())
            }
        }

        if (L.isDebug) {
            if (request.param("dev") == "truthy") {
                //开发控制
                authentication = ResponseAuthenticationToken(authService.tempUserTable())
            }
        }

        return authentication
    }
}