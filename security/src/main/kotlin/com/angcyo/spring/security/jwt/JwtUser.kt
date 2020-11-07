package com.angcyo.spring.security.jwt

import com.angcyo.spring.security.entity.AuthEntity
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

/**
 * @author shuang.kou
 * @description JWT用户对象
 */
class JwtUser(val authEntity: AuthEntity) : UserDetails {

    private val authorities: List<SimpleGrantedAuthority>

    init {
        val list = mutableListOf<SimpleGrantedAuthority>()
        authEntity.roles?.forEach {
            if (!it.role.isNullOrBlank()) {
                list.add(SimpleGrantedAuthority(it.role))
            }
        }
        authorities = list
    }

    /**授权的类型, 一个用户支持多种授权类型*/
    override fun getAuthorities(): Collection<GrantedAuthority> {
        return authorities
    }

    override fun getPassword(): String {
        return authEntity.password!!
    }

    override fun getUsername(): String {
        return authEntity.username!!
    }

    override fun isAccountNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonLocked(): Boolean {
        return true
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun isEnabled(): Boolean {
        return authEntity.enable
    }

    override fun toString(): String {
        return authEntity.toString()
    }
}