package com.angcyo.spring.core.log

import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.web.servlet.DispatcherServlet
import java.util.concurrent.atomic.AtomicLong
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/11/06
 *
 * https://blog.csdn.net/weixin_44515491/article/details/98906392
 */

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
/*@Deprecated("可以拿到403, 404")*/
class LoggerDispatcherServlet : DispatcherServlet() {
    private val id = AtomicLong(1)

    override fun doDispatch(request: HttpServletRequest, response: HttpServletResponse) {
        val requestId = id.incrementAndGet()
        ServletLog.wrap(requestId, request, response) { requestWrap, responseWrap ->
            super.doDispatch(requestWrap, responseWrap)
        }
    }
}