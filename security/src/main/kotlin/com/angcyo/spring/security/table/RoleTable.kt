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
 * 角色表, 记录了有哪些角色.
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/05/28
 */

@TableName("role")
@TableComment("用户角色表")
@AutoQueryConfig(updateFailToSave = true)
class RoleTable : BaseAuditTable(), IAutoParam {

    @AutoQuery(
        queries = [
            Query(type = AutoType.QUERY),
            Query(type = AutoType.UPDATE),
        ]
    )
    @AutoUpdateBy
    @Column(comment = "角色的代码")
    var code: String? = null

    @Column(comment = "角色的名称")
    var name: String? = null

    @Column(comment = "角色的描述")
    var des: String? = null
}