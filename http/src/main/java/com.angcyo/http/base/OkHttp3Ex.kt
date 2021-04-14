package com.angcyo.http.base

import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.internal.checkOffsetAndCount
import okio.BufferedSink
import java.nio.charset.Charset
import kotlin.text.Charsets.UTF_8

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/02/26
 */

fun String.toHttpUrl(): HttpUrl? = this.toHttpUrlOrNull()

fun ByteArray.toRequestBody(
    contentType: MediaType? = null,
    offset: Int = 0,
    byteCount: Int = size
): RequestBody {
    checkOffsetAndCount(size.toLong(), offset.toLong(), byteCount.toLong())
    return object : RequestBody() {
        override fun contentType() = contentType

        override fun contentLength() = byteCount.toLong()

        override fun writeTo(sink: BufferedSink) {
            sink.write(this@toRequestBody, offset, byteCount)
        }
    }
}

fun String.toMediaTypeOrNull(): MediaType? {
    return try {
        toMediaType()
    } catch (_: IllegalArgumentException) {
        null
    }
}

fun String.toMediaType(): MediaType? = this.toMediaTypeOrNull()

fun String.toRequestBody(contentType: MediaType? = null): RequestBody {
    var charset: Charset = UTF_8
    var finalContentType: MediaType? = contentType
    if (contentType != null) {
        val resolvedCharset = contentType.charset()
        if (resolvedCharset == null) {
            charset = UTF_8
            finalContentType = "$contentType; charset=utf-8".toMediaTypeOrNull()
        } else {
            charset = resolvedCharset
        }
    }
    val bytes = toByteArray(charset)
    return bytes.toRequestBody(finalContentType, 0, bytes.size)
}