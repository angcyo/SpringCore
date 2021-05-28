package com.angcyo.spring.security.table

import com.angcyo.spring.mybatis.plus.table.BaseAuditTable
import com.gitee.sunchenbin.mybatis.actable.annotation.Column
import com.gitee.sunchenbin.mybatis.actable.annotation.Table

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/05/28
 */

@Table(name = "user_role", comment = "用户和角色的关联表, 用户有那些角色")
class UserRoleReTable : BaseAuditTable() {

    @Column(comment = "用户的id")
    var userId: Long? = null

    @Column(comment = "角色的id")
    var roleId: Long? = null
}