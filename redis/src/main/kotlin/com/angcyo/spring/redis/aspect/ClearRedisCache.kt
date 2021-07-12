package com.angcyo.spring.redis.aspect

import kotlin.reflect.KClass

/**
 *
 * 标记需要清理redis缓存
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/06/02
 */

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class ClearRedisCache(
    /**
     * key的匹配规则
     * [com.angcyo.spring.redis.Redis.keyList]
     * */
    val key: String,

    /**用于生成key*/
    val generateKey: KClass<out IRedisCacheKey> = PlaceholderRedisCacheKey::class,

    /**简单的判断返回结果是否成功, 如果为false, 则直接清理*/
    val ifSucceed: Boolean = true,

    /**方法调用之前清理*/
    val before: Boolean = false,

    /**方法调用之后清理*/
    val after: Boolean = true,
)
