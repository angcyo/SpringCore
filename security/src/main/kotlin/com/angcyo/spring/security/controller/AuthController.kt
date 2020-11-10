package com.angcyo.spring.security.controller

import com.angcyo.spring.base.data.Result
import com.angcyo.spring.base.data.error
import com.angcyo.spring.base.data.result
import com.angcyo.spring.base.servlet.send
import com.angcyo.spring.base.str
import com.angcyo.spring.base.util.ImageCode
import com.angcyo.spring.base.util.L
import com.angcyo.spring.redis.Redis
import com.angcyo.spring.security.SecurityConstants
import com.angcyo.spring.security.entity.AuthEntity
import com.angcyo.spring.security.service.AuthService
import io.swagger.annotations.Api
import io.swagger.annotations.ApiImplicitParam
import io.swagger.annotations.ApiImplicitParams
import io.swagger.annotations.ApiOperation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.validation.BindingResult
import org.springframework.validation.annotation.Validated
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
@Api(value = "授权控制器value")
class AuthController {

    @Autowired
    lateinit var authService: AuthService

    @Autowired
    lateinit var redis: Redis

    private fun setImageCode(request: HttpServletRequest, code: String) {
        //根据session id, 将code 存到redis
        request.getSession(true)?.apply {
            redis["IMAGE${id}CODE", code] = 1 * 60
        }
    }

    private fun getImageCode(request: HttpServletRequest): String? {
        return request.getSession(false)?.run {
            redis["IMAGE${id}CODE"].str()
        }
    }

    private fun clearImageCode(request: HttpServletRequest) {
        request.getSession(false)?.run {
            redis.del("IMAGE${id}CODE")
        }
    }

    @GetMapping(SecurityConstants.AUTH_REGISTER_CODE_URL)
    @ApiOperation("获取注册时的图形验证码")
    @ApiImplicitParams(
            ApiImplicitParam(name = "l", value = "验证码的长度"),
            ApiImplicitParam(name = "w", value = "验证码的宽度"),
            ApiImplicitParam(name = "h", value = "验证码的高度")
    )
    fun imageCode(request: HttpServletRequest, response: HttpServletResponse) {

        val length: Int = request.getParameter("l")?.toIntOrNull() ?: 4
        val width: Int = request.getParameter("w")?.toIntOrNull() ?: 80
        val height: Int = request.getParameter("h")?.toIntOrNull() ?: 28

        val pair = ImageCode.generate(length, width, height)

        //根据session id, 将code 存到redis
        setImageCode(request, pair.first)

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

    /**
     * [org.springframework.web.servlet.mvc.method.annotation.AbstractMessageConverterMethodArgumentResolver.isBindExceptionRequired]
     * */
    @PostMapping(SecurityConstants.AUTH_REGISTER_URL)
    @ApiOperation("注册用户")
    fun register(@RequestBody @Validated bean: RegisterBean,
                 bindingResult: BindingResult,/*必须放在第2个参数上, 否则无效*/
                 request: HttpServletRequest): Result<AuthEntity?>? {
        return bindingResult.result {

            if (bean.type == WebType.value) {
                //web 注册类型, 需要验证验证码

                if (bean.code.isNullOrBlank() || bean.code != getImageCode(request)) {
                    return "验证码错误".error()
                }
            }

            val pair = authService.canRegister(bean)
            if (!pair.first) {
                clearImageCode(request)
                return pair.second.error()
            }

            val entity = authService.register(bean)
            entity
        }
    }
}