package com.angcyo.spring.security.table

import com.angcyo.spring.mybatis.plus.auto.AutoType
import com.angcyo.spring.mybatis.plus.auto.annotation.*
import com.angcyo.spring.mybatis.plus.auto.param.IAutoParam
import com.angcyo.spring.mybatis.plus.table.BaseAuditTable
import com.baomidou.mybatisplus.annotation.TableName
import com.gitee.sunchenbin.mybatis.actable.annotation.Column
import com.gitee.sunchenbin.mybatis.actable.annotation.TableComment

/**
 * [com.angcyo.spring.security.jwt.JwtPermissionListener]
 * [com.angcyo.spring.security.service.PermissionManagerService.havePermission]
 *
 * 查询接口命名规则:
 *  查询详情: query.*$
 *  所有列表: list.*$
 *  分页列表: page.*$
 *
 * 操作数据接口命名规则:
 *   新增: save.*$
 *   更新: update.*$
 *   软删: delete.*
 *   硬删: remove.*$
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/05/28
 */

@TableName("permission")
@TableComment("权限表, 角色可以访问的api资源")
@AutoQueryConfig(updateFailToSave = true)
class PermissionTable : BaseAuditTable(), IAutoParam {

    @AutoQuery(
        queries = [
            Query(type = AutoType.UPDATE),
        ]
    )
    @AutoUpdateBy
    @Column(comment = "权限的代码")
    var code: String? = null

    @Column(comment = "权限的名称")
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