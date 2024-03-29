package com.angcyo.spring.security.table

import com.angcyo.spring.mybatis.plus.auto.AutoType
import com.angcyo.spring.mybatis.plus.auto.annotation.AutoQuery
import com.angcyo.spring.mybatis.plus.auto.annotation.AutoQueryConfig
import com.angcyo.spring.mybatis.plus.auto.annotation.AutoUpdateBy
import com.angcyo.spring.mybatis.plus.auto.annotation.Query
import com.angcyo.spring.mybatis.plus.auto.param.IAutoParam
import com.angcyo.spring.mybatis.plus.table.BaseAuditTable
import com.baomidou.mybatisplus.annotation.TableName
import com.gitee.sunchenbin.mybatis.actable.annotation.Column
import com.gitee.sunchenbin.mybatis.actable.annotation.TableComment

/**
 * 角色和权限关联表,
 * 一个角色可以包含多个权限
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/05/28
 */

@TableName("role_permission_re")
@TableComment("角色和权限的关联表, 角色有那些权限")
@AutoQueryConfig(updateFailToSave = true)
class RolePermissionReTable : BaseAuditTable(), IAutoParam {

    @Column(comment = "角色的id")
    @AutoQuery(
        queries = [
            Query(type = AutoType.UPDATE),
        ]
    )
    @AutoUpdateBy
    var roleId: Long? = null

    @Column(comment = "权限的id")
    var permissionId: Long? = null
}