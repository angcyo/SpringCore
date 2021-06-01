package com.angcyo.spring.base.data

import com.angcyo.spring.base.data.Result.Companion.ERROR_CODE
import com.angcyo.spring.base.data.Result.Companion.SUCCESS_CODE
import com.angcyo.spring.util.str
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
    var code: Int = SUCCESS_CODE,
    var msg: String? = "Success",
    var data: T? = null
) {
    companion object {

        /**错误码*/
        const val ERROR_CODE = 501
        const val SUCCESS_CODE = 200

        fun <T> ok(data: T? = null): Result<T> {
            return Result<T>().apply {
                code = SUCCESS_CODE
                msg = "Success"
                this.data = data
            }
        }

        fun <T> error(data: T? = null): Result<T> {
            return Result<T>().apply {
                code = ERROR_CODE
                msg = "Error"
                this.data = data
            }
        }
    }
}

/**无论如何都返回成功*/
fun <T> Any?.ok(msg: String? = null, checkNull: Boolean = false) = if (checkNull && this == null) {
    resultError(msg ?: "Data is Null Error.")
} else {
    Result(msg = msg ?: "Success", data = this as T)
}

/**不为空时, 才返回成功; 否则返回失败*/
fun <T> Any?.okIfNull(msg: String? = if (this == null) "Error" else "Success") = if (this == null) {
    resultError(msg ?: "Data is Null Error.")
} else {
    Result(msg = msg, data = this as T)
}

/**error*/
fun <T> resultError(msg: String? = "Error", code: Int = ERROR_CODE) = msg.error<T>(code)

/**Success*/
fun <T> resultOk(msg: String? = "Success", code: Int = SUCCESS_CODE) = msg.error<T>(code)

/**将[this]当做错误信息返回*/
fun <T> Any?.error(code: Int = ERROR_CODE) = Result<T>(code = code, msg = this.str(), null)

inline fun <T> BindingResult.result(checkNull: Boolean = true, responseEntity: () -> T?): Result<T> {
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
            val resultEntity = responseEntity()
            if (checkNull &&
                (resultEntity is Boolean && resultEntity == false ||
                        resultEntity == null)
            ) {
                Result.error(resultEntity)
            } else {
                Result.ok(resultEntity)
            }
        } catch (e: NoSuchElementException) {
            e.toString().error()
        } catch (e: Exception) {
            e.message.error()
        }
    }
}

/**[validator]验证数据是否正确
 * [propertyName] 单独指定需要验证的字段, 不指定表示bean的所以字段
 * @return 返回空集合, 表示全部正确. 否则表示错误信息.
 * */
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
