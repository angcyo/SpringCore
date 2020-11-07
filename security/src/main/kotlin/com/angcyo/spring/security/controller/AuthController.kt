package com.angcyo.spring.security.controller

import com.angcyo.spring.core.data.Result
import com.angcyo.spring.core.data.ok
import com.angcyo.spring.security.SecurityConstants
import com.angcyo.spring.security.entity.AuthEntity
import com.angcyo.spring.security.service.AuthService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/11/07
 *
 * 注册控制器
 */

@RestController
class AuthController {

    @Autowired
    lateinit var authService: AuthService

    @PostMapping(SecurityConstants.AUTH_REGISTER_URL)
    fun register(@RequestBody bean: RegisterBean): Result<AuthEntity> {
        val entity = authService.register(bean)
        return entity.ok()
    }
}