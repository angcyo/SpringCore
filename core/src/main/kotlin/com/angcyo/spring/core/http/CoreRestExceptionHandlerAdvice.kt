package com.angcyo.spring.core.http

import com.angcyo.spring.base.data.Result
import com.angcyo.spring.base.data.error
import com.angcyo.spring.util.str
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.util.NestedServletException


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

    @ExceptionHandler(NestedServletException::class)
    fun apiExtension(exception: NestedServletException): Result<String>? {
        return exception.message.error()
    }
}