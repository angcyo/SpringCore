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

@TableName("permission")
@TableComment("权限表, 角色可以访问的api资源")
class PermissionTable : BaseAuditTable() {

    @Column(comment = "权限的名称")
    var name: String? = null

    @Column(comment = "权限的描述")
    var description: String? = null

    @Column(comment = "权限能访问的资源, 支持正则匹配. 如果为空则全部不允许访问")
    var permit: String? = null

    @Column(comment = "权限不能访问的资源, 支持正则匹配")
    var deny: String? = null
}