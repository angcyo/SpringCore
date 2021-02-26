package com.angcyo.http

import com.angcyo.http.DslHttp.DEFAULT_CODE_KEY
import com.angcyo.http.DslHttp.DEFAULT_MSG_KEY
import com.angcyo.http.DslHttp.retrofit
import com.angcyo.http.base.readString
import com.angcyo.spring.base.connectUrl
import com.angcyo.spring.base.json.fromJson
import com.angcyo.spring.base.json.getInt
import com.angcyo.spring.base.json.toJson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import java.lang.reflect.Type

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
        dslHttpConfig.action()
    }

    fun init() {
        val baseUrl = dslHttpConfig.onGetBaseUrl()

        if (baseUrl.isEmpty()) {
            throw NullPointerException("请先初始化[DslHttp.config{ ... }]")
        }

        val client = dslHttpConfig.onBuildHttpClient(
            dslHttpConfig.defaultOkHttpClientBuilder.apply {
                dslHttpConfig.onConfigOkHttpClient.forEach {
                    it(this)
                }
            }
        )
        dslHttpConfig.okHttpClient = client

        val retrofit = dslHttpConfig.onBuildRetrofit(dslHttpConfig.defaultRetrofitBuilder, client)
        dslHttpConfig.retrofit = retrofit
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

    /**获取[Retrofit]对象*/
    fun retrofit(rebuild: Boolean = false): Retrofit {
        if (rebuild) {
            dslHttpConfig.retrofit = null
        }
        init()
        return dslHttpConfig.retrofit!!
    }
}

/**
 * 通用接口请求
 * */
fun <T> dslHttp(service: Class<T>): T {
    val retrofit = retrofit(false)
    /*如果单例API对象的话, 就需要在动态切换BaseUrl的时候, 重新创建. 否则不会生效*/
    return retrofit.create(service)
}

/**拼接 host 和 api接口*/
fun connectUrl(host: String?, url: String?): String {
//    val h = host?.trimEnd('/') ?: ""
//    val u = url?.trimStart('/') ?: ""
//    return "$h/$u"
    return host.connectUrl(url)
}

/**判断http状态码为成功, 并且接口返回状态也为成功*/
fun Response<JsonElement>?.isSucceed(
    codeKey: String? = DEFAULT_CODE_KEY,
    onResult: (succeed: Boolean, codeErrorJson: JsonObject?) -> Unit = { _, _ -> } /*code码异常时codeErrorJson才有值*/
): Boolean {
    val bodyData = this?.body()

    var result = false
    if (this == null || bodyData == null) {
        //空数据
        result = this?.isSuccessful == true
        onResult(result, null)
        return result
    }

    var errorJson: JsonObject? = null

    if (codeKey.isNullOrEmpty()) {
        result = isSuccessful
    } else if (isSuccessful && bodyData is JsonObject) {
        if (bodyData.getInt(codeKey) in 200..299) {
            result = true
        } else {
            errorJson = bodyData
        }
    }
    onResult(result, errorJson)
    return result
}

//</editor-fold desc="基础">

//<editor-fold desc="JsonElement to Bean">

/**[JsonElement]转换成数据bean*/
fun <T> Response<JsonElement>.toBean(type: Type, parseError: Boolean = false): T? {
    return when {
        isSuccessful -> {
            when (val bodyJson = body().toJson()) {
                null -> null
                else -> bodyJson.fromJson<T>(type, parseError)
            }
        }
        parseError -> {
            when (val bodyJson = errorBody()?.readString()) {
                null -> null
                else -> bodyJson.fromJson<T>(type, parseError)
            }
        }
        else -> null
    }
}

fun <T> Response<JsonElement>.toBean(bean: Class<T>, parseError: Boolean = false): T? {
    return when {
        isSuccessful -> {
            when (val bodyJson = body().toJson()) {
                null -> null
                else -> bodyJson.fromJson(bean, parseError)
            }
        }
        parseError -> {
            when (val bodyJson = errorBody()?.readString()) {
                null -> null
                else -> bodyJson.fromJson(bean, parseError)
            }
        }
        else -> null
    }
}

//</editor-fold desc="JsonElement to Bean">


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
    var body: JsonElement = JsonObject()

    //url后面拼接的参数列表
    var query: HashMap<String, Any> = hashMapOf()

    //表单格式请求数据 method使用[POST_FORM]
    var formMap: HashMap<String, Any> = hashMapOf()

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
}

open class RequestConfig : BaseRequestConfig() {
    //判断返回的数据
    var isSuccessful: (Response<JsonElement>) -> Boolean = {
        it.isSucceed(codeKey)
    }

    //http状态请求成功才回调
    var onSuccess: (Response<JsonElement>) -> Unit = {}
}

open class RequestBodyConfig : BaseRequestConfig() {
    //判断返回的数据
    var isSuccessful: (Response<ResponseBody>) -> Boolean = {
        it.isSuccessful
    }

    //http状态请求成功才回调
    var onSuccess: (Response<ResponseBody>) -> Unit = {}
}

//</editor-fold desc="网络请求配置项">

