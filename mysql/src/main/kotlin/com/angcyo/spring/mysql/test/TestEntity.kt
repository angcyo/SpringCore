package com.angcyo.spring.mysql.test

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

@Entity
data class TestEntity(
        @Id @GeneratedValue(strategy = GenerationType.AUTO)
        var id: Long = 0,
        var data: String? = null,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
        var createTime: LocalDateTime = LocalDateTime.now(),
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
        var updateTime: LocalDateTime = LocalDateTime.now()
)