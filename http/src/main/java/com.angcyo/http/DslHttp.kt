package com.angcyo.http

import com.angcyo.http.DslHttp.DEFAULT_CODE_KEY
import com.angcyo.http.DslHttp.DEFAULT_MSG_KEY
import com.angcyo.spring.util.connectUrl
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import okhttp3.OkHttpClient
import java.security.cert.X509Certificate
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

/**
 * 网络请求库
 * https://www.jianshu.com/p/865e9ae667a0
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/12/25
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */

//<editor-fold desc="基础">

object DslHttp {
    var DEFAULT_CODE_KEY = "code"
    var DEFAULT_MSG_KEY = "msg"

    val dslHttpConfig = DslHttpConfig()

    /**自定义配置, 否则使用库中默认配置*/
    fun config(action: DslHttpConfig.() -> Unit) {
        dslHttpConfig.reset()
        dslHttpConfig.action()
    }

    fun init() {
        val baseUrl = dslHttpConfig.onGetBaseUrl()

        if (baseUrl.isEmpty()) {
            throw NullPointerException("请先初始化[DslHttp.config{ ... }]")
        }

        //缓存客户端
        val client = dslHttpConfig.okHttpClient ?: dslHttpConfig.onBuildHttpClient(
            dslHttpConfig.defaultOkHttpClientBuilder.apply {
                dslHttpConfig.onConfigOkHttpClient.forEach {
                    it(this)
                }
            }
        )
        dslHttpConfig.okHttpClient = client
    }

    /**获取[OkHttpClient]对象*/
    fun httpClient(rebuild: Boolean = false): OkHttpClient {
        if (rebuild) {
            dslHttpConfig.okHttpClient = null
        }
        init()
        return dslHttpConfig.okHttpClient!!
    }

    /**根据配置, 创建一个[OkHttpClient]客户端*/
    fun createClient(): OkHttpClient {
        val client = dslHttpConfig.defaultOkHttpClientBuilder.apply {
            dslHttpConfig.onConfigOkHttpClient.forEach {
                it(this)
            }
        }.build()
        return client
    }


    /**去掉ssl验证*/
    fun noSSL() {
        System.setProperty("sun.net.http.allowRestrictedHeaders", "true")
        val trm: TrustManager = object : X509TrustManager {
            override fun checkClientTrusted(certs: Array<X509Certificate?>?, authType: String?) {}
            override fun checkServerTrusted(certs: Array<X509Certificate?>?, authType: String?) {}
            override fun getAcceptedIssuers(): Array<X509Certificate>? = null
        }
        val sc: SSLContext = SSLContext.getInstance("SSL")
        sc.init(null, arrayOf(trm), null)
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.socketFactory)
    }
}

/**拼接 host 和 api接口*/
fun connectUrl(host: String?, url: String?): String {
//    val h = host?.trimEnd('/') ?: ""
//    val u = url?.trimStart('/') ?: ""
//    return "$h/$u"
    return host.connectUrl(url)
}

//</editor-fold desc="基础">

//<editor-fold desc="网络请求配置项">

const val GET = 1
const val POST = 2
const val PUT = 3

//如果[formMap]有数据, 则会优先使用[POST_FORM]
const val POST_FORM = 22

open class BaseRequestConfig {
    //请求方法
    var method: Int = GET

    //接口api, 可以是全路径, 也可以是相对于baseUrl的路径
    var url: String = ""

    //自动根据url不是http开头,拼接上baseUrl
    var autoConnectUrl: Boolean = true

    //body数据, 仅用于post请求. @Body
    var body: JsonElement? = JsonObject()

    //url后面拼接的参数列表
    var query: HashMap<String, Any?> = hashMapOf()

    //表单格式请求数据 method使用[POST_FORM]
    var formMap: HashMap<String, Any?> = hashMapOf()

    //请求头
    var header: HashMap<String, String> = hashMapOf()

    //解析请求返回的json数据, 判断code是否是成功的状态, 否则走异常流程.
    var codeKey: String = DEFAULT_CODE_KEY
    var msgKey: String = DEFAULT_MSG_KEY

    var onStart: () -> Unit = {}

    //请求结束, 网络状态成功, 但是数据状态不一定成功
    var onComplete: () -> Unit = {}

    //异常处理
    var onError: (Throwable) -> Unit = {}

    //在主线程观察
    var observableOnMain: Boolean = true
}

//</editor-fold desc="网络请求配置项">

