package com.angcyo.spring.security.service

import com.angcyo.spring.base.oneDay
import com.angcyo.spring.redis.Redis
import com.angcyo.spring.security.SecurityConstants
import com.angcyo.spring.security.controller.RegisterBean
import com.angcyo.spring.security.entity.AuthEntity
import com.angcyo.spring.security.entity.RoleEntity
import com.angcyo.spring.security.entity.Roles
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/11/07
 *
 * 授权服务, 保存用户, 查找用户, 设置用户角色
 */

@Service
class AuthService {

    @Autowired
    lateinit var authRepository: AuthRepository

    @Autowired
    lateinit var roleRepository: RoleRepository

    @Autowired
    lateinit var passwordEncoder: PasswordEncoder

    @Autowired
    lateinit var redis: Redis

    /**[rawPassword] 实际的密码,比如angcyo
     * [encodedPassword] 加密后的密码, 数据库中的密码*/
    fun validatePassword(rawPassword: CharSequence?, encodedPassword: String?): Boolean {
        if (rawPassword.isNullOrEmpty() || encodedPassword.isNullOrEmpty()) {
            return false
        }
        return passwordEncoder.matches(rawPassword, encodedPassword)
    }

    /**是否可以注册
     * [first] 是否可以注册
     * [second] 不可以注册的原因*/
    fun canRegister(bean: RegisterBean): Pair<Boolean, String?> {
        val isUsernameExist = bean.username.isNullOrBlank() || authRepository.existsByUsername(bean.username!!)
        if (isUsernameExist) {
            return false to "用户名已存在"
        }
        return true to null
    }

    /**注册用户, 写入数据库*/
    @Transactional
    fun register(bean: RegisterBean): AuthEntity? {
        val entity = authRepository.save(AuthEntity().apply {
            username = bean.username
            password = passwordEncoder.encode(bean.password)
        })
        entity.roles = listOf(roleRepository.save(RoleEntity().apply {
            authId = entity.id
            role = Roles.USER
            des = Roles.USER
        }))
        return entity
    }

    /**[UserDetailsService]需要获取的用户, 系统会自动校验密码是否匹配
     * 这里只需要从数据库查找授权用户信息, 并返回即可*/
    fun loadAuth(username: String): AuthEntity? {
        val authEntity = authRepository.findByUsername(username)
        authEntity?.apply {
            roles = roleRepository.findAllByAuthId(id)
        }
        return authEntity
    }

    /**检查用户的token, 是否和redis里面的一样
     * [token] 支持包含/不包含前缀的token*/
    fun _checkTokenValid(username: String, token: String): Boolean {
        val redisToken = redis["TOKEN$username"]
        if (token.startsWith(SecurityConstants.TOKEN_PREFIX)) {
            return SecurityConstants.TOKEN_PREFIX + redisToken == token
        }
        return redisToken == token
    }

    /**[token] 不含前缀的token*/
    fun _loginEnd(username: String, token: String) {
        //保存token, 一天超时
        redis["TOKEN$username", token] = oneDay
    }

    /**退出登录*/
    fun _logoutEnd(username: String) {
        SecurityContextHolder.clearContext()
        redis.del("TOKEN$username")
    }
}