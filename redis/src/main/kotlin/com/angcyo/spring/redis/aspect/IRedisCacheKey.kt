package com.angcyo.spring.redis.aspect

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/07/12
 */

interface IRedisCacheKey {

    /**[key]的生成*/
    fun getRedisCacheKey(key: String): String = key
}