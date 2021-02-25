package com.angcyo.spring.mybatis.plus.test

import com.angcyo.spring.base.data.Result
import com.angcyo.spring.base.data.ok
import com.angcyo.spring.base.nowTimeString
import com.angcyo.spring.log.core.RecordLog
import com.angcyo.spring.mybatis.plus.test.table.TestBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/02/25
 */

@RestController
@RequestMapping("/test/mybatis")
class MybatisTestController {

    @Autowired
    lateinit var testServiceImpl: TestServiceImpl

    @RequestMapping("/save")
    @Transactional
    fun saveOne(): Result<TestBean> {
        val entity = TestBean().apply { data = nowTimeString() }
        val result = testServiceImpl.save(entity)
        return entity.ok()
    }

    @RequestMapping("/update")
    @Transactional
    fun updateOne(): Result<Boolean> {
        val entity = TestBean().apply { data = nowTimeString() }
        return testServiceImpl.saveOrUpdate(entity).ok()
    }

    @RequestMapping("/one")
    fun findOne(): TestBean? {
        //mysqlTestRepository.findById()
        return TestBean()
    }

    @RequestMapping("/all")
    @RecordLog("RecordLog测试")
    fun findAll(): Result<List<TestBean>> {
        return try {
            //testServiceImpl.getOne(null)
            testServiceImpl.list()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList<TestBean>()
        }.ok()
    }
}