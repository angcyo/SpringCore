package com.angcyo.spring.core.http

import com.angcyo.spring.base.data.Result
import com.angcyo.spring.base.data.error
import com.angcyo.spring.base.str
import com.angcyo.spring.core.http.extension.ApiException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice


/**
 * 全局异常处理
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/11/23
 */

@RestControllerAdvice
class CoreRestExceptionHandlerAdvice {

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun methodArgumentNotValidException(exception: MethodArgumentNotValidException): Result<String>? {
        // 从异常对象中拿到ObjectError对象
        return exception.bindingResult.allErrors.joinToString { it.defaultMessage.str() }.error()
    }

    @ExceptionHandler(ApiException::class)
    fun apiExtension(exception: ApiException): Result<String>? {
        return exception.message.error()
    }
}