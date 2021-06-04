package com.angcyo.spring.redis

import com.alibaba.fastjson.support.spring.FastJsonRedisSerializer
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.data.redis.cache.RedisCacheManager
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.StringRedisSerializer
import java.time.Duration


/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/11/04
 *
 * 注意: 如果库未使用报提示:
 * Could not autowire. No beans of 'RedisConnectionFactory' type found.
 */

@Configuration
class RedisConfiguration {

    /**
     * https://segmentfault.com/a/1190000020314044
     * https://blog.csdn.net/jiangyu1013/article/details/106623913
     * */
    @Bean(name = ["redisTemplate"])
    fun redisTemplate(redisConnectionFactory: RedisConnectionFactory): RedisTemplate<String, Any?>? {
        val template = RedisTemplate<String, Any?>()
        //template.setConnectionFactory(redisConnectionFactory)
        template.setConnectionFactory(redisConnectionFactory)

        /*//使用Jackson2JsonRedisSerializer来序列化和反序列化redis的value值
        val jackson2JsonRedisSerializer = Jackson2JsonRedisSerializer(Any::class.java)
        val mapper = ObjectMapper()
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY)
        // 指定序列化输入的类型，类必须是非final修饰的，final修饰的类，比如String,Integer等会跑出异常
        //mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.EVERYTHING)
        jackson2JsonRedisSerializer.setObjectMapper(mapper)
        template.valueSerializer = jackson2JsonRedisSerializer

        val stringRedisSerializer = StringRedisSerializer()
        //使用StringRedisSerializer来序列化和反序列化redis的key值
        template.keySerializer = stringRedisSerializer
        // hash的key也采用String的序列化方式
        template.hashKeySerializer = stringRedisSerializer

        // value序列化方式采用jackson
        template.valueSerializer = jackson2JsonRedisSerializer
        // hash的value序列化方式采用jackson
        template.hashValueSerializer = jackson2JsonRedisSerializer*/

        /*val om = ObjectMapper()
        GenericJackson2JsonRedisSerializer.registerNullValueSerializer(om, null)
        om.activateDefaultTyping(
            om.polymorphicTypeValidator,
            ObjectMapper.DefaultTyping.NON_FINAL,
            JsonTypeInfo.As.PROPERTY
        )
        om.registerModule(CustomModule()) //注册自定义模块*/

        val objectMapper = ObjectMapper()
                .registerModule(KotlinModule())
                .registerModule(JavaTimeModule())
                .enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY)

        val serializer = GenericJackson2JsonRedisSerializer(objectMapper)

        // 设置值（value）的序列化采用FastJsonRedisSerializer。
        template.valueSerializer = serializer
        template.hashValueSerializer = serializer

        // 设置键（key）的序列化采用StringRedisSerializer。
        template.keySerializer = StringRedisSerializer()
        template.hashKeySerializer = StringRedisSerializer()

        template.afterPropertiesSet()
        return template
    }

    /*@Bean
    fun redisTemplate(redisConnectionFactory: RedisConnectionFactory?): RedisTemplate<Any, Any> {
        val redisTemplate = RedisTemplate<Any, Any>()
        redisTemplate.setConnectionFactory(redisConnectionFactory!!)

        // 使用Jackson2JsonRedisSerialize 替换默认序列化
        val jackson2JsonRedisSerializer: Jackson2JsonRedisSerializer<*> = Jackson2JsonRedisSerializer(Any::class.java)
        val objectMapper = ObjectMapper()
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY)
        //objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL)
        jackson2JsonRedisSerializer.setObjectMapper(objectMapper)

        // 设置value的序列化规则和 key的序列化规则
        redisTemplate.valueSerializer = jackson2JsonRedisSerializer
        redisTemplate.keySerializer = StringRedisSerializer()
        redisTemplate.afterPropertiesSet()
        return redisTemplate
    }
*/

    /* @Bean
     fun redisTemplate(lettuceConnectionFactory: LettuceConnectionFactory?): RedisTemplate<String, Any>? {
         // 设置序列化
         val jackson2JsonRedisSerializer = Jackson2JsonRedisSerializer(Any::class.java)
         val om = ObjectMapper()
         om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY)
         //om.enableDefaultTyping(DefaultTyping.NON_FINAL)
         jackson2JsonRedisSerializer.setObjectMapper(om)
         // 配置redisTemplate
         val redisTemplate = RedisTemplate<String, Any>()
         redisTemplate.setConnectionFactory(lettuceConnectionFactory!!)
         val stringSerializer: RedisSerializer<*> = StringRedisSerializer()
         redisTemplate.keySerializer = stringSerializer // key序列化
         redisTemplate.valueSerializer = jackson2JsonRedisSerializer // value序列化
         redisTemplate.hashKeySerializer = stringSerializer // Hash key序列化
         redisTemplate.hashValueSerializer = jackson2JsonRedisSerializer // Hash value序列化
         redisTemplate.afterPropertiesSet()
         return redisTemplate
     }*/

    @Bean
    fun cacheManager(redisConnectionFactory: RedisConnectionFactory): RedisCacheManager? {
        val fastJsonRedisSerializer = FastJsonRedisSerializer(Any::class.java)
        val config: RedisCacheConfiguration =
            RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(5L))
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(fastJsonRedisSerializer))
                .disableCachingNullValues()
        return RedisCacheManager.builder(redisConnectionFactory).cacheDefaults(config).build()
    }


}