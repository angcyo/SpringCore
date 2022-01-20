package com.angcyo.spring.security.service

import com.angcyo.spring.base.aspect.LogMethodTime
import com.angcyo.spring.base.beanOf
import com.angcyo.spring.base.data.ifError
import com.angcyo.spring.base.data.toBean
import com.angcyo.spring.base.data.toBeanList
import com.angcyo.spring.base.extension.apiError
import com.angcyo.spring.base.toObj
import com.angcyo.spring.mybatis.plus.auto.BaseAutoMybatisServiceImpl
import com.angcyo.spring.mybatis.plus.auto.annotation.AutoFillRef
import com.angcyo.spring.mybatis.plus.auto.param.autoFill
import com.angcyo.spring.security.bean.AuthRepBean
import com.angcyo.spring.security.bean.UserQueryParam
import com.angcyo.spring.security.bean.UserReqBean
import com.angcyo.spring.security.jwt.currentUserId
import com.angcyo.spring.security.mapper.IUserMapper
import com.angcyo.spring.security.table.RoleTable
import com.angcyo.spring.security.table.UserInfoTable
import com.angcyo.spring.security.table.UserTable
import com.baomidou.mybatisplus.core.metadata.IPage
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/05/28
 */

@Service
class UserService : BaseAutoMybatisServiceImpl<IUserMapper, UserTable>() {

    @Autowired
    lateinit var userRoleService: UserRoleService

    /**获取当前登录的用户id*/
    @AutoFillRef("com.angcyo.spring.security.service.annotation.AutoFillUserId")
    fun getCurrentUserId() = currentUserId()

    /**更新用户*/
    @LogMethodTime
    @Transactional
    fun updateUser(req: UserReqBean): Boolean {
        val userId = req.id ?: apiError("请指定用户id")

        //更新用户
        val userTable = req.toObj<UserTable>()
        userTable.password = beanOf(AuthService::class.java).encodePassword(req.password) //密码
        updateById(userTable).ifError("更新失败")

        //更新用户资料
        val userInfo = req.toBean(UserInfoTable::class.java) //UserInfoTable()
        userInfo.userId = userTable.id //帐号关联用户
        beanOf(UserInfoService::class.java).autoUpdate(userInfo).ifError("更新失败")

        if (!req.roleIdList.isNullOrEmpty()) {
            _checkRole(req.roleIdList!!)
            //更新角色
            userRoleService.resetUserRole(userId, req.roleIdList!!).ifError("更新失败")
        }

        return true
    }

    @Autowired
    lateinit var roleService: RoleService

    fun queryRoleList(): List<RoleTable> {
        val list = roleService.autoList()
        return list
    }

    fun _checkRole(roleIdList: List<Long>) {
        val roleList = queryRoleList()
        if (roleList.isEmpty()) {
            apiError("无角色信息")
        }

        roleIdList.forEach { id ->
            if (roleList.find { it.id == id } == null) {
                apiError("无效的角色")
            }
        }
    }

    fun listUser(req: UserQueryParam?): List<AuthRepBean> {
        return autoList(req).toBeanList(AuthRepBean::class.java) {
            autoFill(this@UserService)
        }
    }

    fun pageUser(req: UserQueryParam?): IPage<AuthRepBean> {
        return autoPage2(req, AuthRepBean::class.java) {
            autoFill(this@UserService)
        }
    }
}