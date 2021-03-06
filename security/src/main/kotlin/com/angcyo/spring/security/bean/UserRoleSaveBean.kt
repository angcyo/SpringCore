package com.angcyo.spring.security.bean

import com.angcyo.spring.mybatis.plus.auto.annotation.AutoFill
import com.angcyo.spring.mybatis.plus.auto.annotation.AutoResetBy
import com.angcyo.spring.mybatis.plus.auto.param.IAutoParam
import com.angcyo.spring.security.service.RoleService

/**
 * [com.angcyo.spring.security.table.UserRoleReTable]
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/06/02
 */
class UserRoleSaveBean : IAutoParam {

    @AutoResetBy
    var userId: Long? = null

    @AutoFill(service = RoleService::class, queryColumn = "code", queryParamField = "roleCode")
    var roleId: Long? = null

    var roleCode: String? = null
}