package com.angcyo.spring.security.table

import com.angcyo.spring.mybatis.plus.table.BaseAuditTable
import com.baomidou.mybatisplus.annotation.TableName
import com.gitee.sunchenbin.mybatis.actable.annotation.Column
import com.gitee.sunchenbin.mybatis.actable.annotation.TableComment

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/05/28
 */

@TableName("role_permission")
@TableComment("角色和权限的关联表, 角色有那些权限")
class RolePermissionReTable : BaseAuditTable() {

    @Column(comment = "角色的id")
    var roleId: Long? = null

    @Column(comment = "权限的id")
    var permissionId: Long? = null
}