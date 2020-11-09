package com.angcyo.spring.log.core

import com.angcyo.spring.base.util.L
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
 *
 *
 * 401的原始请求拦截不到, /error的返回体可以收到
 *
 * 如果被[Filter]拦截中断了, 那么就收不到回调
 */

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
class LoggerDispatcherServlet : DispatcherServlet() {
    private val id = AtomicLong(1)

    override fun doDispatch(request: HttpServletRequest, response: HttpServletResponse) {
        val requestId = id.incrementAndGet()
        ServletLog.wrap(requestId, request, response) { requestWrap, responseWrap, requestBuilder, responseBuilder ->
            if (requestBuilder == null) {
                super.doDispatch(requestWrap, responseWrap!!)
            } else {
                if (responseWrap!!.status in 200..299) {
                    //返回成功, 肯定已经被[LoggerFilter]拦截了, 这里就不要日志了
                } else {
                    L.ih(requestBuilder)
                    L.ih(responseBuilder)
                }
            }
        }
        //super.doDispatch(request, response)
    }
}