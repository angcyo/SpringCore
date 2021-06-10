package com.angcyo.spring.base.extension

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/11/23
 */

/**[com.angcyo.spring.base.advice.BaseRestExceptionHandlerAdvice]*/
open class ApiException(message: String = "接口异常", cause: Throwable? = null) : RuntimeException(message, cause)

inline fun apiError(message: Any, cause: Throwable? = null): Nothing = throw ApiException(message.toString(), cause)
