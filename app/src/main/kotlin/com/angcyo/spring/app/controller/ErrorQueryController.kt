package com.angcyo.spring.app.controller

import com.angcyo.spring.base.servlet.request
import com.angcyo.spring.util.str
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * 错误查询
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/10/21
 */

@Api(tags = ["错误日志的控制器"])
@RestController
@RequestMapping("/err")
class ErrorQueryController {

    @Autowired
    lateinit var jdbc: JdbcTemplate

    /**错误信息查询*/
    @ApiOperation("根据错误UUID, 查询错误信息")
    @GetMapping("/{errorUuid}")
    fun query(@PathVariable(required = true) errorUuid: String): String? {
        val list = if (errorUuid.lowercase() == "last") {
            jdbc.queryForList("SELECT * FROM logging_event ORDER BY timestmp DESC LIMIT 1")
        } else {
            jdbc.queryForList("SELECT * FROM logging_event WHERE arg0 = '${errorUuid}'")
        }

        val builder = StringBuilder()

        val accept = request()?.getHeader("Accept")
        val isHtml = accept?.contains("text/html") == true

        //请求时间
        var time: String? = null

        //请求耗时
        var duration: String? = null

        for (map in list) {
            val levelString = map["level_string"]
            if (levelString == "INFO") {
                duration = map["arg2"].str()
            } else if (levelString == "ERROR") {
                time = map["arg2"].str()
            }
        }

        if (list.size > 0) {

            //log 消息
            val formattedMessage = list[0]["formatted_message"]

            if (isHtml) {
                time?.apply {
                    builder.append("<p>${this}</p>")
                }
                builder.append("<p>调用接口:${list[0]["arg1"]} $duration</p>")
                builder.append("<p>来自:${list[0]["arg3"]}</p>")
                builder.append("<br>")
                builder.append("<p>${formattedMessage.toString().replace("\n", "<br>")}</p>")
            } else {
                time?.apply { builder.appendLine(this) }
                builder.appendLine("调用接口:${list[0]["arg1"]} $duration")
                builder.appendLine("来自:${list[0]["arg3"]}")
                builder.appendLine()
                builder.append(formattedMessage)
            }
        }

        return builder.toString()
    }
}