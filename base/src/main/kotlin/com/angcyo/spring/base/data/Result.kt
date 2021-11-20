package com.angcyo.spring.base.data

import com.angcyo.spring.base.data.Result.Companion.ERROR_CODE
import com.angcyo.spring.base.data.Result.Companion.SUCCESS_CODE
import com.angcyo.spring.base.extension.apiError
import com.angcyo.spring.base.json.fromJackson
import com.angcyo.spring.base.json.toJackson
import com.angcyo.spring.util.L
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

/**自动判断是否返回成功/失败*/
fun <T> Any?.result(successMsg: String = "Success", errorMsg: String = "Success"): Result<T> {
    if (this == null || this == false) {
        return Result.error(this as T).apply {
            msg = errorMsg
        }
    }
    return Result.ok(this as T).apply {
        msg = successMsg
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

//<editor-fold desc="BindingResult">

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
 * [javax.validation.constraints.Size]
 * [javax.validation.constraints.Min]
 * [javax.validation.constraints.Max]
 * [javax.validation.constraints.NotNull]
 * [javax.validation.constraints.NotEmpty]
 * @return 返回空集合, 表示全部正确. 否则表示错误信息.
 * */
fun Any.validate(vararg propertyName: String): Set<ConstraintViolation<Any>> {
    val validator = Validation.buildDefaultValidatorFactory().validator
    if (propertyName.isEmpty()) {
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

inline fun Any.validateResult(vararg propertyName: String) {
    val validate = validate(*propertyName)
    if (validate.isNotEmpty()) {
        apiError(validate.joinToString { it.message.str() })
    }
}

//</editor-fold desc="BindingResult">

/**是否需要返回对象*/
inline fun <T> T.isReturn(judge: T.() -> Boolean): T? {
    if (judge()) {
        return this
    }
    return null
}

inline fun <T> T?.ifError(
    /**异常的提示*/
    error: String = "操作失败",
    /**什么情况下是异常*/
    judge: T?.() -> Boolean = {
        this == null || this == false
    }
): T? {
    if (judge()) {
        L.e(error)
        apiError(error)
    }
    return this
}

inline fun Int?.ifExist(
    error: String = "数据已存在"
): Int? {
    return ifError(error) {
        (this ?: 0) > 0
    }
}

inline fun Int?.ifNotExist(
    error: String = "数据不存在"
): Int? {
    return ifError(error) {
        (this ?: 0) <= 0
    }
}

inline fun Long?.ifExist(
    error: String = "数据已存在"
): Long? {
    return ifError(error) {
        (this ?: 0) > 0
    }
}

inline fun Long?.ifNotExist(
    error: String = "数据不存在"
): Long? {
    return ifError(error) {
        (this ?: 0) <= 0
    }
}

//<editor-fold desc="数据类型转换">

/**将数据结构[T]通过json转换成[R]*/
fun <T, R> T.toBean(cls: Class<R>): R? {
    val json = this.toJackson()
    return json.fromJackson(cls)
}

fun <T, R> List<T>.toBeanList(cls: Class<R>): List<R> {
    val result = mutableListOf<R>()
    forEach {
        it.toBean(cls)?.let { bean ->
            result.add(bean)
        }
    }
    return result
}

//</editor-fold desc="数据类型转换">
