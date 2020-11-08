package com.angcyo.spring.mysql.test

import com.angcyo.spring.base.nowTimeString
import com.angcyo.spring.core.data.ok
import com.angcyo.spring.core.data.Result
import com.angcyo.spring.log.core.RecordLog
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
@RequestMapping("/test/mysql")
class MysqlTestController {

    @Autowired
    lateinit var mysqlTestRepository: MysqlTestRepository

    @RequestMapping("/save")
    @Transactional
    fun saveOne(): Result<TestEntity> {
        val entity = TestEntity(data = nowTimeString())
        val result = mysqlTestRepository.save(entity)
        return result.ok()
    }

    @RequestMapping("/one")
    fun findOne(): TestEntity? {
        return TestEntity()
    }

    @RequestMapping("/all")
    @RecordLog("RecordLog测试")
    fun findAll(): Result<List<TestEntity>> {
        return try {
            mysqlTestRepository.findAll()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList<TestEntity>()
        }.ok()
    }
}