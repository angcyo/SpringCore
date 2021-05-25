package com.angcyo.spring.log.core

import com.angcyo.spring.util.L
import org.springframework.web.filter.CommonsRequestLoggingFilter
import javax.servlet.http.HttpServletRequest

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/11/09
 *
 * https://www.baeldung.com/spring-http-logging
 */

/*@Component*/
@Deprecated("没有ResponseBody的回调")
class CoreLoggerFilter : CommonsRequestLoggingFilter() {

    init {
        isIncludeClientInfo = true
        isIncludeHeaders = true
        isIncludePayload = true
        isIncludeQueryString = true
        maxPayloadLength = 10000
        setAfterMessagePrefix("REQUEST DATA : ")
    }

    override fun shouldLog(request: HttpServletRequest): Boolean {
        return true
    }

    override fun beforeRequest(request: HttpServletRequest, message: String) {
        super.beforeRequest(request, message)
        L.i(message)
    }

    override fun afterRequest(request: HttpServletRequest, message: String) {
        super.afterRequest(request, message)
        L.i(message)
    }
}