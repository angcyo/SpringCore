package com.angcyo.spring.security.bean

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/11/07
 *
 * 注册需要用的参数, Data Class 编译之后会丢失java注解, 所以换成 class
 */

@ApiModel("注册账号请求参数")
class RegisterReqBean {

    @ApiModelProperty("注册的账号, 也是默认的用户昵称", required = true)
    @NotBlank(message = "账号不能为空")
    @Size(min = 4, max = 20, message = "账号至少4个字符")
    var account: String? = null

    @ApiModelProperty("注册账号的密码")
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码至少6个字符")
    var password: String? = null

    @ApiModelProperty("注册时的验证码")
    var code: String? = null
}