package com.angcyo.spring.log.core.task

import com.angcyo.spring.util.L
import org.springframework.beans.factory.annotation.Configurable
import org.springframework.scheduling.annotation.Async
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled

/**
 * 定时清理日志的任务
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/10/16
 */

@Configurable
@EnableScheduling
//@EnableAsync
class ClearLogTask {

    /**定时任务执行*/
    @Scheduled(cron = "*/5 * * * * ?")
    //@Async
    fun clearLog() {
        L.i("开始清理日志...")
    }
}