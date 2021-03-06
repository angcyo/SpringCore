package com.angcyo.spring.security.jwt

import com.angcyo.spring.base.AppProperties
import com.angcyo.spring.base.servlet.request
import com.angcyo.spring.security.SecurityConfiguration
import com.angcyo.spring.security.SecurityConstants
import com.angcyo.spring.security.controller.PermissionManager
import com.angcyo.spring.security.jwt.event.AuthenticationTokenEvent
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationListener
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.security.web.util.matcher.OrRequestMatcher
import org.springframework.security.web.util.matcher.RequestMatcher
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct

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
class JwtPermissionListener : ApplicationListener<AuthenticationTokenEvent> {

    @Autowired
    lateinit var appProperties: AppProperties

    /**白名单列表*/
    val whiteList = mutableListOf<String>()

    @PostConstruct
    fun initPost() {
        whiteList.addAll(SecurityConfiguration.SECURITY_WHITE_LIST)
        whiteList.addAll(SecurityConstants.AUTH_URL_LIST)
    }

    @Autowired
    lateinit var permissionManager: PermissionManager

    override fun onApplicationEvent(event: AuthenticationTokenEvent) {
        if (appProperties.enablePermission) {
            val token = event.token
            val path = request()?.servletPath // "/xxx/xxx"
            if (!path.isNullOrEmpty()) {

                //白名单检查
                if (whiteList.isNotEmpty()) {
                    val matchersList = mutableListOf<RequestMatcher>()
                    for (pattern in whiteList) {
                        matchersList.add(AntPathRequestMatcher(pattern, null))
                    }
                    if (OrRequestMatcher(matchersList).matches(request())) {
                        //白名单通过
                        return
                    }
                }

                //权限验证
                token.userDetail.userTable?.id?.let { id ->
                    if (permissionManager.havePermission(id, path)) {
                        //有权限
                    } else {
                        //无权限
                        permissionException("无权访问:$path")
                    }
                }
            }
        }
    }

    fun permissionException(msg: String) {
        throw PermissionException(msg)
    }
}