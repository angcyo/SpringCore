package com.angcyo.spring.security.bean

import com.angcyo.spring.mybatis.plus.auto.annotation.AutoWhere
import com.angcyo.spring.mybatis.plus.auto.param.IAutoParam

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/05/28
 */
class AccountQueryParam : IAutoParam {

    @AutoWhere
    var name: String? = null
}