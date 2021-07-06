package com.angcyo.spring.http.controller

import com.angcyo.http.base.readString
import com.angcyo.http.dslRequest
import com.angcyo.spring.base.servlet.param
import com.angcyo.spring.base.util.L
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.net.URI
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
        val hUrl = "url"
        val url = request.getHeader(hUrl)
        val noHeader = request.param("noHeader")

        if (url.isNullOrEmpty()) {
            return "url地址不能为空!"
        } else if (url.startsWith("http://") || url.startsWith("https://")) {
            var result: String? = null
            dslRequest {
                this.url = url

                if (noHeader != "true") {
                    val header = hashMapOf<String, String>()
                    this.header = header

                    for (hKey in request.headerNames) {
                        if (hKey != hUrl) {
                            if (hKey == "host") {
                                header["host"] = URI(url).host
                            } else {
                                header[hKey] = request.getHeader(hKey)
                            }
                        }
                    }

                    L.i("header↓")
                    L.i(header)
                }

                async = false
                onEndAction = { response, exception ->
                    response?.body()?.readString()?.let {
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