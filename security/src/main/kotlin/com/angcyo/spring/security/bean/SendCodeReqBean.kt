package com.angcyo.spring.security.bean

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/06/03
 */

@ApiModel("发送验证码请求参数")
class SendCodeReqBean {

    @ApiModelProperty("发送的目标(手机号/邮箱)", required = true)
    var target: String? = null

    @ApiModelProperty("验证码的长度(默认6位)", required = true)
    var lenght: Int? = null

    @ApiModelProperty("验证码类型(1:注册,2:登录)", required = true, allowableValues = "1,2")
    var type: Int? = null

}