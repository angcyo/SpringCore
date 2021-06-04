package com.angcyo.spring.security.controller

import com.angcyo.spring.base.data.Result
import com.angcyo.spring.base.data.result
import com.angcyo.spring.security.bean.UserPermissionQueryBean
import com.angcyo.spring.security.bean.UserRoleQueryBean
import com.angcyo.spring.security.jwt.currentUser
import com.angcyo.spring.security.service.UserService
import com.angcyo.spring.security.table.PermissionTable
import com.angcyo.spring.security.table.RoleTable
import io.swagger.annotations.Api
import io.swagger.annotations.ApiImplicitParam
import io.swagger.annotations.ApiOperation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/06/04
 */

@RestController
@RequestMapping("/user")
@Api(tags = ["用户相关的控制器"])
class UserController {

    @Autowired
    lateinit var userService: UserService

    @GetMapping("/listRole")
    @ApiOperation("获取用户对应的角色列表")
    @ApiImplicitParam(name = "userId", value = "查询的用户id,默认自己")
    fun getUserRoleList(@RequestParam(required = false, name = "userId") id: Long? = null): Result<List<RoleTable>> {
        val userId = id ?: currentUser().userTable?.id
        val query = UserRoleQueryBean().apply {
            this.userId = userId
        }
        userService.autoFill(query)
        return query.roleList.result()
    }

    @GetMapping("/listPermission")
    @ApiOperation("获取用户对应的权限列表")
    @ApiImplicitParam(name = "userId", value = "查询的用户id,默认自己")
    fun getUserPermissionList(
        @RequestParam(
            required = false,
            name = "userId"
        ) id: Long? = null
    ): Result<List<PermissionTable>> {
        val userId = id ?: currentUser().userTable?.id
        val query = UserPermissionQueryBean().apply {
            this.userId = userId
        }
        userService.autoFill(query)
        return query.permissionList.result()
    }
}