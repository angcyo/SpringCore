package com.angcyo.spring.mysql.test

import com.angcyo.spring.mysql.entity.BaseAuditEntity
import javax.persistence.Entity

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/11/04
 */

@Entity(name = "test_jpa_entity")
class TestEntity : BaseAuditEntity() {
    var data: String? = null
    var message: String? = null
    var count: Int = 0
}