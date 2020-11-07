package com.angcyo.spring.mysql.entity

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonIgnore
import io.swagger.annotations.ApiModelProperty
import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDateTime
import javax.persistence.*

/**2020-11-07
 * https://www.cnblogs.com/niceyoo/p/10908647.html*/

@MappedSuperclass
@EntityListeners(value = [AuditingEntityListener::class])
abstract class BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @ApiModelProperty("唯一标识")
    var id: Long = 0

    @CreatedDate
    @Column(updatable = false)
    @ApiModelProperty("创建时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    var createdAt: LocalDateTime = LocalDateTime.now()

    @LastModifiedDate
    @ApiModelProperty("更新时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    var updatedAt: LocalDateTime = LocalDateTime.now()

    @CreatedBy
    @Column(updatable = false)
    @JsonIgnore
    @ApiModelProperty("创建者")
    var createdBy: String? = null

    @LastModifiedBy
    @JsonIgnore
    @ApiModelProperty("更新者")
    var updatedBy: String? = null
}