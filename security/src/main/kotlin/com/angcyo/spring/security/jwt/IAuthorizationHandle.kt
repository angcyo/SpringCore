package com.angcyo.spring.security.jwt

import com.angcyo.spring.base.data.resultError
import com.angcyo.spring.base.json.toJackson
import com.angcyo.spring.base.servlet.send
import com.angcyo.spring.security.jwt.token.ResponseAuthenticationToken
import org.springframework.context.ApplicationEventPublisher
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.context.SecurityContextHolder
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
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
            eventPublisher?.publishEvent(
                InteractiveAuthenticationSuccessEvent(authResult, ResponseAuthenticationToken::class.java)
            )
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
            var code = HttpServletResponse.SC_UNAUTHORIZED
            if (failed is PermissionException) {
                code = HttpServletResponse.SC_FORBIDDEN
            }
            response.send(resultError<String>(failed?.message, code).toJackson(), code)
        }

        /*  //response.sendError(HttpServletResponse.SC_UNAUTHORIZED, failed.message)
          val username = request.getParameter(SecurityConstants.KEY_USERNAME)
          response.send(HttpServletResponse.SC_UNAUTHORIZED, "$username 授权失败.")
          //super.unsuccessfulAuthentication(request, response, failed)*/
    }
}