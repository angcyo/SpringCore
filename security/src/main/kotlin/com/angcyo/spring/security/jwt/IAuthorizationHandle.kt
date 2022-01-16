package com.angcyo.spring.security.jwt

import com.angcyo.spring.base.data.resultError
import com.angcyo.spring.base.json.toJackson
import com.angcyo.spring.base.servlet.send
import com.angcyo.spring.security.jwt.event.AuthenticationTokenEvent
import com.angcyo.spring.security.jwt.provider.authError
import com.angcyo.spring.security.jwt.token.ResponseAuthenticationToken
import org.springframework.context.ApplicationEventPublisher
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.context.SecurityContextHolder
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 *
 * [com.angcyo.spring.security.jwt.JwtLoginFilter.successfulAuthentication]
 * [com.angcyo.spring.security.jwt.JwtAuthorizationFilter.onSuccessfulAuthentication]
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/05/30
 */
interface IAuthorizationHandle {

    fun onDoSuccessfulAuthentication(
        eventPublisher: ApplicationEventPublisher?,
        request: HttpServletRequest?,
        response: HttpServletResponse?,
        authResult: Authentication?
    ) {
        if (authResult is ResponseAuthenticationToken) {
            //1
            SecurityContextHolder.getContext().authentication = authResult

            //3 Fire event
            try {
                //发送授权成功的事件, 可用于权限验证
                eventPublisher?.publishEvent(AuthenticationTokenEvent(authResult))
            } catch (e: PermissionException) {
                e.printStackTrace()
                //authError(e.message ?: "授权异常失败")
                throw e
            } catch (e: Exception) {
                e.printStackTrace()
                authError(e.message ?: "授权异常失败")
            }
        }
    }

    fun onDoUnsuccessfulAuthentication(
        eventPublisher: ApplicationEventPublisher?,
        request: HttpServletRequest?,
        response: HttpServletResponse?,
        failed: AuthenticationException?
    ) {
        SecurityContextHolder.clearContext()

        if (response != null) {
            var code = HttpServletResponse.SC_UNAUTHORIZED //401
            if (failed is LoginException || failed is BadCredentialsException) {
                code = HttpServletResponse.SC_BAD_REQUEST //400
            } else if (failed is PermissionException) {
                code = HttpServletResponse.SC_FORBIDDEN //403
            }
            response.send(resultError<String>(failed?.message, code).toJackson(), code)
        }

        /*  //response.sendError(HttpServletResponse.SC_UNAUTHORIZED, failed.message)
          val username = request.getParameter(SecurityConstants.KEY_USERNAME)
          response.send(HttpServletResponse.SC_UNAUTHORIZED, "$username 授权失败.")
          //super.unsuccessfulAuthentication(request, response, failed)*/
    }
}