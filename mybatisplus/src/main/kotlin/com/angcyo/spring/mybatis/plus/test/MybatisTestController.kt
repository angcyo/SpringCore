package com.angcyo.spring.mybatis.plus.test

import com.angcyo.spring.base.data.Result
import com.angcyo.spring.base.data.ok
import com.angcyo.spring.base.nowTimeString
import com.angcyo.spring.base.util.L
import com.angcyo.spring.log.core.RecordLog
import com.angcyo.spring.mybatis.plus.queryWrapper
import com.angcyo.spring.mybatis.plus.test.table.TestBean
import com.angcyo.spring.mybatis.plus.updateWrapper
import com.baomidou.mybatisplus.annotation.TableName
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
    fun save(): Result<TestBean> {
        val entity = TestBean().apply { data = nowTimeString() }
        val result = testServiceImpl.save(entity)
        return entity.ok()
    }

    @RequestMapping("/delete")
    fun delete(): Result<Int> {
        val cls = TestBean::class.java
        val ano = cls.getAnnotation(TableName::class.java)
        L.i(ano.value)
        return testServiceImpl.remove(queryWrapper {
            //删除最后一条
            apply("id = (select * from (select max(id) from test_mybatis_bean) a)")

            //删除最后多条
            //apply("id in (select * from (select id from test_mybatis_bean ORDER BY id desc limit 5) a)")
        }).ok()
    }

    @RequestMapping("/update")
    fun update(): Result<Boolean> {
        return testServiceImpl.update(updateWrapper {
            set("message", "update:${nowTimeString()}")

            apply("id = (select * from (select max(id) from test_mybatis_bean) a)")
        }).ok()
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