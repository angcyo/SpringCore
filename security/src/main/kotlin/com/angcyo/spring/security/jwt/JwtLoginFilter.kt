package com.angcyo.spring.security.jwt

import com.angcyo.spring.base.servlet.body
import com.angcyo.spring.security.SecurityConstants
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import java.util.stream.Collectors
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * 2020-11-06
 * `Authorization: Bearer <token string>`
 *
 * 认证授权, 只在[SecurityConstants.AUTH_LOGIN_URL]接口调用时触发
 * https://dev.to/kubadlo/spring-security-with-jwt-3j76
 * 认证成功后, 返回token `Bearer <token string>`
 */
class JwtLoginFilter(val authManager: AuthenticationManager) : UsernamePasswordAuthenticationFilter() {

    init {
        //设置授权接口地址
        setFilterProcessesUrl(SecurityConstants.AUTH_LOGIN_URL)
    }

    /**检查用户是否登录成功, 登录成功需要返回token给客户端*/
    override fun attemptAuthentication(request: HttpServletRequest, response: HttpServletResponse): Authentication {
        val body = request.body()
        val username = request.getParameter(SecurityConstants.KEY_USERNAME)
        val password = request.getParameter(SecurityConstants.KEY_PASSWORD)
        val authenticationToken = UsernamePasswordAuthenticationToken(username, password)
        return authManager.authenticate(authenticationToken)
    }

    /**认证成功后的回调
     * 创建token返回给客户端*/
    override fun successfulAuthentication(request: HttpServletRequest,
                                          response: HttpServletResponse,
                                          filterChain: FilterChain,
                                          authentication: Authentication) {
        super.successfulAuthentication(request, response, filterChain, authentication)
        val user = authentication.principal as User
        val roles = user.authorities
                .stream()
                .map { obj: GrantedAuthority -> obj.authority }
                .collect(Collectors.toList())

        val token = JWT.generateToken(user.username, roles)

        response.addHeader(SecurityConstants.TOKEN_HEADER, SecurityConstants.TOKEN_PREFIX + token)
        response.writer.println(SecurityConstants.TOKEN_PREFIX + token)
    }

    override fun unsuccessfulAuthentication(request: HttpServletRequest, response: HttpServletResponse, failed: AuthenticationException) {
        super.unsuccessfulAuthentication(request, response, failed)
        /*SecurityContextHolder.clearContext()
        //response.sendError(HttpServletResponse.SC_UNAUTHORIZED, failed.message)
        rememberMeServices.loginFail(request, response)
        val username = request.getParameter(SecurityConstants.KEY_USERNAME)
        response.send(HttpServletResponse.SC_UNAUTHORIZED, "$username 授权失败.")
        //super.unsuccessfulAuthentication(request, response, failed)*/
    }

    override fun doFilter(req: ServletRequest?, res: ServletResponse?, chain: FilterChain?) {
        super.doFilter(req, res, chain)
    }
}