package com.angcyo.spring.core.log

import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/11/05
 *
 * 日志输出类
 */

object L {

    val _log: Logger = LoggerFactory.getLogger("!angcyo!")

    fun i(vararg log: Any?) {
        _log.info(log.joinToString(" "))
    }

    fun w(vararg log: Any?) {
        _log.warn(log.joinToString(" "))
    }

    fun e(vararg log: Any?) {
        _log.error(log.joinToString(" "))
    }
}