package com.angcyo.spring.security.bean

import com.angcyo.spring.mybatis.plus.auto.annotation.AutoWhere
import com.angcyo.spring.mybatis.plus.auto.param.BaseAutoPageParam

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/05/29
 */

/**用户表的查询参数
 * [com.angcyo.spring.security.table.UserTable]*/
class UserQueryParam : BaseAutoPageParam() {
    @AutoWhere
    var nickname: String? = null
}