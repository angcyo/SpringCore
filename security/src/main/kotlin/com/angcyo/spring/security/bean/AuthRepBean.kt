package com.angcyo.spring.security.bean

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/05/29
 */

@ApiModel("登录返回的数据")
class AuthRepBean {

    @ApiModelProperty("用户的id")
    var id: Long? = null

    @ApiModelProperty("用户的昵称")
    var nickname: String? = null

    @ApiModelProperty("授权成功,返回的token")
    var token: String? = null
}