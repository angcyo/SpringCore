package com.angcyo.spring.core.http.extension

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/11/23
 */
class ApiException(message: String = "接口异常") : RuntimeException(message)

inline fun apiError(message: Any): Nothing = throw ApiException(message.toString())
