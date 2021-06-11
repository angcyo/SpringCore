package com.angcyo.spring.security.table

import com.angcyo.spring.mybatis.plus.auto.annotation.AutoQueryConfig
import com.angcyo.spring.mybatis.plus.auto.annotation.AutoUpdateBy
import com.angcyo.spring.mybatis.plus.auto.annotation.AutoQuery
import com.angcyo.spring.mybatis.plus.auto.param.IAutoParam
import com.angcyo.spring.mybatis.plus.table.BaseAuditTable
import com.baomidou.mybatisplus.annotation.TableName
import com.gitee.sunchenbin.mybatis.actable.annotation.Column
import com.gitee.sunchenbin.mybatis.actable.annotation.TableComment

/**
 * [com.angcyo.spring.security.jwt.JwtPermissionListener]
 * [com.angcyo.spring.security.service.PermissionManagerService.havePermission]
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/05/28
 */

@TableName("permission")
@TableComment("权限表, 角色可以访问的api资源")
@AutoQueryConfig(updateFailToSave = true)
class PermissionTable : BaseAuditTable(), IAutoParam {

    @Column(comment = "权限的名称")
    @AutoQuery
    @AutoUpdateBy
    var name: String? = null

    @Column(comment = "权限的描述")
    var des: String? = null

    @Column(comment = "权限能访问的资源, 支持正则匹配. 如果为空则全部不允许访问")
    var permit: String? = null

    @Column(comment = "权限不能访问的资源, 支持正则匹配")
    var deny: String? = null

    @Column(comment = "严格的权限校验规则, 只要[deny]不通过, 权限就不通过")
    var strict: Boolean? = null
}