package com.angcyo.spring.redis.test

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/11/04
 */

data class TestBean(
    var message: String? = "message",
    var test: TestBean? = null
)