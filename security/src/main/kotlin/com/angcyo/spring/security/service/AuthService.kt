package com.angcyo.spring.security.service

import com.angcyo.spring.security.controller.RegisterBean
import com.angcyo.spring.security.entity.AuthEntity
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

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

    fun register(bean: RegisterBean): AuthEntity? {
        return AuthEntity()
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