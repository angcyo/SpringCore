package com.angcyo.spring.core.http

import com.angcyo.spring.util.L
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerExceptionResolver
import org.springframework.web.servlet.ModelAndView
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/02/27
 */

@Component
class CoreHandlerExceptionResolver : HandlerExceptionResolver {

    override fun resolveException(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any?,
        ex: Exception
    ): ModelAndView? {
        L.w("全局异常:${ex.message}")
        response.sendError(400, ex.message)
        return ModelAndView()
        //return null
    }
}