package com.angcyo.spring.security.service

import com.angcyo.spring.mybatis.plus.auto.BaseAutoMybatisServiceImpl
import com.angcyo.spring.mybatis.plus.auto.annotation.AutoFillRef
import com.angcyo.spring.mybatis.plus.c
import com.angcyo.spring.mybatis.plus.columnName
import com.angcyo.spring.mybatis.plus.tableName
import com.angcyo.spring.security.mapper.IPermissionMapper
import com.angcyo.spring.security.table.PermissionTable
import com.angcyo.spring.security.table.RolePermissionReTable
import com.angcyo.spring.security.table.UserRoleReTable
import org.springframework.stereotype.Service

/**
 * [com.angcyo.spring.security.controller.PermissionManager.havePermission]
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/05/28
 */

@Service
class PermissionService : BaseAutoMybatisServiceImpl<IPermissionMapper, PermissionTable>() {

    /**获取指定用户的权限集合*/
    @AutoFillRef("com.angcyo.spring.security.bean.UserDetail.getUserPermissionList")
    fun getUserPermission(userId: Long): List<PermissionTable> {
        val list = list(queryWrapper().apply {
            //先获取用户id 对应的 角色id
            val roleSql =
                "SELECT ${UserRoleReTable::roleId.columnName()} FROM ${UserRoleReTable::class.tableName()} WHERE ${UserRoleReTable::userId.columnName()} = $userId"
            //在根据角色id 获取对应的 权限id 集合
            val subSql =
                "(SELECT ${RolePermissionReTable::permissionId.columnName()} FROM ${RolePermissionReTable::class.tableName()} WHERE ${UserRoleReTable::roleId.columnName()} in ($roleSql))"
            //在根据权限id, 查询对应的权限信息
            inSql(PermissionTable::id.columnName(), subSql)
        })

        /*val list2 = selectFrom(SelectFrom().apply {
            fromTable = RolePermissionReTable::class.tableName()
            fromColumn = RolePermissionReTable::permissionId.columnName()
            fromWhere = UserRoleReTable::roleId.columnName()

            from = SelectFrom().apply {
                fromTable = UserRoleReTable::class.tableName()
                fromColumn = UserRoleReTable::roleId.columnName()
                fromWhere = "${UserRoleReTable::userId.queryColumn()} = $userId"
            }
        })*/

        return list
    }

    fun queryPermission(code: String?): PermissionTable? {
        return listQueryOne {
            eq(PermissionTable::code.c(), code)
        }
    }

    @AutoFillRef("com.angcyo.spring.spmt.api.init.RolePermissionUpdateBean")
    fun queryPermissionId(code: String?): Long? {
        return queryPermission(code)?.id
    }
}