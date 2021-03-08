package com.angcyo.spring.encrypt.rsa

import com.angcyo.spring.base.servlet.isTruthy
import org.springframework.http.HttpMessage

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/02/24
 */

/**忽略rsa验证*/
fun HttpMessage.isIgnoreRsa() = header("ignore-rsa").isTruthy()

/**请求头*/
fun HttpMessage.header(key: String) = headers.getFirst(key)