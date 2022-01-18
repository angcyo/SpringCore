package com.angcyo.spring.security.service

import com.angcyo.spring.base.extension.apiError
import com.angcyo.spring.base.servlet.param
import com.angcyo.spring.base.servlet.request
import com.angcyo.spring.security.bean.ClientType
import com.angcyo.spring.security.jwt.currentUserIdOrDef
import com.angcyo.spring.util.*
import javax.servlet.http.HttpServletRequest

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/05/29
 */

/**获取客户端唯一标识的key*/
fun HttpServletRequest.codeKey(): String {
    val uuid = param("clientUuid")
    var key = ""
    if (uuid.isNullOrEmpty()) {
        //根据session id, 将code 存到redis
        getSession(true)?.apply {
            key = id
        }.elseNull {
            //key = uuid()
            apiError("无效的客户端标识")
        }
    } else {
        key = uuid
    }
    return key
}

/**客户端类型*/
fun currentClientType(def: String = ClientType.Web.value) = request()?.clientType() ?: def

fun currentClientUuid() = request()?.clientUuid()

/**客户端的uuid*/
fun HttpServletRequest.clientUuid() = param("clientUuid") ?: param("client-uuid")

/**从请求头中, 获取客户端类型*/
fun HttpServletRequest.clientType() = param("clientType") ?: param("client-type")

/**从字符串中解析用户id
 * xxxxxxxx.xxx*/
fun String.parseUserId(): Long? {
    val index = indexOf(".")
    if (index == -1) {
        return this.base64Decoder().toLongOrNull()
    }
    return this.substring(index + 1).base64Decoder().toLongOrNull()
}

/**
 * ug1AjFSIug1AjFSI.MQ==
 * */
fun generateShortUserUuid(length: Int = 8): String {
    return "${generateShortUuid(length)}.${currentUserIdOrDef(-1).str().base64Encode()}"
}