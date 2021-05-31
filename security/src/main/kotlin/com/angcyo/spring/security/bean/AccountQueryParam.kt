package com.angcyo.spring.security.bean

import com.angcyo.spring.mybatis.plus.auto.annotation.AutoWhere
import com.angcyo.spring.mybatis.plus.auto.param.BaseAutoPageParam

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/05/28
 */

/**帐号表的查询参数
 * [com.angcyo.spring.security.table.AccountTable]*/
class AccountQueryParam : BaseAutoPageParam() {

    @AutoWhere
    var name: String? = null
}