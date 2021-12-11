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
import org.springframework.security.core.userdetails.UserDetails
import java.security.SignatureException
import java.util.*
import javax.crypto.SecretKey


/**
 * Jwt token 工具类
 *
 * http://www.macrozheng.com/#/architect/mall_arch_04?id=%e6%b7%bb%e5%8a%a0jwt-token%e7%9a%84%e5%b7%a5%e5%85%b7%e7%b1%bb
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/11/07
 */

object JWT {

    private const val CLAIM_KEY_CREATED = "created"

    fun secretKey(): SecretKey {
        val signingKey = SecurityConstants.JWT_SECRET.toByteArray()
        val secretKey = Keys.hmacShaKeyFor(signingKey)
        return secretKey
    }

    fun jwtBuilder(): JwtBuilder {

        return Jwts.builder().signWith(secretKey(), SignatureAlgorithm.HS512)
            .setHeaderParam(SecurityConstants.KEY_TOKEN_TYPE, SecurityConstants.TOKEN_TYPE)
            .setIssuer(SecurityConstants.TOKEN_ISSUER).setAudience(SecurityConstants.TOKEN_AUDIENCE)
            .setExpiration(generateExpirationDate()) //过期时间
    }

    /**生成一个token
     * [username] token里面可以解析出来的用户名
     * [roles] 用户对应的角色
     * */
    fun generateToken(username: String, roles: Collection<Any>? = null): String {
        val token = jwtBuilder().setSubject(username).claim(SecurityConstants.TOKEN_ROLES, roles).compact()
        return token
    }

    /**
     * 根据负责生成JWT的token
     */
    private fun generateToken(username: String, claims: Map<String, Any?>, roles: Collection<Any>? = null): String? {
        val token =
            jwtBuilder().setSubject(username).claim(SecurityConstants.TOKEN_ROLES, roles).addClaims(claims).compact()
        return token
    }

    /**
     * 生成token的过期时间
     */
    private fun generateExpirationDate(): Date {
        return Date(System.currentTimeMillis() + oneDay)
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


    /**
     * 验证token是否还有效
     *
     * @param token       客户端传入的token
     * @param userDetails 从数据库中查询出来的用户信息
     */
    fun validateToken(token: String?, userDetails: UserDetails): Boolean {
        val username: String? = getUserNameFromToken(token)
        return username == userDetails.username && !isTokenExpired(token)
    }

    /**
     * 从token中获取登录用户名
     */
    fun getUserNameFromToken(token: String?): String? {
        val username: String? = try {
            val claims: Claims? = getClaimsFromToken(token)
            claims?.subject
        } catch (e: Exception) {
            null
        }
        return username
    }

    /**
     * 从token中获取JWT中的负载
     */
    private fun getClaimsFromToken(token: String?): Claims? {
        var claims: Claims? = null
        try {
            claims = Jwts.parserBuilder()
                //.requireAudience("string")
                .setSigningKey(secretKey()).build().parseClaimsJws(token).body
        } catch (e: Exception) {
            L.i("JWT格式验证失败:{}", token)
        }
        return claims
    }

    /**
     * 判断token是否已经失效
     */
    private fun isTokenExpired(token: String?): Boolean {
        val expiredDate: Date? = getExpiredDateFromToken(token)
        return expiredDate?.before(Date()) == true
    }

    /**
     * 从token中获取过期时间
     */
    private fun getExpiredDateFromToken(token: String?): Date? {
        val claims = getClaimsFromToken(token)
        return claims?.expiration
    }


    /**
     * 判断token是否可以被刷新
     */
    fun canRefresh(token: String): Boolean {
        return !isTokenExpired(token)
    }

    /**
     * 刷新token
     */
    fun refreshToken(token: String?, username: String, roles: Collection<Any>? = null): String? {
        val claims = getClaimsFromToken(token)
        claims!![CLAIM_KEY_CREATED] = Date()
        return generateToken(username, claims, roles)
    }

}

/**当前登录的用户id*/
fun currentUserId(): Long = currentUser().userTable!!.id!!

fun currentUserIdOrDef(def: Long = -1): Long = currentUser().userTable?.id ?: def

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
