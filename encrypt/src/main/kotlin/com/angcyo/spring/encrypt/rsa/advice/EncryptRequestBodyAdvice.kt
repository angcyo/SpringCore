package com.angcyo.spring.encrypt.rsa.advice

import com.angcyo.spring.encrypt.rsa.annotation.CheckFrequentRequest
import com.angcyo.spring.encrypt.rsa.annotation.Decrypt
import com.angcyo.spring.encrypt.rsa.annotation.IgnoreDecryptException
import com.angcyo.spring.encrypt.rsa.config.SecretKeyConfig
import com.angcyo.spring.encrypt.rsa.isIgnoreRsa
import com.angcyo.spring.redis.Redis
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.MethodParameter
import org.springframework.http.HttpInputMessage
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice
import java.lang.reflect.Type

/**
 * Author:Bobby
 * DateTime:2019/4/9
 */
@ControllerAdvice
class EncryptRequestBodyAdvice : RequestBodyAdvice {

    private val log = LoggerFactory.getLogger(this.javaClass)
    private var encrypt = false
    private var ignoreDecryptException = false

    @Autowired
    lateinit var secretKeyConfig: SecretKeyConfig

    @Autowired
    lateinit var redis: Redis

    override fun supports(
        methodParameter: MethodParameter,
        targetType: Type,
        converterType: Class<out HttpMessageConverter<*>>
    ): Boolean {
        encrypt = false
        ignoreDecryptException = false
        if (secretKeyConfig.isOpen) {
            if (methodParameter.method!!.isAnnotationPresent(Decrypt::class.java)) {
                encrypt = true
            }
            if (methodParameter.method!!.isAnnotationPresent(IgnoreDecryptException::class.java)) {
                ignoreDecryptException = true
            }
        }
        return encrypt
    }

    override fun handleEmptyBody(
        body: Any?,
        inputMessage: HttpInputMessage,
        parameter: MethodParameter,
        targetType: Type,
        converterType: Class<out HttpMessageConverter<*>>
    ): Any? {
        return body
    }

    override fun beforeBodyRead(
        inputMessage: HttpInputMessage,
        parameter: MethodParameter,
        targetType: Type,
        converterType: Class<out HttpMessageConverter<*>>
    ): HttpInputMessage {
        if (encrypt) {
            try {
                if (inputMessage.isIgnoreRsa()) {
                    return inputMessage
                }
                return DecryptHttpInputMessage(
                    inputMessage,
                    redis,
                    secretKeyConfig,
                    parameter.method!!.isAnnotationPresent(CheckFrequentRequest::class.java)
                )
            } catch (e: Exception) {
                log.error("Decryption failed", e)
                if (!ignoreDecryptException) {
                    throw e
                }
            }
        }
        return inputMessage
    }

    override fun afterBodyRead(
        body: Any,
        inputMessage: HttpInputMessage,
        parameter: MethodParameter,
        targetType: Type,
        converterType: Class<out HttpMessageConverter<*>>
    ): Any {
        return body
    }
}