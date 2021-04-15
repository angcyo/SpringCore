package com.angcyo.jsoup.http

import com.angcyo.http.DslRequest
import com.angcyo.http.base.readString
import com.angcyo.http.request
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

/**
 * 通过http请求url, 拿到返回数据. 再通过jsoup解析返回数据
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/04/14
 */
class HttpJsoup {

    /**配置网络请求*/
    var configRequest: DslRequest.() -> Unit = {}

    /**解析文档*/
    var parseDocument: (document: Document?, exception: Exception?) -> Unit = { _, _ -> }

    fun doIt() {
        request {
            async = false
            configRequest(this)
            onEndAction = { response, exception ->
                if (exception == null) {
                    val html = response?.body()?.readString()
                    val document = Jsoup.parse(html)
                    parseDocument(document, null)
                } else {
                    parseDocument(null, exception)
                }
            }
        }
    }
}

fun httpJsoup(dsl: HttpJsoup.() -> Unit) {
    HttpJsoup().apply {
        dsl()
        doIt()
    }
}