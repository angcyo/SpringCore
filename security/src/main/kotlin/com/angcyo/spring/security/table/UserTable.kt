package com.angcyo.spring.security.table

import com.angcyo.spring.mybatis.plus.table.BaseAuditTable
import com.baomidou.mybatisplus.annotation.TableName
import com.fasterxml.jackson.annotation.JsonIgnore
import com.gitee.sunchenbin.mybatis.actable.annotation.Column
import com.gitee.sunchenbin.mybatis.actable.annotation.TableComment
import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/05/28
 */

@TableName("user")
@TableComment("用户表, 记录用户的关键信息")
@ApiModel("用户表, 记录用户的关键信息")
class UserTable : BaseAuditTable() {

    @Column(comment = "用户密码, 密文后")
    @ApiModelProperty("用户密码, 明文")
    @JsonIgnore
    var password: String? = null

    @Column(comment = "用户的状态, >0表示用户可用, <0表示对应的不可用状态码")
    @ApiModelProperty("用户的状态, >0表示用户可用, <0表示对应的不可用状态码")
    var state: Int? = null
}