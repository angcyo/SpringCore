package com.angcyo.spring.log.core

import org.springframework.web.filter.OncePerRequestFilter
import java.util.concurrent.atomic.AtomicLong
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/11/06
 *
 * 网络请求过滤器
 *
 * https://www.baeldung.com/spring-boot-add-filter
 * https://blog.csdn.net/fxbin123/article/details/82558174
 *
 * https://blog.csdn.net/weixin_44515491/article/details/98906392
 *
 * 似乎拦截不了Spring返回的404, 401, 403等返回体信息
 */

/*@Component
@Order(Ordered.HIGHEST_PRECEDENCE)*/
@Deprecated("401,403,404拿不到")
class LoggerFilter : OncePerRequestFilter() {

    private val id = AtomicLong(1)

    /**https://github.com/isrsal/spring-mvc-logger*/
    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
        val requestId = id.incrementAndGet()

        ServletLog.wrap(requestId, request, response) { requestWrap, responseWrap ->
            filterChain.doFilter(requestWrap, responseWrap)
        }
    }
}