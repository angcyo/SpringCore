package com.angcyo.spring.security.bean

import com.angcyo.spring.mybatis.plus.auto.annotation.AutoFill
import com.angcyo.spring.mybatis.plus.auto.param.IAutoParam
import com.angcyo.spring.security.service.AccountService
import com.angcyo.spring.security.service.PermissionService
import com.angcyo.spring.security.service.UserRoleService
import com.angcyo.spring.security.table.AccountTable
import com.angcyo.spring.security.table.PermissionTable
import com.angcyo.spring.security.table.RoleTable
import com.angcyo.spring.security.table.UserTable

/**
 * 用户详情
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/06/04
 */

class UserDetail : IAutoParam {

    /**用户表的数据*/
    var userTable: UserTable? = null

    /**用户的账号列表*/
    @AutoFill(
        service = AccountService::class,
        queryColumn = "userId",
        queryParamField = "userTable.id",
        targetField = ""
    )
    var userAccountList: List<AccountTable>? = null

    /**用户对应的角色列表*/
    @AutoFill(
        service = UserRoleService::class,
        serviceMethod = "getUserRoleList",
        methodParamField = "userTable.id",
        targetField = ""
    )
    var userRoleList: List<RoleTable>? = null

    /**用户对应的权限列表*/
    @AutoFill(
        service = PermissionService::class,
        serviceMethod = "getUserPermission",
        methodParamField = "userTable.id",
        targetField = ""
    )
    var userPermissionList: List<PermissionTable>? = null

    /**额外存储的数据*/
    var data: Any? = null

    /**额外存储的数据映射*/
    var map: HashMap<String, Any?>? = null
}