package com.angcyo.spring.redis.test

import com.angcyo.spring.base.data.Result
import com.angcyo.spring.base.data.ok
import com.angcyo.spring.redis.Redis
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

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

    @RequestMapping("/hello/{msg}")
    fun hello(@PathVariable(required = true) msg: String): String? {
        return "hello redis->$msg"
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