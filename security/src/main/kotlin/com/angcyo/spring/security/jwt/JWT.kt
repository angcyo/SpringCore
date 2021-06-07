package com.angcyo.spring.security.jwt

import com.angcyo.spring.base.extension.apiError
import com.angcyo.spring.security.SecurityConstants
import com.angcyo.spring.security.bean.UserDetail
import com.angcyo.spring.security.jwt.token.ResponseAuthenticationToken
import com.angcyo.spring.util.L
import com.angcyo.spring.util.oneDay
import io.jsonwebtoken.*
import io.jsonwebtoken.security.Keys
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import java.security.SignatureException
import java.util.*

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/11/07
 */

object JWT {

    /**生成一个token
     * [username] token里面可以解析出来的用户名
     * [roles] 用户对应的角色
     * */
    fun generateToken(username: String, roles: Collection<Any>? = null): String {
        val signingKey = SecurityConstants.JWT_SECRET.toByteArray()
        val token = Jwts.builder()
            .signWith(Keys.hmacShaKeyFor(signingKey), SignatureAlgorithm.HS512)
            .setHeaderParam(SecurityConstants.KEY_TOKEN_TYPE, SecurityConstants.TOKEN_TYPE)
            .setIssuer(SecurityConstants.TOKEN_ISSUER)
            .setAudience(SecurityConstants.TOKEN_AUDIENCE)
            .setSubject(username)
            .setExpiration(Date(System.currentTimeMillis() + oneDay))
            .claim(SecurityConstants.TOKEN_ROLES, roles)
            .compact()
        return token
    }

    /**从token中解析数据
     * 格式:`Authorization: Bearer <token string>`
     * [first] 用户名
     * [second] 角色权限
     */
    fun parseToken(token: String?): Pair<String, Collection<GrantedAuthority>?>? {
        if (token?.startsWith(SecurityConstants.TOKEN_PREFIX) == true) {
            try {
                val signingKey = SecurityConstants.JWT_SECRET.toByteArray()

                val parsedToken = Jwts.parserBuilder().setSigningKey(signingKey).build()
                    .parseClaimsJws(token.replace(SecurityConstants.TOKEN_PREFIX, ""))

                val username = parsedToken.body.subject
                if (username.isNullOrBlank()) {
                    return null
                }

                val roles = parsedToken.body[SecurityConstants.TOKEN_ROLES]
                return if (roles is Iterable<*>) {
                    val authorities = mutableListOf<SimpleGrantedAuthority>()
                    roles.forEach { role ->
                        if (role is Map<*, *>) {
                            val authority = role["authority"]
                            if (authority is String && authority.isNotEmpty()) {
                                authorities.add(SimpleGrantedAuthority(authority))
                            }
                        }
                    }
                    //颁发证书
                    username to authorities
                } else {
                    username to null
                }
            } catch (exception: ExpiredJwtException) {
                L.w("Request to parse expired JWT : {} failed : {}", token, exception.message)
            } catch (exception: UnsupportedJwtException) {
                L.w("Request to parse unsupported JWT : {} failed : {}", token, exception.message)
            } catch (exception: MalformedJwtException) {
                L.w("Request to parse invalid JWT : {} failed : {}", token, exception.message)
            } catch (exception: SignatureException) {
                L.w("Request to parse JWT with invalid signature : {} failed : {}", token, exception.message)
            } catch (exception: IllegalArgumentException) {
                L.w("Request to parse empty or null JWT : {} failed : {}", token, exception.message)
            }
        }
        return null
    }
}

/**获取当前登录的用户信息*/
fun currentUser(): UserDetail {
    return currentUserOrNull() ?: apiError("请先登录")
}

fun currentUserOrNull(): UserDetail? {
    val authentication = SecurityContextHolder.getContext().authentication
    if (authentication is ResponseAuthenticationToken) {
        return authentication.userDetail
    }
    return null
}
