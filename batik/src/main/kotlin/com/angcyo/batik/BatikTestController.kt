package com.angcyo.batik

import com.angcyo.spring.util.nowTimeString
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletResponse

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/10/27
 */

@RequestMapping("/test/svg")
@RestController
class BatikTestController {

    @RequestMapping("/")
    fun test(): String {
        return nowTimeString()
    }

    @RequestMapping("/test")
    fun test(response: HttpServletResponse) {
        response.contentType = MediaType.valueOf("image/svg+xml").toString()
        Batik.test(response.outputStream)
    }
}