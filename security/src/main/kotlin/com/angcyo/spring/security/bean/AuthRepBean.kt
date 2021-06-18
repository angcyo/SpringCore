package com.angcyo.spring.security.bean

import com.angcyo.spring.security.table.UserTable
import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/05/29
 */

@ApiModel("登录返回的数据")
class AuthRepBean : UserTable() {

    @ApiModelProperty("授权成功,返回的token")
    var token: String? = null
}