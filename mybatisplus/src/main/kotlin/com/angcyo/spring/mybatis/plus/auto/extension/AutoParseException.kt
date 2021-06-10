package com.angcyo.spring.mybatis.plus.auto.extension

import com.angcyo.spring.base.extension.ApiException

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/06/10
 */
class AutoParseException(msg: String, cause: Throwable? = null) : ApiException(msg, cause)