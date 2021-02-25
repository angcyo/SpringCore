package com.angcyo.spring.mybatis.plus.test

import com.angcyo.spring.mybatis.plus.test.table.TestBean
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import org.springframework.stereotype.Service

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/02/25
 */

@Service
class TestServiceImpl : ServiceImpl<TestMapper, TestBean>(), TestService {
    fun test() {
        /*update().setSql()
        //query().sql
        remove(QueryWrapper<TestMapper>().apply {

        })*/
    }
}