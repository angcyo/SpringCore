package com.angcyo.spring.app

import com.angcyo.spring.base.AppProperties
import com.angcyo.spring.base.beanOf
import com.angcyo.spring.util.*
import org.springframework.boot.info.BuildProperties
import org.springframework.context.ConfigurableApplicationContext
import java.io.File

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/05/28
 */
object LibApp {
    /**启动目标程序*/
    inline fun <reified T : Any> run(
        vararg args: String,
        init: ConfigurableApplicationContext.() -> Unit = {}
    ): ConfigurableApplicationContext {
        //启动计时
        val startTime = nowTime()
        //启动
        return org.springframework.boot.runApplication<T>(*args).apply {
            init()

            //启动耗时输出
            val nowTime = nowTime()
            val app: AppProperties = getBean(AppProperties::class.java)
            val duration = (nowTime - startTime).toElapsedTime(intArrayOf(1, 1, 1))
            val file = File("")
            val path = file.absolutePath

            L.w(buildString {
                appendLine("编译时间:${beanOf<BuildProperties>().get("buildTime")}")
                append("${app.name ?: "SpringBoot"} 启动结束,耗时:${duration}☞ $path http://${getLocalHost()}:${getServerPort()} http://localhost:${getServerPort()}")
            })
        }
    }
}