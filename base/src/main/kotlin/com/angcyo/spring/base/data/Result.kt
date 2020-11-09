package com.angcyo.spring.base.data

import com.angcyo.spring.base.data.Result.Companion.ERROR_CODE
import com.angcyo.spring.base.str
import org.springframework.validation.BindingResult

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

        fun <T> error(msg: String? = "Error", code: Int = ERROR_CODE, data: T? = null): Result<T?> {
            return data.error(msg, code)
        }
    }
}

fun <T> Any?.ok(msg: String? = "Success") = when {
    else -> Result(msg = msg, data = this as T)
}

/**error*/
fun <T> error(msg: String? = "Error", code: Int = ERROR_CODE) = Result.error<T>(msg, code)

/**error*/
fun <T> Any?.error(msg: String? = "Error", code: Int = ERROR_CODE) = when {
    else -> Result(code = code, msg = msg, data = this as T)
}

inline fun <T> BindingResult.result(responseEntity: () -> T): Result<T> {
    return if (hasErrors()) {
        //null.error(allErrors.toString())
        null.error(allErrors.joinToString { it.defaultMessage.str() })
    } else {
        responseEntity().ok()
    }
}