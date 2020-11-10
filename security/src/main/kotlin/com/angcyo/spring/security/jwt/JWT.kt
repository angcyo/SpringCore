package com.angcyo.spring.security.jwt

import com.angcyo.spring.base.oneDay
import com.angcyo.spring.base.util.L
import com.angcyo.spring.security.SecurityConstants
import com.angcyo.spring.security.entity.Roles
import io.jsonwebtoken.*
import io.jsonwebtoken.security.Keys
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import java.security.SignatureException
import java.util.*

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/11/07
 */

object JWT {

    const val TEMP_USER = "TEMP_USER"
    const val TEMP_GUEST = "TEMP_GUEST"
    const val TEMP_ADMIN = "TEMP_ADMIN"

    val TEMP_USER_ROLES = listOf(SimpleGrantedAuthority(Roles.USER))
    val TEMP_GUEST_ROLES = listOf(SimpleGrantedAuthority(Roles.GUEST))
    val TEMP_ADMIN_ROLES = listOf(SimpleGrantedAuthority(Roles.USER), SimpleGrantedAuthority(Roles.ADMIN))

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
                if (roles is Iterable<*>) {
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
                    return username to authorities
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