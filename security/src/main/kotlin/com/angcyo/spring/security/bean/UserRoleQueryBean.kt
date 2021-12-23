package com.angcyo.spring.security.bean

import com.angcyo.spring.mybatis.plus.auto.AutoType
import com.angcyo.spring.mybatis.plus.auto.annotation.AutoFill
import com.angcyo.spring.mybatis.plus.auto.annotation.AutoQuery
import com.angcyo.spring.mybatis.plus.auto.annotation.Query
import com.angcyo.spring.mybatis.plus.auto.param.BaseAutoPageParam
import com.angcyo.spring.security.service.RoleService
import com.angcyo.spring.security.service.UserRoleService
import com.angcyo.spring.security.table.RoleTable
import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/06/03
 */

@ApiModel("用户角色查询参数")
class UserRoleQueryBean : BaseAutoPageParam() {

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
        service = RoleService::class, queryParamField = "roleIdList", queryColumn = "id", targetField = "",
        des = "再通过角色id列表, 查询对应的角色信息列表"
    )
    @ApiModelProperty("用户对应的角色列表")
    var roleList: List<RoleTable>? = null
}