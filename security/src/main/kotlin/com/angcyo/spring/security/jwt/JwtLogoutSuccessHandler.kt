package com.angcyo.spring.security.jwt

import com.angcyo.spring.security.jwt.event.LogoutEvent
import com.angcyo.spring.security.jwt.token.ResponseAuthenticationToken
import com.angcyo.spring.util.L
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.ApplicationEventPublisherAware
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler
import org.springframework.stereotype.Component
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/11/07
 *
 * [org.springframework.security.web.authentication.logout.LogoutFilter.doFilter]
 */

@Component
class JwtLogoutSuccessHandler : LogoutSuccessHandler, ApplicationEventPublisherAware {

    override fun onLogoutSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication?
    ) {
        if (authentication is ResponseAuthenticationToken) {
            eventPublisher?.publishEvent(LogoutEvent(authentication.userDetail))
        }
        L.i("[SecurityLogoutSuccessHandler] 注销成功: ${request.requestURL}")
    }

    var eventPublisher: ApplicationEventPublisher? = null

    override fun setApplicationEventPublisher(applicationEventPublisher: ApplicationEventPublisher) {
        eventPublisher = applicationEventPublisher
    }
}