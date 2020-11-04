package com.angcyo.spring.mysql.test

import org.springframework.data.jpa.repository.JpaRepository

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/11/04
 */

interface TestRepository : JpaRepository<TestEntity, Long> {
}