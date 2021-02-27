package com.angcyo.spring.base.data

import com.angcyo.spring.base.data.Result.Companion.ERROR_CODE
import com.angcyo.spring.base.str
import org.springframework.validation.BindingResult
import org.springframework.validation.FieldError
import javax.validation.ConstraintViolation
import javax.validation.Validation
import javax.validation.groups.Default

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/11/04
 *
 *
 * 返回的数据结构体.
 *
 * ```
 * //404默认结构体
 * {
 *    "timestamp": "2020-11-04T09:24:05.425+00:00",
 *    "status": 404,
 *    "error": "Not Found",
 *    "message": "No message available",
 *    "path": "/test/one12"
 * }
 * ```
 */

data class Result<T>(
        var code: Int = 200,
        var msg: String? = "Success",
        var data: T? = null
) {
    companion object {

        /**错误码*/
        const val ERROR_CODE = 501
    }
}

fun <T> Any?.ok(msg: String? = "Success") = when {
    else -> Result(msg = msg, data = this as T)
}

/**error*/
fun <T> resultError(msg: String? = "Error", code: Int = ERROR_CODE) = msg.error<T>(code)

/**将[this]当做错误信息返回*/
fun <T> Any?.error(code: Int = ERROR_CODE) = Result<T>(code = code, msg = this.str(), null)

inline fun <T> BindingResult.result(responseEntity: () -> T?): Result<T> {
    return if (hasErrors()) {
        allErrors.joinToString {
            if (it is FieldError) {
                "${it.field}:${it.defaultMessage}"
            } else {
                "${it.defaultMessage}"
            }
        }.error()
    } else {
        try {
            responseEntity().ok()
        } catch (e: NoSuchElementException) {
            e.toString().error<T>()
        } catch (e: Exception) {
            e.message.error<T>()
        }
    }
}

/**[validator]验证数据是否正确*/
fun Any.validate(vararg propertyName: String): Set<ConstraintViolation<Any>> {
    val validator = Validation.buildDefaultValidatorFactory().validator
    if (propertyName.isNullOrEmpty()) {
        //如果指定验证的属性为空,则验证所有字段
        return validator.validate(this, Default::class.java)
    }
    for (prop in propertyName) {
        val result = validator.validateProperty(this, prop, Default::class.java)
        if (!result.isNullOrEmpty()) {
            //指定属性验证失败了, 立即返回. 否则验证下一个指定的属性
            return result
        }
    }
    return emptySet()
}

/**拿不到数据不正确提示的消息错误返回结构体*/
fun <T> Set<ConstraintViolation<T>?>.result(): Result<T> {
    return joinToString { it?.message.str() }.error()
}
