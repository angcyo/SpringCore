package com.angcyo.spring.encrypt.rsa.util

import java.util.*


/**
 * Base64
 * Author:Bobby
 * DateTime:2019/4/9
 */
object Base64Util {
    /**
     * Decoding to binary
     * @param base64 base64
     * @return byte
     * @throws Exception Exception
     */
    @Throws(Exception::class)
    fun decode(base64: String): ByteArray {
        return Base64.getDecoder().decode(base64)
    }

    /**
     * Binary encoding as a string
     * @param bytes byte
     * @return String
     * @throws Exception Exception
     */
    @Throws(Exception::class)
    fun encode(bytes: ByteArray): String {
        return String(Base64.getEncoder().encode(bytes))
    }
}