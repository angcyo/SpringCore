package com.angcyo.spring.redis.aspect

/**
 *
 * 将方法返回的结果, 放到redis缓存中
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/06/02
 */

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class SetRedisCache(

    /**缓存key*/
    val key: String,

    /**缓存时长, 秒*/
    val time: Long = 60
)
