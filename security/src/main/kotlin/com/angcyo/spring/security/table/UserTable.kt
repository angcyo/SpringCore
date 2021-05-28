package com.angcyo.spring.security.table

import com.angcyo.spring.mybatis.plus.table.BaseAuditTable
import com.gitee.sunchenbin.mybatis.actable.annotation.Column
import com.gitee.sunchenbin.mybatis.actable.annotation.Table

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/05/28
 */

@Table(name = "user", comment = "用户表, 记录用户的一些信息")
class UserTable : BaseAuditTable() {

    @Column(comment = "用户名称")
    var username: String? = null

    @Column(comment = "用户密码, 密文")
    var password: String? = null

    @Column(comment = "用户的描述")
    var description: String? = null

    @Column(comment = "用户的状态, >0表示用户可用, <0表示对应的不可用状态码")
    var state: Int? = null
}