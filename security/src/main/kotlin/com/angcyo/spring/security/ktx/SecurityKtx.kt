package com.angcyo.spring.security.ktx

import com.angcyo.spring.security.SecurityConstants
import com.angcyo.spring.security.jwt.JWT
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