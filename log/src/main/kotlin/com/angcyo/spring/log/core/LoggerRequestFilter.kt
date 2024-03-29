package com.angcyo.spring.log.core

import com.angcyo.spring.util.L
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
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
 * 拦截所有请求.
 * 拦截不了Spring返回的404, 401, 403等返回体信息
 *
 * [404, 401, 403]等信息, 需要使用[LoggerDispatcherServlet]拦截
 *
 * https://juejin.im/post/6844903624187854862#heading-6
 * Filter 对 用户请求 进行 预处理，接着将请求交给 Servlet 进行 处理 并 生成响应，最后 Filter 再对 服务器响应 进行 后处理。
 */

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
class LoggerRequestFilter : OncePerRequestFilter() {

    companion object {
        val LOG_IGNORE = arrayOf(
            ".*\\.js",
            ".*\\.css",
            ".*\\.html",
            "/images/.*",
            "/webjars/.*",
            "/.*/favicon.ico"
        )
    }

    private val id = AtomicLong(1)

    /**https://github.com/isrsal/spring-mvc-logger*/
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val path = request.servletPath
        for (ignore in LOG_IGNORE) {
            if (ignore.toRegex().matches(path)) {
                //忽略此请求
                filterChain.doFilter(request, response)
                return
            }
        }

        //
        val requestId = id.incrementAndGet()
        ServletLog.wrap(requestId, request, response) { requestWrap, responseWrap, requestBuilder, responseBuilder ->
            if (requestBuilder == null) {
                filterChain.doFilter(requestWrap, responseWrap)
            } else {
                L.ih(requestBuilder)
                L.ih(responseBuilder)
            }
        }
    }
}