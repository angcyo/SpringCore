package com.angcyo.spring.security.bean

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/05/28
 */

@ApiModel("登录请求的参数")
class AuthReqBean {

    @ApiModelProperty("需要登录的账号, 可以是邮箱/账号/openId等")
    var account: String? = null

    @ApiModelProperty("如果是账号登录, 则账号密码放这里")
    var password: String? = null

    @ApiModelProperty("如果是验证码登录, 则验证码放这里")
    var code: Long? = null

    @ApiModelProperty("客户端类型,默认是web. android/ios/web/等. 请在请求头中指定.")
    var clientType: String? = ClientType.Web.value

    @ApiModelProperty("授权类型,默认是password. password/code等")
    var grantType: String? = GrantType.Password.value
}