package com.angcyo.spring.app.test

import com.angcyo.spring.base.bean
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import springfox.documentation.annotations.ApiIgnore

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/11/04
 */
@RestController
@RequestMapping("/test")
@ApiIgnore
class HelloController {

    @RequestMapping("/hello")
    fun hello(): String? {
        return HelloController::class.java.bean()?.hello2("default value")
    }

    @RequestMapping("/hello/{value}")
    fun hello2(@PathVariable(required = false) value: String?): String? {
        return "hello spring core. ${System.currentTimeMillis()}. $value"
    }
}