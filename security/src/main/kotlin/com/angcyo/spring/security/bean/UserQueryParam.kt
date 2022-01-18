package com.angcyo.spring.security.bean

import com.angcyo.spring.mybatis.plus.auto.AutoType
import com.angcyo.spring.mybatis.plus.auto.annotation.AutoQuery
import com.angcyo.spring.mybatis.plus.auto.annotation.Query
import com.angcyo.spring.mybatis.plus.auto.param.BaseAutoPageParam
import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/05/29
 */

/**用户表的查询参数
 * [com.angcyo.spring.security.table.UserTable]*/
@ApiModel("查询用户信息的结构")
class UserQueryParam : BaseAutoPageParam() {

    @AutoQuery(
        [
            Query(type = AutoType.QUERY),
        ]
    )
    @ApiModelProperty("根据用户名id")
    var userId: String? = null

    @AutoQuery(
        [
            Query(type = AutoType.QUERY),
        ]
    )
    @ApiModelProperty("根据用户名查询")
    var nickname: String? = null
}