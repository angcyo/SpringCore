package com.angcyo.spring.redis.aspect

import com.angcyo.spring.base.beanOf
import com.angcyo.spring.redis.Redis
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 * Redis缓存管理的切面
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/07/12
 */

@Aspect
@Component
class CacheAspect {

    @Autowired
    lateinit var redis: Redis

    @Around("@annotation(com.angcyo.spring.redis.aspect.ClearRedisCache)")
    fun clearRedisCache(point: ProceedingJoinPoint): Any? {
        val signature = point.signature
        if (signature is MethodSignature) {
            val clearRedisCache = signature.method.getAnnotation(ClearRedisCache::class.java)
            val key = beanOf(clearRedisCache.generateKey.java).getRedisCacheKey(clearRedisCache.key)

            if (clearRedisCache.before) {
                redis.clearCache(key, false)
            }

            //proceed
            val result = point.proceed(point.args)

            if (clearRedisCache.after) {
                if (clearRedisCache.ifSucceed) {
                    if (result == null || result != true || (result is Collection<*> && result.isEmpty())) {
                        //失败了, 则不清理缓存
                    } else {
                        redis.clearCache(key, false)
                    }
                } else {
                    redis.clearCache(key, false)
                }
            }

            return result
        } else {
            return point.proceed(point.args)
        }
    }

    @Around("@annotation(com.angcyo.spring.redis.aspect.SetRedisCache)")
    fun setRedisCache(point: ProceedingJoinPoint): Any? {
        val signature = point.signature
        if (signature is MethodSignature) {
            val setRedisCache = signature.method.getAnnotation(SetRedisCache::class.java)
            val key = beanOf(setRedisCache.generateKey.java).getRedisCacheKey(setRedisCache.key)

            //proceed
            val result = point.proceed(point.args)

            if (result == null) {
                redis.clearCache(key, false)
            } else {
                //缓存
                redis[key, setRedisCache.time] = result
            }

            return result
        } else {
            return point.proceed(point.args)
        }
    }
}