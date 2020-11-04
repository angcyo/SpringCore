package com.angcyo.spring.mysql.test

import com.angcyo.spring.core.nowDate
import java.sql.Date
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/11/04
 */

@Entity
data class TestEntity(
        @Id @GeneratedValue(strategy = GenerationType.AUTO)
        var id: Long = 0,
        var data: String? = null,
        var createTime: Date = nowDate(),
        var updateTime: Date = nowDate()
)