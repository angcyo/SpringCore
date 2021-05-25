package com.angcyo.spring.security.test

import com.angcyo.spring.util.nowTimeString
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import springfox.documentation.annotations.ApiIgnore

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/11/06
 */

@RestController
@RequestMapping("/test/security")
@ApiIgnore
class SecurityTestController {

    @RequestMapping("/")
    fun hello(): String? {
        return "hello security ${nowTimeString()}"
    }

    @RequestMapping("/test")
    fun test(): String? {
        return "hello security ${nowTimeString()}"
    }
}