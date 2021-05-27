package com.angcyo.spring.mysql.example

import com.angcyo.spring.mysql.test.TestJpaEntity
import com.angcyo.spring.util.nowTimeString
import org.springframework.data.domain.Example
import org.springframework.data.domain.ExampleMatcher


/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/11/09
 *
 * https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#query-by-example
 */

object ExampleUtil {
    fun test() {
        val entity = TestJpaEntity()
        entity.data = nowTimeString()

        //val example = Example.of(entity)

        val matcher: ExampleMatcher = ExampleMatcher.matching()
            .withIgnorePaths("lastname")
            .withIncludeNullValues()
            .withStringMatcher(ExampleMatcher.StringMatcher.DEFAULT)

        val example = Example.of<TestJpaEntity>(entity, matcher)
    }
}