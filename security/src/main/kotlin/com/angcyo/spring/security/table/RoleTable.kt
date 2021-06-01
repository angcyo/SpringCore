package com.angcyo.spring.security.table

import com.angcyo.spring.mybatis.plus.auto.annotation.AutoQuery
import com.angcyo.spring.mybatis.plus.auto.annotation.AutoWhere
import com.angcyo.spring.mybatis.plus.auto.param.IAutoParam
import com.angcyo.spring.mybatis.plus.table.BaseAuditTable
import com.baomidou.mybatisplus.annotation.TableName
import com.gitee.sunchenbin.mybatis.actable.annotation.Column
import com.gitee.sunchenbin.mybatis.actable.annotation.TableComment

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/05/28
 */

@TableName("role")
@TableComment("用户角色表")
@AutoQuery(updateFailToSave = true)
class RoleTable : BaseAuditTable(), IAutoParam {

    @Column(comment = "角色的名称")
    @AutoWhere
    var name: String? = null

    @Column(comment = "角色的描述")
    var description: String? = null
}