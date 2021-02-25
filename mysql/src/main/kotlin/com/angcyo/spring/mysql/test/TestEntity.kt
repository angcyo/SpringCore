package com.angcyo.spring.mysql.test

import com.angcyo.spring.base.Base
import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDateTime
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/11/04
 */

@Entity(name = "test_jpa_entity")
data class TestEntity(
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long = 0,
    var data: String? = null,
    @JsonFormat(pattern = Base.DEFAULT_DATE_TIME_FORMATTER, timezone = "GMT+8")
    var createdAt: LocalDateTime = LocalDateTime.now(),
    @JsonFormat(pattern = Base.DEFAULT_DATE_TIME_FORMATTER, timezone = "GMT+8")
    var updatedAt: LocalDateTime = LocalDateTime.now()
)