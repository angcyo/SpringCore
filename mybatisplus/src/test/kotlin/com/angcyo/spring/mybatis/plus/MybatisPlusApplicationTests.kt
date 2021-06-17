package com.angcyo.spring.mybatis.plus

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest()
class MybatisPlusApplicationTests {

    @Test
    fun contextLoads() {
    }

    @Test
    fun test() {
        println("...")
        val text = ",5,9,"
        println(text.substring(0, text.length - 3))
    }

}
