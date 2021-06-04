package com.angcyo.spring.security.controller

import com.angcyo.spring.base.data.Result
import com.angcyo.spring.base.data.ok
import com.angcyo.spring.base.data.result
import com.angcyo.spring.base.extension.apiError
import com.angcyo.spring.base.servlet.body
import com.angcyo.spring.base.servlet.send
import com.angcyo.spring.security.SecurityConstants
import com.angcyo.spring.security.bean.*
import com.angcyo.spring.security.service.AuthService
import com.angcyo.spring.security.service.codeKey
import com.angcyo.spring.util.ImageCode
import com.angcyo.spring.util.L
import io.swagger.annotations.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.validation.BindingResult
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
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
@Api(tags = ["授权相关的控制器"])
class AuthController {

    @Autowired
    lateinit var authService: AuthService

    @GetMapping(SecurityConstants.AUTH_REGISTER_CODE_URL)
    @ApiOperation("获取注册时的图形验证码")
    @ApiImplicitParams(
        ApiImplicitParam(name = "clientUuid", value = "客户端的UUID", required = true, dataTypeClass = String::class),
        ApiImplicitParam(name = "type", value = "验证码类型", required = true, dataTypeClass = Int::class),
        ApiImplicitParam(name = "l", value = "验证码的长度", required = false, dataTypeClass = Int::class),
        ApiImplicitParam(name = "w", value = "验证码的宽度", required = false, dataTypeClass = Int::class),
        ApiImplicitParam(name = "h", value = "验证码的高度", required = false, dataTypeClass = Int::class)
    )
    fun getAuthImageCode(request: HttpServletRequest, response: HttpServletResponse) {

        val type: Int = request.getParameter("type")?.toIntOrNull() ?: CodeType.Register.value
        val length: Int = request.getParameter("l")?.toIntOrNull() ?: 4
        val width: Int = request.getParameter("w")?.toIntOrNull() ?: 80
        val height: Int = request.getParameter("h")?.toIntOrNull() ?: 28

        val pair = ImageCode.generate(length, width, height)

        //根据session id, 将code 存到redis
        authService.setImageCode(request.codeKey(), type, pair.first)

        //将VerifyCode绑定session
        request.session.setAttribute("code.${type}", pair.first)
        //设置响应头
        response.setHeader("Pragma", "no-cache")
        //设置响应头
        response.setHeader("Cache-Control", "no-cache")
        //在代理服务器端防止缓冲
        response.setDateHeader("Expires", 0)
        //设置响应内容类型
        response.send(pair.second, type = "image/jpeg")
        L.i("验证码${type}:${pair.first}")
    }

    @PostMapping(SecurityConstants.AUTH_SEND_CODE_URL)
    @ApiOperation("发送验证码")
    fun sendCode(request: HttpServletRequest, @RequestBody req: SendCodeReqBean): Result<Boolean> {

        if (req.type == CodeType.Login.value) {
            if (!authService.accountService.isAccountExist(req.target!!)) {
                apiError("无效的账号")
            }
        }

        return authService.sendCode(request.codeKey(), req.target!!, req.type!!).result()
    }

    /**
     * [org.springframework.web.servlet.mvc.method.annotation.AbstractMessageConverterMethodArgumentResolver.isBindExceptionRequired]
     * */
    @PostMapping(SecurityConstants.AUTH_REGISTER_URL)
    @ApiOperation("注册用户")
    //@ApiImplicitParam(name = "uuid", value = "客户端id", required = true, dataTypeClass = String::class)
    fun register(
        @ApiParam @RequestBody @Validated bean: RegisterReqBean, /*不支持kotlin的data class*/
        bindingResult: BindingResult,/*必须放在模型属性之后, 否则无效*/
        request: HttpServletRequest
    ): Result<Boolean>? {
        return bindingResult.result {
            authService.register(bean) != null
        }
    }

    /**登录接口, 用来生成Swagger文档*/
    @ApiOperation("授权登录")
    @PostMapping(SecurityConstants.AUTH_LOGIN_URL)
    fun login(@ApiParam("授权登录参数") @RequestBody authReqBean: AuthReqBean): Result<AuthRepBean>? {
        return null
    }

    @RequestMapping("/auth/test", method = [RequestMethod.GET, RequestMethod.POST])
    @ApiOperation("授权测试")
    fun test(request: HttpServletRequest): Result<String>? {
        return request.body()?.ok()
    }
}