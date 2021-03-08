package com.angcyo.spring.encrypt.rsa.advice

import com.angcyo.spring.base.getSafe
import com.angcyo.spring.base.util.md5
import com.angcyo.spring.base.util.nowTime
import com.angcyo.spring.encrypt.rsa.config.SecretKeyConfig
import com.angcyo.spring.encrypt.rsa.header
import com.angcyo.spring.encrypt.rsa.util.Base64Util
import com.angcyo.spring.encrypt.rsa.util.RSAUtil
import com.angcyo.spring.encrypt.rsa.util.SecurityCodeUtil
import com.angcyo.spring.redis.Redis
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpInputMessage
import java.io.BufferedReader
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.io.InputStreamReader
import java.nio.charset.Charset
import java.util.stream.Collectors
import kotlin.math.absoluteValue

/**
 * Author:Bobby
 * DateTime:2019/4/9
 */
class DecryptHttpInputMessage(
    inputMessage: HttpInputMessage,
    redis: Redis,
    secretKeyConfig: SecretKeyConfig,
    /**是否要检查频繁请求*/
    checkFrequent: Boolean = false
) : HttpInputMessage {

    private val log = LoggerFactory.getLogger(this.javaClass)
    private val headers: HttpHeaders
    private val body: InputStream

    init {
        val privateKey = secretKeyConfig.privateKey
        require(!privateKey.isNullOrEmpty()) { "privateKey is null" }
        headers = inputMessage.headers
        var content = BufferedReader(InputStreamReader(inputMessage.body)).lines()
            .collect(Collectors.joining(System.lineSeparator()))

        val sign = inputMessage.header(secretKeyConfig.securityCodeKey)
        if (sign.isNullOrEmpty()) {
            throw IllegalArgumentException("无效的签名[1]")
        }
        val signList = SecurityCodeUtil.decode(sign)
        val contentMd5 = content.md5()

        //内容md5值比对
        if (signList.getSafe(0) != contentMd5) {
            throw IllegalArgumentException("无效的签名[2]")
        }

        //安全码比对
        if (signList.getSafe(1)?.startsWith(secretKeyConfig.securityCode.toUpperCase()) == true) {
        } else {
            throw IllegalArgumentException("无效的签名[3]")
        }

        //时间比对
        val time = signList.getSafe(2)?.toLongOrNull() ?: 0
        val nowTime = nowTime()
        if ((nowTime - time).absoluteValue > secretKeyConfig.securityTimeGap * 1000) {
            throw IllegalArgumentException("无效的客户端时间")
        }

        val decryptBody: String
        /*if (content.startsWith("{")) {
            log.info("未加密:{}", content)
            decryptBody = content
        } else {*/
        val json = StringBuilder()
        content = content.replace(" ".toRegex(), "+")
        if (!content.isNullOrEmpty()) {
            val contents = content.split("\\|").toTypedArray()
            for (value in contents) {
                val decrypt =
                    String(
                        RSAUtil.decrypt(Base64Util.decode(value), privateKey),
                        Charset.forName(secretKeyConfig.charset)
                    )
                json.append(decrypt)
            }
        }
        decryptBody = json.toString()

        if (checkFrequent) {
            //解密后的内容md5
            val decryptBodyMd5 = decryptBody.md5()!!
            if (redis.hasKey(decryptBodyMd5)) {
                throw IllegalArgumentException("请勿频繁请求")
            }

            //30分钟后才可以再次请求
            redis[decryptBodyMd5, nowTime] = 30 * 60
        }

        if (secretKeyConfig.isShowLog) {
            log.info("密文：{},原文：{}", content, decryptBody)
        }
        //}
        body = ByteArrayInputStream(decryptBody.toByteArray())
    }


    override fun getBody(): InputStream {
        return body
    }

    override fun getHeaders(): HttpHeaders {
        return headers
    }
}