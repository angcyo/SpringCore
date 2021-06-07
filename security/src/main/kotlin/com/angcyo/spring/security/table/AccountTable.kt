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

@TableName("account")
@TableComment("登录账户表, 可以是账号/邮箱/手机号/openId等, 账号表关联用户表")
class AccountTable : BaseAuditTable() {

    @Column(comment = "账号的名称, 比如邮箱/手机号等")
    var name: String? = null

    @Column(comment = "账号的描述")
    var des: String? = null

    @Column(comment = "对应用户表的id")
    var userId: Long? = null
}