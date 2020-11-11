package com.angcyo.spring.security.jwt

import com.angcyo.spring.base.util.L
import com.angcyo.spring.security.SecurityConstants
import com.angcyo.spring.security.UserDetailsServiceImpl
import com.angcyo.spring.security.service.AuthService
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
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
 * 授权过程
 * 从请求头中[ecurityConstants.TOKEN_HEADER]获取token
 * */
class JwtAuthorizationFilter(authenticationManager: AuthenticationManager?,
                             val userDetailsService: UserDetailsServiceImpl,
                             val authService: AuthService) :
        BasicAuthenticationFilter(authenticationManager) {

    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
        val authentication = getAuthentication(request)
        if (authentication == null) {
            SecurityContextHolder.clearContext()
        } else {
            SecurityContextHolder.getContext().authentication = authentication
        }
        filterChain.doFilter(request, response)
    }

    private fun getAuthentication(request: HttpServletRequest): UsernamePasswordAuthenticationToken? {
        val token = request.getHeader(SecurityConstants.TOKEN_HEADER)
        var authentication = JWT.parseToken(token)?.run {
            if (authService._checkTokenValid(first, token)) {
                UsernamePasswordAuthenticationToken(first, null, second)
            } else {
                null
            }
        }

        if (authentication == null) {
            //未传递token
            val accept = request.getHeader(HttpHeaders.ACCEPT)
            accept?.let {
                if (it.startsWith("image") || it.startsWith("video")) {
                    //访问媒体, 给一个临时的token
                    authentication = UsernamePasswordAuthenticationToken(JWT.TEMP_USER, null, JWT.TEMP_USER_ROLES)
                }
            }
        }

        if (authentication == null) {
            if (L.isDebug) {
                if (request.getParameter("dev") == "truthy") {
                    //开发控制
                    authentication = UsernamePasswordAuthenticationToken(JWT.TEMP_GUEST, null, JWT.TEMP_GUEST_ROLES)
                }
            }
        }

        return authentication
    }
}