package com.angcyo.spring.security.jwt

import com.angcyo.spring.base.data.ifError
import com.angcyo.spring.base.servlet.param
import com.angcyo.spring.security.SecurityConstants
import com.angcyo.spring.security.bean.UserDetail
import com.angcyo.spring.security.bean.UserQueryParam
import com.angcyo.spring.security.jwt.token.ResponseAuthenticationToken
import com.angcyo.spring.security.service.AuthService
import com.angcyo.spring.security.table.UserTable
import com.angcyo.spring.util.L
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.ApplicationEventPublisherAware
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
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
    authenticationManager: AuthenticationManager?, val authService: AuthService
) : BasicAuthenticationFilter(authenticationManager), ApplicationEventPublisherAware, IAuthorizationHandle {

    /**请求拦截, 验证Token*/
    override fun doFilterInternal(
        request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain
    ) {
        try {
            val authentication = getAuthentication(request)
            if (authentication == null) {
                SecurityContextHolder.clearContext()
                //val ex = UsernameNotFoundException("无效的TOKEN")
                //onUnsuccessfulAuthentication(request, response, ex)
                //authenticationEntryPoint.commence(request, response, ex)
            } else {
                SecurityContextHolder.getContext().authentication = authentication
                onSuccessfulAuthentication(request, response, authentication)
            }
        } catch (ex: PermissionException) {
            SecurityContextHolder.clearContext()
            logger.debug("Failed to process authentication request", ex)
            onUnsuccessfulAuthentication(request, response, ex)
            return
        } catch (ex: AuthenticationException) {
            SecurityContextHolder.clearContext()
            logger.debug("Failed to process authentication request", ex)
            onUnsuccessfulAuthentication(request, response, ex)
            return
        }
        filterChain.doFilter(request, response)
    }

    /**获取授权*/
    private fun getAuthentication(request: HttpServletRequest): Authentication? {
        val token = request.getHeader(SecurityConstants.TOKEN_HEADER)

        var authentication: ResponseAuthenticationToken? = null

        if (token.isNullOrEmpty()) {
            //临时授权功能支持
            authService.authorizationTempToken(request)?.let {
                authentication = ResponseAuthenticationToken(authService.tempUserDetail(UserTable().apply {
                    id = it.first
                    nickname = "临时用户"
                    des = "临时用户"
                }))
            }
        } else {
            //1. token检查
            val parseToken = JWT.parseToken(token)

            if (parseToken == null) {
                L.e("无法解析TOKEN:$token")
            } else {
                authentication = parseToken.run {
                    val userId = first
                    if (authService._checkTokenValid(userId, token)) {
                        val userDetail = authService.getUserDetail(userId) ?: UserDetail().apply {
                            //无缓存, 从新获取数据
                            val user = authService.userService.autoList(UserQueryParam().apply {
                                id = userId.toLongOrNull()
                            }).firstOrNull().ifError("无效的用户")
                            userTable = user
                            authService.userService.autoFill(this)
                        }
                        ResponseAuthenticationToken(userDetail)
                    } else {
                        L.e("无效的TOKEN:$token")
                        null
                    }
                }
            }
        }

        /*//未传递token
        val accept = request.getHeader(HttpHeaders.ACCEPT)
        accept?.let {
            if (it.startsWith("image") || it.startsWith("video")) {
                //访问媒体, 给一个临时的token
                authentication = ResponseAuthenticationToken(authService.tempUserDetail())
            }
        }*/

        if (authentication == null || L.isDebug) {
            if (request.param("dev") == "truthy") {
                //开发控制
                authentication = ResponseAuthenticationToken(authService.tempUserDetail())
            }
        }

        return authentication
    }

    override fun onSuccessfulAuthentication(
        request: HttpServletRequest?, response: HttpServletResponse?, authResult: Authentication?
    ) {
        super.onSuccessfulAuthentication(request, response, authResult)
        onDoSuccessfulAuthentication(eventPublisher, request, response, authResult)
    }

    override fun onUnsuccessfulAuthentication(
        request: HttpServletRequest?, response: HttpServletResponse?, failed: AuthenticationException?
    ) {
        super.onUnsuccessfulAuthentication(request, response, failed)
        onDoUnsuccessfulAuthentication(eventPublisher, request, response, failed)
    }

    var eventPublisher: ApplicationEventPublisher? = null

    override fun setApplicationEventPublisher(applicationEventPublisher: ApplicationEventPublisher) {
        eventPublisher = applicationEventPublisher
    }
}