package com.angcyo.spring.security.ktx

import com.angcyo.spring.security.SecurityConstants
import com.angcyo.spring.security.jwt.JWT
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.security.web.util.matcher.OrRequestMatcher
import org.springframework.security.web.util.matcher.RequestMatcher
import javax.servlet.http.HttpServletRequest

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/11/10
 */

/**[JWT.parseToken]
 * [first] 用户名
 * [second] 角色权限
 * */
fun HttpServletRequest.authPair() = JWT.parseToken(getHeader(SecurityConstants.TOKEN_HEADER))

/**请求是否匹配白名单
 * [org.springframework.http.HttpMethod]*/
fun HttpServletRequest.matches(whiteList: List<String>, httpMethod: String? = null): Boolean {
    //白名单检查
    if (whiteList.isNotEmpty()) {
        val matchersList = mutableListOf<RequestMatcher>()
        for (pattern in whiteList) {
            matchersList.add(AntPathRequestMatcher(pattern, httpMethod))
        }
        if (OrRequestMatcher(matchersList).matches(this)) {
            //白名单通过
            return true
        }
        return false
    }
    return true
}