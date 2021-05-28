package com.angcyo.spring.security.jwt

import com.angcyo.spring.base.data.ok
import com.angcyo.spring.base.data.resultError
import com.angcyo.spring.base.json.toJackson
import com.angcyo.spring.base.json.toJacksonIgnore
import com.angcyo.spring.base.servlet.fromJson
import com.angcyo.spring.base.servlet.send
import com.angcyo.spring.base.servlet.sendError
import com.angcyo.spring.security.SecurityConstants
import com.angcyo.spring.security.entity.AuthEntity
import com.angcyo.spring.security.entity.toAuthorities
import com.angcyo.spring.security.bean.AuthReqBean
import com.angcyo.spring.security.jwt.provider.RequestAuthenticationToken
import com.angcyo.spring.security.service.AuthService
import com.angcyo.spring.util.str
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter
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
class JwtLoginFilter(
    authManager: AuthenticationManager,
    val authService: AuthService
) : AbstractAuthenticationProcessingFilter(
    AntPathRequestMatcher(SecurityConstants.AUTH_LOGIN_URL, RequestMethod.POST.toString())
) {

    init {
        //设置授权接口地址
        //setFilterProcessesUrl(SecurityConstants.AUTH_LOGIN_URL)
        authenticationManager = authManager
    }

    /**颁发证书, 但是还没有设置证书应该有的权限*/
    override fun attemptAuthentication(request: HttpServletRequest, response: HttpServletResponse): Authentication? {
        val bean = request.fromJson(AuthReqBean::class.java)

        if (bean == null) {
            response.sendError("授权参数异常")
            return null
        }

        /*if (bean == null) {
            response.sendError("参数为空")
            return null
        }

        val validate = bean.validate()
        if (!validate.isNullOrEmpty()) {
            response.send(validate.result().toJson())
            return null
        }

        if (bean.type == WebType.value) {
            //web 登录需要验证 code 码
            if (bean.code.isNullOrBlank()) {
                response.sendError("验证码不正确")
                return null
            }
            val imageCode = authService.getImageCode(request, AuthService.CODE_TYPE_LOGIN)
            if (imageCode == null) {
                response.sendError("验证码已过期")
                return null
            }
            if (bean.code?.toLowerCase() != imageCode.toLowerCase()) {
                response.sendError("验证码不正确")
                return null
            }
        }*/

        return authenticationManager.authenticate(RequestAuthenticationToken(bean))

        /*val entity: AuthEntity
        try {
            entity = authService.loadAuth(bean.username!!) ?: throw UsernameNotFoundException("not found")
            if (!authService.validatePassword(bean.password, entity.password)) {
                throw PasswordException("密码不正确")
            }
        } catch (e: Exception) {
            response.sendError("用户名或密码不正确")
            return null
        }

        //授权
        val authRequest = UsernamePasswordAuthenticationToken(entity, bean.password)
        // Allow subclasses to set the "details" property
        setDetails(request, authRequest)
        return authenticationManager.authenticate(authRequest)*/
    }

    /**认证成功后的回调
     * 创建token返回给客户端*/
    override fun successfulAuthentication(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
        authentication: Authentication
    ) {
        //super会执行授权成功的重定向

        val entity = authentication.principal
        if (entity is AuthEntity) {
            //1
            SecurityContextHolder.getContext().authentication = authentication

            //2
            rememberMeServices.loginSuccess(request, response, authentication)

            //3 Fire event
            eventPublisher?.publishEvent(
                InteractiveAuthenticationSuccessEvent(
                    authentication, this.javaClass
                )
            )

            //4
            val roles = entity.roles.toAuthorities()
            val username = entity.username.str()
            val token = JWT.generateToken(username, roles)
            entity.token = /*SecurityConstants.TOKEN_PREFIX + */token

            //5 将token保存至redis
            authService._loginEnd(username, token)

            //6 清除验证码
            authService.clearImageCode(request, AuthService.CODE_TYPE_LOGIN)

            response.send(entity.ok<AuthEntity>().toJacksonIgnore("roles", "enable"))
            //response.send(entity.ok<AuthEntity>().toJacksonOnly("username", "token"))
        } else {
            response.send("登录成功")

            //super.successfulAuthentication(request, response, filterChain, authentication)
        }
    }

    /**认证失败后的回调*/
    override fun unsuccessfulAuthentication(
        request: HttpServletRequest,
        response: HttpServletResponse,
        failed: AuthenticationException
    ) {
        //super.unsuccessfulAuthentication(request, response, failed)
        SecurityContextHolder.clearContext()
        rememberMeServices.loginFail(request, response)
        response.send(
            resultError<String>(failed.message, HttpServletResponse.SC_UNAUTHORIZED).toJackson(),
            HttpServletResponse.SC_UNAUTHORIZED
        )

        /*  //response.sendError(HttpServletResponse.SC_UNAUTHORIZED, failed.message)
          val username = request.getParameter(SecurityConstants.KEY_USERNAME)
          response.send(HttpServletResponse.SC_UNAUTHORIZED, "$username 授权失败.")
          //super.unsuccessfulAuthentication(request, response, failed)*/
    }

    override fun doFilter(req: ServletRequest?, res: ServletResponse?, chain: FilterChain?) {
        super.doFilter(req, res, chain)
    }
}