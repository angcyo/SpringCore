package com.angcyo.spring.encrypt.rsa.annotation

import com.angcyo.spring.encrypt.rsa.advice.EncryptRequestBodyAdvice
import com.angcyo.spring.encrypt.rsa.advice.EncryptResponseBodyAdvice
import com.angcyo.spring.encrypt.rsa.config.SecretKeyConfig
import org.springframework.context.annotation.Import
import java.lang.annotation.Inherited

/**
 * Author:Bobby
 * DateTime:2019/4/9 16:44
 */
@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.CLASS)
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
@Inherited
@MustBeDocumented
@Import(SecretKeyConfig::class, EncryptResponseBodyAdvice::class, EncryptRequestBodyAdvice::class)
annotation class EnableSecurity