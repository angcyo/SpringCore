package com.angcyo.spring.redis.test

import com.angcyo.spring.core.data.Result
import com.angcyo.spring.core.data.ok
import com.angcyo.spring.redis.Redis
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/11/04
 */

@RestController
@RequestMapping("/test/redis")
class RedisTestController {

    @Autowired
    lateinit var redis: Redis

    @RequestMapping("/hello")
    fun hello(): String? {
        return "hello redis"
    }

    @RequestMapping("/set")
    fun setValue(@RequestParam value: String?): Result<Boolean> {
        return redis.set("key", value).ok()
    }

    @RequestMapping("/get")
    fun getValue(): Result<String> {
        return redis["key"].ok()
    }

    @RequestMapping("/setBean")
    fun setBean(@RequestBody bean: TestBean?): Result<Boolean> {
        return redis.set("bean", bean).ok()
    }

    @RequestMapping("/getBean")
    fun getBean(): Result<TestBean> {
        return redis["bean"].ok()
    }
}