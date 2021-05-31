package com.angcyo.spring.security.jwt

import com.angcyo.spring.base.AppProperties
import com.angcyo.spring.base.servlet.request
import com.angcyo.spring.security.SecurityConstants
import com.angcyo.spring.security.jwt.token.ResponseAuthenticationToken
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationListener
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent
import org.springframework.stereotype.Component

/**
 * 权限检查
 *
 * 授权成功之后, 判断是否有权限
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/05/30
 */

@Component
class JwtPermissionListener : ApplicationListener<InteractiveAuthenticationSuccessEvent> {

    @Autowired
    lateinit var appProperties: AppProperties

    override fun onApplicationEvent(event: InteractiveAuthenticationSuccessEvent) {
        if (appProperties.enablePermission) {
            val authentication = event.authentication
            if (authentication is ResponseAuthenticationToken) {
                val path = request()?.servletPath
                if (!path.isNullOrEmpty()) {
                    if (path != SecurityConstants.AUTH_LOGIN_URL) {
                        //permissionException("无权访问:$path")
                    }
                }
            }
        }
    }

    fun permissionException(msg: String) {
        throw PermissionException(msg)
    }
}