package com.angcyo.spring.security.bean

import com.angcyo.spring.mybatis.plus.auto.AutoType
import com.angcyo.spring.mybatis.plus.auto.annotation.AutoFill
import com.angcyo.spring.mybatis.plus.auto.annotation.AutoQuery
import com.angcyo.spring.mybatis.plus.auto.annotation.Query
import com.angcyo.spring.mybatis.plus.auto.param.BaseAutoPageParam
import com.angcyo.spring.security.service.PermissionService
import com.angcyo.spring.security.service.RolePermissionService
import com.angcyo.spring.security.service.UserRoleService
import com.angcyo.spring.security.table.PermissionTable
import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/06/03
 */

@ApiModel("用户权限查询参数")
class UserPermissionQueryBean : BaseAutoPageParam() {

    @AutoQuery(
        queries = [
            Query(type = AutoType.QUERY, column = "id"),
        ]
    )
    @ApiModelProperty("需要查询的用户id")
    var userId: Long? = null

    @AutoFill(
        service = UserRoleService::class, queryParamField = "userId", queryColumn = "userId", targetField = "roleId",
        des = "先通过用户id, 查询到对应的角色id列表"
    )
    @ApiModelProperty("用户对应的角色id列表")
    var roleIdList: List<Long>? = null

    @AutoFill(
        service = RolePermissionService::class,
        queryParamField = "roleIdList",
        queryColumn = "roleId",
        targetField = "permissionId",
        des = "再通过角色id, 查询到对应的权限id列表"
    )
    @ApiModelProperty("用户对应的权限id列表")
    var permissionIdList: List<Long>? = null

    @AutoFill(
        service = PermissionService::class, queryParamField = "permissionIdList", queryColumn = "id", targetField = "",
        des = "再通过权限id列表, 查询对应的权限信息列表"
    )
    @ApiModelProperty("用户对应的权限列表")
    var permissionList: List<PermissionTable>? = null
}