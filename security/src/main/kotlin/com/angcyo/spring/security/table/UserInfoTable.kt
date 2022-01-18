package com.angcyo.spring.security.table

import com.angcyo.spring.mybatis.plus.auto.AutoType
import com.angcyo.spring.mybatis.plus.auto.annotation.AutoQuery
import com.angcyo.spring.mybatis.plus.auto.annotation.Query
import com.angcyo.spring.mybatis.plus.table.BaseAuditTable
import com.baomidou.mybatisplus.annotation.TableName
import com.gitee.sunchenbin.mybatis.actable.annotation.Column
import com.gitee.sunchenbin.mybatis.actable.annotation.TableComment
import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty

/**
 * 用户信息表
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2022/01/18
 */

@TableName("user_info")
@TableComment("用户信息表, 记录用户的一些信息")
@ApiModel("用户表, 记录用户的一些信息")
class UserInfoTable : BaseAuditTable() {

    @Column(comment = "对应用户表的id")
    @ApiModelProperty("用户的id")
    @AutoQuery(
        [
            Query(type = AutoType.QUERY),
            Query(type = AutoType.UPDATE, checkNull = true, nullError = "请指定userId"),
        ]
    )
    var userId: Long? = null

    @Column(comment = "用户昵称")
    @ApiModelProperty("用户昵称")
    var nickname: String? = null

    @Column(comment = "姓名")
    @ApiModelProperty("姓名")
    var name: String? = null

    @Column(comment = "头像url地址")
    @ApiModelProperty("头像url地址")
    var avatar: String? = null

    @Column(comment = "电话")
    @ApiModelProperty("电话")
    var phone: String? = null

    @Column(comment = "邮箱")
    @ApiModelProperty("邮箱")
    var email: String? = null

    @Column(comment = "性别: 1:男 2:女")
    @ApiModelProperty("性别: 1:男 2:女")
    var sex: Int? = null

    @Column(comment = "用户的描述")
    @ApiModelProperty("用户的描述")
    var des: String? = null
}