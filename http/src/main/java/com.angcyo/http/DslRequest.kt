package com.angcyo.http

import com.angcyo.http.base.toHttpUrl
import com.angcyo.http.base.toMediaTypeOrNull
import com.angcyo.http.base.toRequestBody
import com.angcyo.http.exception.HttpDataException
import com.angcyo.spring.util.L
import com.angcyo.spring.util.json.fromJson
import okhttp3.*
import okhttp3.internal.http.HttpMethod
import org.springframework.http.HttpHeaders
import java.io.IOException
import java.lang.reflect.Type
import java.nio.charset.Charset

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/01/17
 */
class DslRequest {

    companion object {
        fun body(mediaType: String, byteArray: ByteArray): RequestBody {
            return byteArray.toRequestBody(mediaType.toMediaTypeOrNull())
        }

        fun jsonBody(json: String): RequestBody {
            return json.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        }

        fun textBody(text: String): RequestBody {
            return text.toRequestBody("text/plain".toMediaTypeOrNull())
        }

        fun formBody(form: Map<String, String?>?, charset: Charset? = Charsets.UTF_8): FormBody {
            return FormBody.Builder(charset).apply {
                form?.forEach { entry ->
                    entry.value?.let {
                        add(entry.key, it)
                    }
                }
            }.build()
        }

        fun multipartBody(config: MultipartBody.Builder.() -> Unit): MultipartBody {
            return MultipartBody.Builder().apply {
                //setType()
                //addPart()
                //addFormDataPart()
                config()
            }.build()
        }
    }

    /**请求地址*/
    var url: String? = null

    /**请求方法*/
    var method: String = "GET"

    /**请求体*/
    var body: RequestBody? = null

    /**请求头*/
    var header: HashMap<String, String>? = null

    /**返回回调, 有可能在子线程回调*/
    var onEndAction: ((response: Response?, exception: Exception?) -> Unit)? = null

    /**异步请求*/
    var async: Boolean = false

    var _call: Call? = null

    /**执行*/
    fun doIt(): Call? {
        return try {
            request()
        } catch (e: Exception) {
            e.printStackTrace()
            _callback(_call, null, e)
            null
        }
    }

    fun request(): Call {
        val request: Request = Request.Builder()
            .apply {
                val requestUrl = url
                if (!requestUrl.isNullOrEmpty()) {
                    url(requestUrl)
                }

                var requestMethod = method.toUpperCase()

                if (body != null) {
                    if (requestMethod == "GET") {
                        requestMethod = "POST"
                    }
                }

                val requestBody = if (HttpMethod.requiresRequestBody(requestMethod)) {
                    body ?: jsonBody("")
                } else {
                    body
                }
                method(requestMethod, body ?: requestBody)

                //base header
                header(HttpHeaders.ACCEPT, "*/*")
                //header(HttpHeaders.ACCEPT_ENCODING, "gzip, deflate, br") //声明gzip压缩,需要手动解压.
                header(HttpHeaders.CACHE_CONTROL, "no-cache")
                header(HttpHeaders.CONNECTION, "keep-alive")

                requestUrl?.let {
                    val httpUrl = it.toHttpUrl()
                    header("Host", httpUrl.host())
                    //header("Origin", "${httpUrl.scheme}://${httpUrl.host}")
                }

                header?.forEach { entry ->
                    if (entry.key.toLowerCase() != "Accept-Encoding".toLowerCase()) {
                        //不支持压缩
                        header(entry.key, entry.value)
                    }
                }
            }
            .build()

        val client = DslHttp.dslHttpConfig.onBuildHttpClient(
            DslHttp.dslHttpConfig.defaultOkHttpClientBuilder.apply {
                DslHttp.dslHttpConfig.onConfigOkHttpClient.forEach {
                    it(this)
                }
            }
        )

        val call = client.newCall(request)

        L.i("请求:$url")
        if (async) {
            call.enqueue(object : Callback {

                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                    _callback(call, null, e)
                }

                override fun onResponse(call: Call, response: Response) {
                    _callback(call, response, null)
                }
            })
        } else {
            try {
                val response = call.execute()
                _callback(call, response, null)
            } catch (e: Exception) {
                e.printStackTrace()
                _callback(call, null, e)
            }
        }
        _call = call
        return call
    }

    //回调
    fun _callback(call: Call?, response: Response?, e: Exception?) {
        if (call == null || !call.isCanceled) {
            try {
                if (response?.isSuccessful == true) {
                    onEndAction?.invoke(response, null)
                } else {
                    if (response == null) {
                        onEndAction?.invoke(null, e)
                    } else {
                        onEndAction?.invoke(
                            null,
                            HttpDataException(response.message(), response.code())
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                onEndAction?.invoke(null, e)
            }
        }
    }
}

fun jsonBody(json: String): RequestBody {
    return json.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
}

fun textBody(text: String): RequestBody {
    return text.toRequestBody("text/plain".toMediaTypeOrNull())
}

fun formBody(form: Map<String, String?>?, charset: Charset = Charsets.UTF_8): FormBody {
    return FormBody.Builder(charset).apply {
        form?.forEach { entry ->
            entry.value?.let {
                add(entry.key, it)
            }
        }
    }.build()
}

fun multipartBody(config: MultipartBody.Builder.() -> Unit): MultipartBody {
    return MultipartBody.Builder().apply {
        //setType()
        //addPart()
        //addFormDataPart()
        config()
    }.build()
}

/**toBean*/
fun <T> Response.toBean(typeOfT: Type): T? = fromJson(typeOfT)

fun <T> Response.fromJson(typeOfT: Type): T? {
    if (isSuccessful) {
        val bodyString = body()?.string()
        return bodyString?.fromJson<T>(typeOfT)
    }
    return null
}

fun Response.bodyString(): String? = body()?.string()

/**[DSL]*/
fun request(action: DslRequest.() -> Unit): Call? {
    return DslRequest().run {
        action()
        doIt()
    }
}

fun dslRequest(action: DslRequest.() -> Unit): Call? = request(action)