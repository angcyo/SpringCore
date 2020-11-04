package com.angcyo.spring.mysql.test

import com.angcyo.spring.core.data.Result
import com.angcyo.spring.core.data.ok
import com.angcyo.spring.core.nowTimeString
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/11/04
 */

@RestController
@RequestMapping("/test")
class TestController {

    @Autowired
    lateinit var testRepository: TestRepository

    @RequestMapping("/save")
    @Transactional
    fun saveOne(): Result<TestEntity> {
        val entity = TestEntity(data = nowTimeString())
        val result = testRepository.save(entity)
        return result.ok()
    }

    @RequestMapping("/one")
    fun findOne(): TestEntity? {
        return TestEntity()
    }

    @RequestMapping("/all")
    fun findAll(): Result<List<TestEntity>> {
        return testRepository.findAll().ok()
    }
}