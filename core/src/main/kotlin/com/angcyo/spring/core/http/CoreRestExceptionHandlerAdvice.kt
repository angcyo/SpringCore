package com.angcyo.spring.core.http

import com.angcyo.spring.base.data.Result
import com.angcyo.spring.base.data.error
import com.angcyo.spring.base.extension.ApiException
import com.angcyo.spring.base.servlet.address
import com.angcyo.spring.base.servlet.request
import com.angcyo.spring.log.core.ServletLog
import com.angcyo.spring.util.L
import com.angcyo.spring.util.nowTimeString
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

    @ExceptionHandler(RuntimeException::class)
    fun runtimeException(exception: RuntimeException): Result<String>? {
        val request = request()
        L._logDb.error(
            exception.stackTraceToString(),
            ServletLog.logRequestUuid.get(),
            request?.servletPath ?: "",
            nowTimeString(),
            request?.address() ?: "",
        )
        if (exception is ApiException) {
            return exception.message.error()
        }
        throw exception
    }
}