package com.angcyo.spring.security.service

import com.angcyo.spring.base.aspect.LogMethodTime
import com.angcyo.spring.mybatis.plus.auto.BaseAutoMybatisServiceImpl
import com.angcyo.spring.mybatis.plus.auto.annotation.AutoFillRef
import com.angcyo.spring.mybatis.plus.columnName
import com.angcyo.spring.security.bean.UserRoleQueryBean
import com.angcyo.spring.security.bean.UserRoleSaveBean
import com.angcyo.spring.security.mapper.IUserRoleMapper
import com.angcyo.spring.security.table.RoleTable
import com.angcyo.spring.security.table.UserRoleReTable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/05/28
 */

@Service
class UserRoleService : BaseAutoMybatisServiceImpl<IUserRoleMapper, UserRoleReTable>() {

    /**充值用户对应的角色*/
    @LogMethodTime
    @Transactional
    fun resetUserRole(list: List<UserRoleSaveBean>) {
        autoReset(list)
    }

    @Transactional
    fun resetUserRole(userId: Long, roleIdList: List<Long>) {
        val roleList = mutableListOf<UserRoleReTable>()
        roleIdList.forEach {
            roleList.add(UserRoleReTable().apply {
                this.userId = userId
                this.roleId = it
            })
        }
        resetFrom(roleList, UserRoleReTable::userId.columnName(), userId, UserRoleReTable::roleId.name)
    }

    /**用户是否有指定的角色名*/
    fun haveRole(userId: Long, roleName: String): Boolean {
        return getUserRoleList(userId).find { it.name == roleName } != null
    }

    /**获取用户对应的角色列表*/
    @AutoFillRef("com.angcyo.spring.security.bean.UserDetail.getUserRoleList")
    fun getUserRoleList(userId: Long): List<RoleTable> {
        val queryBean = UserRoleQueryBean().apply {
            this.userId = userId
        }
        autoFill(queryBean)
        return queryBean.roleList ?: emptyList()

        /*val userRoleList = autoList(UserRoleQueryBean().apply {
            this.userId = userId
        })
        val roleIdList = userRoleList.mapTo(mutableListOf()) {
            it.roleId
        }
        return roleService.list(roleService.queryWrapper().apply {
            `in`(RoleTable::id.columnName(), roleIdList)
        })*/
    }
}