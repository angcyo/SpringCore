package com.angcyo.spring.security.jwt

import com.angcyo.spring.base.data.ok
import com.angcyo.spring.base.data.result
import com.angcyo.spring.base.data.resultError
import com.angcyo.spring.base.data.validate
import com.angcyo.spring.base.json.toJackson
import com.angcyo.spring.base.json.toJacksonIgnore
import com.angcyo.spring.base.json.toJson
import com.angcyo.spring.base.servlet.fromJson
import com.angcyo.spring.base.servlet.send
import com.angcyo.spring.base.servlet.sendError
import com.angcyo.spring.base.str
import com.angcyo.spring.security.SecurityConstants
import com.angcyo.spring.security.controller.RegisterBean
import com.angcyo.spring.security.entity.AuthEntity
import com.angcyo.spring.security.entity.toAuthorities
import com.angcyo.spring.security.service.AuthService
import org.bouncycastle.openssl.PasswordException
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.web.bind.annotation.RequestMethod
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
class JwtLoginFilter(authManager: AuthenticationManager, val authService: AuthService) : UsernamePasswordAuthenticationFilter() {

    init {
        //设置授权接口地址
        //setFilterProcessesUrl(SecurityConstants.AUTH_LOGIN_URL)
        setRequiresAuthenticationRequestMatcher(
                AntPathRequestMatcher(SecurityConstants.AUTH_LOGIN_URL, RequestMethod.POST.toString()))
        authenticationManager = authManager
    }

    override fun getAuthenticationManager(): AuthenticationManager {
        return super.getAuthenticationManager()
    }

    /**颁发证书, 但是还没有设置证书应该有的权限*/
    override fun attemptAuthentication(request: HttpServletRequest, response: HttpServletResponse): Authentication? {
        val bean = request.fromJson(RegisterBean::class.java)

        if (bean == null) {
            response.sendError("参数为空")
            return null
        }

        val validate = bean.validate()
        if (!validate.isNullOrEmpty()) {
            response.sendError(validate.result().toJson())
            return null
        }

        val entity: AuthEntity
        try {
            entity = authService.loadAuth(bean.username!!) ?: throw UsernameNotFoundException("not found")
            if (!authService.validatePassword(bean.password, entity.password)) {
                throw PasswordException("密码不正确")
            }
        } catch (e: Exception) {
            response.sendError("用户名或密码不正确")
            return null
        }

        return bean.run {
            val authRequest = UsernamePasswordAuthenticationToken(entity, password)
            // Allow subclasses to set the "details" property
            setDetails(request, authRequest)
            authenticationManager.authenticate(authRequest)
        }
    }

    /**认证成功后的回调
     * 创建token返回给客户端*/
    override fun successfulAuthentication(request: HttpServletRequest,
                                          response: HttpServletResponse,
                                          filterChain: FilterChain,
                                          authentication: Authentication) {
        //super会执行授权成功的重定向

        val entity = authentication.principal
        if (entity is AuthEntity) {
            //1
            SecurityContextHolder.getContext().authentication = authentication

            //2
            rememberMeServices.loginSuccess(request, response, authentication)

            //3 Fire event
            eventPublisher?.publishEvent(InteractiveAuthenticationSuccessEvent(
                    authentication, this.javaClass))

            //4
            val roles = entity.roles.toAuthorities()
            val username = entity.username.str()
            val token = JWT.generateToken(username, roles)
            entity.token = /*SecurityConstants.TOKEN_PREFIX + */token

            //5 将token保存至redis
            authService._loginEnd(username, token)

            response.send(entity.ok<AuthEntity>().toJacksonIgnore("roles", "enable"))
            //response.send(entity.ok<AuthEntity>().toJacksonOnly("username", "token"))
        } else {
            super.successfulAuthentication(request, response, filterChain, authentication)
        }
    }

    override fun unsuccessfulAuthentication(request: HttpServletRequest, response: HttpServletResponse, failed: AuthenticationException) {
        //super.unsuccessfulAuthentication(request, response, failed)
        SecurityContextHolder.clearContext()
        rememberMeServices.loginFail(request, response)
        response.send(
                resultError(failed.message, HttpServletResponse.SC_UNAUTHORIZED).toJackson(),
                HttpServletResponse.SC_UNAUTHORIZED)

        /*  //response.sendError(HttpServletResponse.SC_UNAUTHORIZED, failed.message)
          val username = request.getParameter(SecurityConstants.KEY_USERNAME)
          response.send(HttpServletResponse.SC_UNAUTHORIZED, "$username 授权失败.")
          //super.unsuccessfulAuthentication(request, response, failed)*/
    }

    override fun doFilter(req: ServletRequest?, res: ServletResponse?, chain: FilterChain?) {
        super.doFilter(req, res, chain)
    }
}