package com.angcyo.spring.encrypt.rsa.advice

import com.alibaba.fastjson.JSON
import com.angcyo.spring.encrypt.rsa.annotation.Encrypt
import com.angcyo.spring.encrypt.rsa.config.SecretKeyConfig
import com.angcyo.spring.encrypt.rsa.isIgnoreRsa
import com.angcyo.spring.encrypt.rsa.util.Base64Util
import com.angcyo.spring.encrypt.rsa.util.RSAUtil
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.MethodParameter
import org.springframework.http.MediaType
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.server.ServerHttpRequest
import org.springframework.http.server.ServerHttpResponse
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice

/**
 * Author:Bobby
 * DateTime:2019/4/9
 */
@ControllerAdvice
class EncryptResponseBodyAdvice : ResponseBodyAdvice<Any?> {
    private val log = LoggerFactory.getLogger(this.javaClass)
    private var encrypt = false

    @Autowired
    lateinit var secretKeyConfig: SecretKeyConfig

    override fun supports(returnType: MethodParameter, converterType: Class<out HttpMessageConverter<*>>): Boolean {
        encrypt = false
        if (returnType.method!!.isAnnotationPresent(Encrypt::class.java) && secretKeyConfig.isOpen) {
            encrypt = true
        }
        return encrypt
    }

    override fun beforeBodyWrite(
        body: Any?,
        returnType: MethodParameter,
        selectedContentType: MediaType,
        selectedConverterType: Class<out HttpMessageConverter<*>>,
        request: ServerHttpRequest,
        response: ServerHttpResponse
    ): Any? {
        // EncryptResponseBodyAdvice.setEncryptStatus(false);
        // Dynamic Settings Not Encrypted
        val status = encryptLocal.get()
        if (null != status && !status) {
            encryptLocal.remove()
            return body
        }
        if (encrypt) {
            if (request.isIgnoreRsa()) {
                return body
            }
            val publicKey: String? = secretKeyConfig.publicKey
            try {
                val content: String = JSON.toJSONString(body)
                if (publicKey.isNullOrEmpty()) {
                    throw NullPointerException("Please configure rsa.encrypt.privatekeyc parameter!")
                }
                val data = content.toByteArray()
                val encodedData: ByteArray = RSAUtil.encrypt(data, publicKey)
                val result: String = Base64Util.encode(encodedData)
                if (secretKeyConfig.isShowLog) {
                    log.info("原文：{}，密文：{}", content, result)
                }
                return result
            } catch (e: Exception) {
                log.error("Encrypted data exception", e)
            }
        }
        return body
    }

    companion object {
        private val encryptLocal = ThreadLocal<Boolean>()
    }
}