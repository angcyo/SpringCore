package com.angcyo.spring.http.controller

import com.angcyo.http.base.readString
import com.angcyo.http.dslRequest
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/11/04
 */

@RestController
@RequestMapping("/http")
class HttpController {

    @RequestMapping("/get")
    fun get(request: HttpServletRequest): String? {
        val url = request.getHeader("url")
        if (url.isNullOrEmpty()) {
            return "url地址不能为空!"
        } else if (url.startsWith("http://") || url.startsWith("https://")) {
            var result: String? = null
            dslRequest {
                this.url = url
                async = false
                onEndAction = { response, exception ->
                    response?.body?.readString()?.let {
                        result = it
                    }
                    exception?.let {
                        result = it.message
                    }
                }
            }
            return result
        } else {
            return "无效的url地址:$url"
        }
    }
}