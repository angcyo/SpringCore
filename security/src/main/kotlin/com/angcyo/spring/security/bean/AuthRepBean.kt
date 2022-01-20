package com.angcyo.spring.security.bean

import com.angcyo.spring.mybatis.plus.auto.annotation.AutoFill
import com.angcyo.spring.mybatis.plus.auto.param.IAutoParam
import com.angcyo.spring.security.table.UserAccountTable
import com.angcyo.spring.security.table.UserTable
import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/05/29
 */

@ApiModel("登录返回的数据")
class AuthRepBean : UserTable(), IAutoParam {

    @ApiModelProperty("授权成功,返回的token")
    var token: String? = null

    @ApiModelProperty("用于登录的账号")
    @AutoFill(spEL = "@userAccountService.getUserAccountList(id)")
    var accountList: List<UserAccountTable>? = null

    @AutoFill(spEL = "@userInfoService.queryUserInfo(id)")
    @ApiModelProperty("用户信息")
    var userInfo: UserInfoRepBean? = null
}