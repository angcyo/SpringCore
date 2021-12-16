package com.angcyo.spring.app.test

import com.angcyo.spring.base.bean
import com.angcyo.spring.util.uuid
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import springfox.documentation.annotations.ApiIgnore

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/11/04
 */

@ApiIgnore
@RestController
@RequestMapping("/test")
class AppTestController {

    @RequestMapping("/")
    fun test(): String? {
        val key = "/file/downloadFile/${uuid()}.png"
        val uri = ServletUriComponentsBuilder.fromCurrentContextPath().path(key).toUriString()
        val className = this::class.java.name
        val uri2 = ServletUriComponentsBuilder.fromCurrentContextPath().toUriString()
        return "$className $uri2"
    }

    @RequestMapping("/hello")
    fun hello(): String? {
        return AppTestController::class.java.bean().hello2("default value")
    }

    @RequestMapping("/hello/{value}")
    fun hello2(@PathVariable(required = false) value: String?): String? {
        return "hello spring core. ${System.currentTimeMillis()}. $value"
    }
}