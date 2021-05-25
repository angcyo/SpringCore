package com.angcyo.spring.mybatis.plus.test

import com.angcyo.spring.base.data.Result
import com.angcyo.spring.base.data.ok
import com.angcyo.spring.log.core.RecordLog
import com.angcyo.spring.mybatis.plus.queryWrapper
import com.angcyo.spring.mybatis.plus.test.table.TestTable
import com.angcyo.spring.util.L
import com.angcyo.spring.util.nowTimeString
import com.baomidou.mybatisplus.annotation.TableName
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import springfox.documentation.annotations.ApiIgnore

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/02/25
 */

@RestController
@RequestMapping("/test/mybatis")
@ApiIgnore
class MybatisTestController {

    @Autowired
    lateinit var testServiceImpl: TestServiceImpl

    @RequestMapping("/save")
    @Transactional
    fun save(): Result<TestTable> {
        val entity = TestTable().apply { data = nowTimeString() }
        val result = testServiceImpl.save(entity)
        return entity.ok()
    }

    @RequestMapping("/delete")
    fun delete(): Result<Int> {
        val cls = TestTable::class.java
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
    fun update(): Result<TestTable> {
        //1
        //FieldFill.INSERT_UPDATE 不会触发
        /*val result = testServiceImpl.update(updateWrapper {
            set("message", "update:${nowTimeString()}")

            apply("id = (select * from (select max(id) from test_mybatis_bean) a)")
        })*/

        //2
        //会触发
        /*val result = testServiceImpl.update(TestBean(), updateWrapper {
            eq("id", 1)
        })*/

        //3
        /*val result = testServiceImpl.updateById(TestBean().apply { id = 1 })
        return if (result) {
            find()
        } else {
            null.ok()
        }*/

        //4
        val last = find().data

        return if (last == null) {
            null.ok()
        } else {
            testServiceImpl.updateById(last.apply {
                updatedAt = null //置空后, 才会自动插入
                message = "update:${nowTimeString()}"
            })
            last.ok()
        }
    }

    @RequestMapping("/find")
    fun find(): Result<TestTable> {
        return testServiceImpl.getOne(queryWrapper {
            apply("id = (select * from (select max(id) from test_mybatis_bean) a)")
        }).ok()
    }

    @RequestMapping("/all")
    @RecordLog("RecordLog测试")
    fun findAll(): Result<List<TestTable>> {
        return try {
            //testServiceImpl.getOne(null)
            testServiceImpl.list()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList<TestTable>()
        }.ok()
    }
}