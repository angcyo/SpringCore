package com.angcyo.spring.security.controller

import com.angcyo.spring.base.servlet.send
import com.angcyo.spring.base.util.ImageCode
import com.angcyo.spring.base.util.L
import com.angcyo.spring.core.data.Result
import com.angcyo.spring.core.data.ok
import com.angcyo.spring.redis.Redis
import com.angcyo.spring.security.SecurityConstants
import com.angcyo.spring.security.entity.AuthEntity
import com.angcyo.spring.security.service.AuthService
import io.swagger.annotations.ApiOperation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

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

    @Autowired
    lateinit var redis: Redis

    @GetMapping(SecurityConstants.AUTH_REGISTER_CODE_URL)
    @ApiOperation("获取注册时的验证码")
    fun imageCode(request: HttpServletRequest, response: HttpServletResponse) {

        val length: Int = request.getParameter("l")?.toIntOrNull() ?: 4
        val width: Int = request.getParameter("w")?.toIntOrNull() ?: 80
        val height: Int = request.getParameter("h")?.toIntOrNull() ?: 28

        val pair = ImageCode.generate(length, width, height)

        //根据session id, 将code 存到redis
        request.getSession(true)?.apply {
            redis[id, pair.first] = 5 * 60
        }

        //将VerifyCode绑定session
        request.session.setAttribute("code", pair.first)
        //设置响应头
        response.setHeader("Pragma", "no-cache")
        //设置响应头
        response.setHeader("Cache-Control", "no-cache")
        //在代理服务器端防止缓冲
        response.setDateHeader("Expires", 0)
        //设置响应内容类型
        response.send(pair.second, type = "image/jpeg")
        L.i("验证码:${pair.first}")
    }

    @PostMapping(SecurityConstants.AUTH_REGISTER_URL)
    fun register(@RequestBody bean: RegisterBean): Result<AuthEntity> {
        val entity = authService.register(bean)
        return entity.ok()
    }
}