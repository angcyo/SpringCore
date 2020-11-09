package com.angcyo.spring.security.service

import com.angcyo.spring.security.controller.RegisterBean
import com.angcyo.spring.security.entity.AuthEntity
import com.angcyo.spring.security.entity.RoleEntity
import com.angcyo.spring.security.entity.Roles
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.UserDetailsService
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

    fun login(username: String, password: String) {

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
            password = bean.password
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
        authEntity?.let {
            //roleRepository
        }
        return authEntity
    }
}