package com.angcyo.spring.util

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.event.Level

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/11/05
 *
 * 日志输出类
 */

object L {

    val _logAngcyo: Logger = LoggerFactory.getLogger("angcyo")
    val _logHttp: Logger = LoggerFactory.getLogger("http")
    val _logTemp: Logger = LoggerFactory.getLogger("temp")
    val _logDb: Logger = LoggerFactory.getLogger("db")

    /**调试环境*/
    var isDebug: Boolean = false

    fun debug(action: () -> Unit) {
        if (isDebug) {
            action()
        }
    }

    /*----------------angcyo.log----------------------*/

    fun i(vararg log: Any?) {
        _logAngcyo.info(log.joinToString(" "))
    }

    fun d(vararg log: Any?) {
        _logAngcyo.debug(log.joinToString(" "))
    }

    fun w(vararg log: Any?) {
        _logAngcyo.warn(log.joinToString(" "))
    }

    fun e(vararg log: Any?) {
        _logAngcyo.error(log.joinToString(" "))
    }

    /*----------------http.log----------------------*/

    fun ih(vararg log: Any?) {
        _logHttp.info(log.joinToString(" "))
    }

    fun wh(vararg log: Any?) {
        _logHttp.warn(log.joinToString(" "))
    }

    fun eh(vararg log: Any?) {
        _logHttp.error(log.joinToString(" "))
    }

    /*----------------temp.log----------------------*/

    fun it(vararg log: Any?) {
        _logTemp.info(log.joinToString(" "))
    }

    fun wt(vararg log: Any?) {
        _logTemp.warn(log.joinToString(" "))
    }

    fun et(vararg log: Any?) {
        _logTemp.error(log.joinToString(" "))
    }

    /*----------------db.log----------------------*/

    fun db(level: Level = Level.INFO, vararg log: String?) {
        val args = log.toList()
        val last = if (args.size() > 1) {
            args.subList(1, args.size())
        } else {
            emptyList()
        }
        val first = args.getOrNull(0)
        when (level) {
            Level.ERROR -> _logDb.error(first, *last.toTypedArray())
            Level.WARN -> _logDb.warn(first, *last.toTypedArray())
            Level.INFO -> _logDb.info(first, *last.toTypedArray())
            Level.DEBUG -> _logDb.debug(first, *last.toTypedArray())
            Level.TRACE -> _logDb.trace(first, *last.toTypedArray())
        }
    }

    fun dbInfo(vararg log: String) {
        db(Level.INFO, *log)
    }

    fun dbWarn(vararg log: String) {
        db(Level.WARN, *log)
    }

    fun dbError(vararg log: String) {
        db(Level.ERROR, *log)
    }
}

/**
 * 获取调用栈信息
 * [front] 当前调用位置的前几个开始
 * [count] 共几个, 负数表示全部
 * */
fun getStackTrace(front: Int = 0, count: Int = -1): List<StackTraceElement> {
    val stackTrace = Thread.currentThread().stackTrace
    stackTrace.reverse()
    val endIndex = stackTrace.size - 3 - front
    val startIndex = if (count > 0) (endIndex - count) else 0
    val slice = stackTrace.slice(startIndex until endIndex)
    return slice
}

/**最后一条调用栈的信息
 * com.angcyo.spring.core.http.CoreRestExceptionHandlerAdvice.runtimeException(CoreRestExceptionHandlerAdvice.kt:44)
 *
 * 如果是用变量接收返回结果,则[front]使用0
 * */
fun atLastStackTrace(front: Int = 0): String {
    val last = getStackTrace(front, 1).lastOrNull()
    return if (last == null) {
        ""
    } else {
        "at $last\n"
    }
}
