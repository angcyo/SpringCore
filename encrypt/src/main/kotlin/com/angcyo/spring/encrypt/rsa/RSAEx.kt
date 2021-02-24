package com.angcyo.spring.encrypt.rsa

import com.angcyo.spring.base.servlet.isTruthy
import org.springframework.http.HttpMessage

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/02/24
 */
fun HttpMessage.isIgnoreRsa() = headers.getFirst("ignore-rsa").isTruthy()