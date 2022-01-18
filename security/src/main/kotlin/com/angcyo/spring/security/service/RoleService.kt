package com.angcyo.spring.security.service

import com.angcyo.spring.mybatis.plus.auto.BaseAutoMybatisServiceImpl
import com.angcyo.spring.mybatis.plus.auto.annotation.AutoFillRef
import com.angcyo.spring.mybatis.plus.c
import com.angcyo.spring.security.mapper.IRoleMapper
import com.angcyo.spring.security.table.RoleTable
import org.springframework.stereotype.Service

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/05/28
 */

@Service
class RoleService : BaseAutoMybatisServiceImpl<IRoleMapper, RoleTable>() {

    fun queryRole(code: String?): RoleTable? {
        return listQueryOne {
            eq(RoleTable::code.c(), code)
        }
    }

    @AutoFillRef("com.angcyo.spring.spmt.api.init.RolePermissionUpdateBean")
    fun queryRoleId(code: String?): Long? {
        return queryRole(code)?.id
    }

}