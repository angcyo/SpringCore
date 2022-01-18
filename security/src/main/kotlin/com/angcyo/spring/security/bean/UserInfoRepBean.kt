package com.angcyo.spring.security.bean

import com.angcyo.spring.mybatis.plus.auto.annotation.AutoFill
import com.angcyo.spring.mybatis.plus.auto.param.IAutoParam
import com.angcyo.spring.security.table.RoleTable
import com.angcyo.spring.security.table.UserInfoTable
import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2022/01/18
 */

@ApiModel("用户信息返回的结构")
class UserInfoRepBean : UserInfoTable(), IAutoParam {

    @ApiModelProperty("角色列表")
    @AutoFill(spEL = "@userRoleService.getUserRoleList(userId)")
    var roleList: List<RoleTable>? = null

}