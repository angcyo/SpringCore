package com.angcyo.spring.encrypt.rsa.advice

import com.angcyo.spring.encrypt.rsa.util.Base64Util
import com.angcyo.spring.encrypt.rsa.util.RSAUtil
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpInputMessage
import java.io.BufferedReader
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.io.InputStreamReader
import java.nio.charset.Charset
import java.util.stream.Collectors

/**
 * Author:Bobby
 * DateTime:2019/4/9
 */
class DecryptHttpInputMessage(
    inputMessage: HttpInputMessage,
    privateKey: String?,
    charset: String,
    showLog: Boolean
) : HttpInputMessage {
    private val log = LoggerFactory.getLogger(this.javaClass)
    private val headers: HttpHeaders
    private val body: InputStream
    override fun getBody(): InputStream {
        return body
    }

    override fun getHeaders(): HttpHeaders {
        return headers
    }

    init {
        require(!privateKey.isNullOrEmpty()) { "privateKey is null" }
        headers = inputMessage.headers
        var content = BufferedReader(InputStreamReader(inputMessage.body)).lines()
            .collect(Collectors.joining(System.lineSeparator()))
        val decryptBody: String
        if (content.startsWith("{")) {
            log.info("Unencrypted without decryption:{}", content)
            decryptBody = content
        } else {
            val json = StringBuilder()
            content = content.replace(" ".toRegex(), "+")
            if (!content.isNullOrEmpty()) {
                val contents = content.split("\\|").toTypedArray()
                for (value in contents) {
                    val decrypt =
                        String(RSAUtil.decrypt(Base64Util.decode(value), privateKey), Charset.forName(charset))
                    json.append(decrypt)
                }
            }
            decryptBody = json.toString()
            if (showLog) {
                log.info("Encrypted data received：{},After decryption：{}", content, decryptBody)
            }
        }
        body = ByteArrayInputStream(decryptBody.toByteArray())
    }
}