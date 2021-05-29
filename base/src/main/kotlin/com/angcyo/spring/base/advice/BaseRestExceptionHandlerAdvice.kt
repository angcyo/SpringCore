package com.angcyo.spring.base.advice

import com.angcyo.spring.base.data.Result
import com.angcyo.spring.base.data.error
import com.angcyo.spring.base.extension.ApiException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice


/**
 * [ApiException]异常处理
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021-05-28
 */

@RestControllerAdvice
class BaseRestExceptionHandlerAdvice {

    @ExceptionHandler(ApiException::class)
    fun apiExtension(exception: ApiException): Result<String>? {
        return exception.message.error()
    }
}