package com.angcyo.spring.encrypt.rsa.advice

import com.angcyo.spring.encrypt.rsa.annotation.Decrypt
import com.angcyo.spring.encrypt.rsa.config.SecretKeyConfig
import com.angcyo.spring.encrypt.rsa.isIgnoreRsa
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

    @Autowired
    lateinit var secretKeyConfig: SecretKeyConfig

    override fun supports(
        methodParameter: MethodParameter,
        targetType: Type,
        converterType: Class<out HttpMessageConverter<*>>
    ): Boolean {
        if (methodParameter.method!!.isAnnotationPresent(Decrypt::class.java) && secretKeyConfig.isOpen) {
            encrypt = true
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
                    secretKeyConfig.privateKey,
                    secretKeyConfig.charset,
                    secretKeyConfig.isShowLog
                )
            } catch (e: Exception) {
                log.error("Decryption failed", e)
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