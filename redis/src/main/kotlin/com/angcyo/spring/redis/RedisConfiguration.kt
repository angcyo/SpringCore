package com.angcyo.spring.redis

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.PropertyAccessor
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer


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

    /**https://segmentfault.com/a/1190000020314044*/
    @Bean(name = ["redisTemplate"])
    fun redisTemplate(redisConnectionFactory: RedisConnectionFactory?): RedisTemplate<String, Any?>? {
        val template = RedisTemplate<String, Any?>()
        template.setConnectionFactory(redisConnectionFactory!!)
        //使用Jackson2JsonRedisSerializer来序列化和反序列化redis的value值
        val jackson2JsonRedisSerializer = Jackson2JsonRedisSerializer(Any::class.java)
        val mapper = ObjectMapper()
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY)
        mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL)
        jackson2JsonRedisSerializer.setObjectMapper(mapper)
        template.valueSerializer = jackson2JsonRedisSerializer
        val stringRedisSerializer = StringRedisSerializer()
        //使用StringRedisSerializer来序列化和反序列化redis的key值
        template.keySerializer = stringRedisSerializer
        template.keySerializer = stringRedisSerializer
        // hash的key也采用String的序列化方式
        template.hashKeySerializer = stringRedisSerializer
        // value序列化方式采用jackson
        template.valueSerializer = jackson2JsonRedisSerializer
        // hash的value序列化方式采用jackson
        template.hashValueSerializer = jackson2JsonRedisSerializer
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
        objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL)
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
         om.enableDefaultTyping(DefaultTyping.NON_FINAL)
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

}