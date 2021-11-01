package com.angcyo.spring.base.extension

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/11/23
 */

/**[com.angcyo.spring.base.advice.BaseRestExceptionHandlerAdvice]*/
//@ResponseStatus(value = HttpStatus.NOT_IMPLEMENTED, code = HttpStatus.NOT_IMPLEMENTED, reason = "接口异常")
open class ApiException(message: String = "接口异常", cause: Throwable? = null) : RuntimeException(message, cause)

inline fun apiError(message: Any, cause: Throwable? = null): Nothing = throw ApiException(message.toString(), cause)
