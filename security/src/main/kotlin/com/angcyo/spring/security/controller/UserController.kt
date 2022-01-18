package com.angcyo.spring.security.controller

import com.angcyo.spring.base.data.Result
import com.angcyo.spring.base.data.ok
import com.angcyo.spring.base.data.result
import com.angcyo.spring.security.bean.UserInfoRepBean
import com.angcyo.spring.security.bean.UserPermissionQueryBean
import com.angcyo.spring.security.bean.UserQueryParam
import com.angcyo.spring.security.bean.UserRoleQueryBean
import com.angcyo.spring.security.jwt.currentUser
import com.angcyo.spring.security.jwt.currentUserId
import com.angcyo.spring.security.service.UserInfoService
import com.angcyo.spring.security.service.UserService
import com.angcyo.spring.security.table.PermissionTable
import com.angcyo.spring.security.table.RoleTable
import com.angcyo.spring.security.table.UserInfoTable
import io.swagger.annotations.Api
import io.swagger.annotations.ApiImplicitParam
import io.swagger.annotations.ApiOperation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

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

    @Autowired
    lateinit var userInfoService: UserInfoService

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
        @RequestParam(required = false, name = "userId") id: Long? = null
    ): Result<List<PermissionTable>> {
        val userId = id ?: currentUser().userTable?.id
        val query = UserPermissionQueryBean().apply {
            this.userId = userId
        }
        userService.autoFill(query)
        return query.permissionList.result()
    }

    @PostMapping("/query")
    @ApiOperation("查询用户信息")
    fun queryUserInfo(@RequestBody req: UserQueryParam): Result<UserInfoRepBean>? {
        return userInfoService.queryUserInfo(req).ok()
    }

    @PostMapping("/updateSelf")
    @ApiOperation("更新用户信息")
    fun updateUserInfo(@RequestBody req: UserInfoTable): Result<Boolean>? {
        req.userId = currentUserId()//只能更新自己的用户信息
        return userInfoService.autoUpdate(req).ok()
    }
}