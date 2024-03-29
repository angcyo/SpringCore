package com.angcyo.spring.core.task

import com.angcyo.spring.base.AppProperties
import com.angcyo.spring.util.L
import com.angcyo.spring.util.wrapDuration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.scheduling.annotation.Async
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

/**
 * 定时清理日志的任务, 需要[@EnableScheduling] //激活定时任务
 *
 * http://www.macrozheng.com/#/architect/mall_arch_06?id=mall%e6%95%b4%e5%90%88springtask%e5%ae%9e%e7%8e%b0%e5%ae%9a%e6%97%b6%e4%bb%bb%e5%8a%a1
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/10/16
 */

@Component
class ClearLogTask {

    @Autowired
    lateinit var jdbc: JdbcTemplate

    @Autowired
    lateinit var app: AppProperties

    /**定时任务执行*/
    @Async
    //@Scheduled(cron = "*/5 * * * * ?") //每5秒执行一次
    @Scheduled(cron = "0 0 4 */7 * ? ") //每隔7天的4点执行一次
    fun clearLogPoint() {
        L.i("开始清理日志...")
        L.i("清理结束,耗时:${
            wrapDuration {
                _clearLog()
            }
        }")
    }

    fun _clearLog() {
        //多少天前的日志
        try {
            val before = app.clearLogBefore
            val count =
                jdbc.update("DELETE FROM logging_event where timestmp <= UNIX_TIMESTAMP(NOW()) * 1000 - $before * 86400 * 1000")
            L.i("清除日志($count)条")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}