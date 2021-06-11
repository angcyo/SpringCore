package com.angcyo.spring.security.jwt

import com.angcyo.spring.base.data.ok
import com.angcyo.spring.base.json.toJackson
import com.angcyo.spring.base.servlet.fromJson
import com.angcyo.spring.base.servlet.send
import com.angcyo.spring.base.servlet.sendError
import com.angcyo.spring.security.SecurityConstants
import com.angcyo.spring.security.bean.AuthRepBean
import com.angcyo.spring.security.bean.AuthReqBean
import com.angcyo.spring.security.jwt.event.LoginEvent
import com.angcyo.spring.security.jwt.provider.authError
import com.angcyo.spring.security.jwt.token.RequestAuthenticationToken
import com.angcyo.spring.security.jwt.token.ResponseAuthenticationToken
import com.angcyo.spring.security.service.AuthService
import org.springframework.security.authentication.AuthenticationManager
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
), IAuthorizationHandle {

    init {
        //设置授权接口地址
        //setFilterProcessesUrl(SecurityConstants.AUTH_LOGIN_URL)
        authenticationManager = authManager
    }

    /**颁发证书, 但是还没有设置证书应该有的权限*/
    override fun attemptAuthentication(request: HttpServletRequest, response: HttpServletResponse): Authentication? {
        SecurityContextHolder.clearContext()

        val bean = request.fromJson(AuthReqBean::class.java)

        if (bean == null) {
            response.sendError("授权参数异常")
            return null
        }

        if (bean.account.isNullOrEmpty()) {
            response.sendError("无效的账号或密码")
            return null
        }

        if (!authService.accountService.isAccountExist(bean.account)) {
            response.sendError("无效的账号或密码")
            return null
        }

        return authenticationManager.authenticate(RequestAuthenticationToken(bean))
    }

    /**认证成功后的回调
     * 创建token返回给客户端*/
    override fun successfulAuthentication(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
        authentication: Authentication
    ) {
        if (authentication is ResponseAuthenticationToken) {

            //2
            rememberMeServices.loginSuccess(request, response, authentication)

            //4 使用用户的id,创建token
            val userDetail = authentication.userDetail
            val flag = "${userDetail.userTable?.id}"
            val token = JWT.generateToken(flag)

            //5 将token保存至redis
            authService._loginEnd(userDetail, token)

            try {
                eventPublisher?.publishEvent(LoginEvent(userDetail))
                onDoSuccessfulAuthentication(eventPublisher, request, response, authentication)
            } catch (e: PermissionException) {
                e.printStackTrace()
                //authError(e.message ?: "授权异常失败")
                throw e
            } catch (e: Exception) {
                e.printStackTrace()
                authError(e.message ?: "授权异常失败")
            }

            //send response
            val repBean = AuthRepBean()
            repBean.id = userDetail.userTable?.id
            repBean.nickname = userDetail.userTable?.nickname
            repBean.token = SecurityConstants.TOKEN_PREFIX + token
            response.send(repBean.ok<AuthRepBean>().toJackson())


        } else {
            //super会执行授权成功的重定向
            super.successfulAuthentication(request, response, filterChain, authentication)
        }
    }

    /**认证失败后的回调*/
    override fun unsuccessfulAuthentication(
        request: HttpServletRequest,
        response: HttpServletResponse,
        failed: AuthenticationException
    ) {
        //super.unsuccessfulAuthentication(request, response, failed)
        rememberMeServices.loginFail(request, response)
        onDoUnsuccessfulAuthentication(eventPublisher, request, response, failed)
    }

    override fun doFilter(req: ServletRequest?, res: ServletResponse?, chain: FilterChain?) {
        super.doFilter(req, res, chain)
    }
}