package com.angcyo.spring.security

import com.angcyo.spring.security.entity.AuthEntity
import com.angcyo.spring.security.jwt.JwtUser
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
    override fun loadUserByUsername(name: String): UserDetails {
        val entity: AuthEntity = authService.loadAuth(name) ?: throw UsernameNotFoundException("未找到用户:$name")
        return JwtUser(entity)
    }
}