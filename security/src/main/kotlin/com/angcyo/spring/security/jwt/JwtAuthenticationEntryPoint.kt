package com.angcyo.spring.security.jwt

import com.angcyo.spring.base.data.resultError
import com.angcyo.spring.base.servlet.send
import com.angcyo.spring.util.L
import com.angcyo.spring.util.decode
import com.angcyo.spring.util.json.toJson
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import java.io.IOException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * 2020-11-07
 * @author shuang.kou
 * @description AuthenticationEntryPoint 用来解决匿名用户访问需要权限才能访问的资源时的异常
 *
 * https://github.com/SpringStudioIst/spring-security-jwt-guide
 */
class JwtAuthenticationEntryPoint : AuthenticationEntryPoint {
    /**
     * 当用户尝试访问需要权限才能的REST资源而不提供Token或者Token错误或者过期时，
     * 将调用此方法发送401响应以及错误信息
     */
    @Throws(IOException::class)
    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authException: AuthenticationException
    ) {
        L.e("[JwtAuthenticationEntryPoint] 未授权: ${request.requestURL.toString().decode()} ${authException.message}")
        authException.printStackTrace()

        val status = response.status
        if (status == 401) {
            //授权失败. response body需要手动设置
            response.send(
                resultError<String>("Unauthorized", HttpServletResponse.SC_UNAUTHORIZED).toJson(),
                HttpServletResponse.SC_UNAUTHORIZED
            )
        } else {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException.message)
        }
    }
}