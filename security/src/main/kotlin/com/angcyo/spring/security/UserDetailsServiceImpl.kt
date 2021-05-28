package com.angcyo.spring.security

import com.angcyo.spring.security.entity.AuthEntity
import com.angcyo.spring.security.jwt.JwtUserDetails
import com.angcyo.spring.security.service.AuthService
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

/**
 * @author shuang.kou
 * @description UserDetailsService实现类
 */
@Service
class UserDetailsServiceImpl(val authService: AuthService) : UserDetailsService {
    override fun loadUserByUsername(username: String): UserDetails {
        val entity: AuthEntity = authService.loadAuth(username) ?: throw UsernameNotFoundException("未找到用户:$username")
        return JwtUserDetails(entity)
    }
}