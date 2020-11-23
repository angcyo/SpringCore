package com.angcyo.spring.mysql.entity

import com.angcyo.spring.base.Base
import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonIgnore
import io.swagger.annotations.ApiModel
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
@ApiModel("基础表结构信息")
abstract class BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @ApiModelProperty("唯一标识")
    open var id: Long = 0

    @CreatedDate
    @Column(updatable = false)
    @ApiModelProperty("创建时间")
    @DateTimeFormat(pattern = Base.DEFAULT_DATE_TIME_FORMATTER)
    @JsonFormat(pattern = Base.DEFAULT_DATE_TIME_FORMATTER, timezone = "GMT+8")
    open var createdAt: LocalDateTime = LocalDateTime.now()

    @LastModifiedDate
    @ApiModelProperty("更新时间")
    @DateTimeFormat(pattern = Base.DEFAULT_DATE_TIME_FORMATTER)
    @JsonFormat(pattern = Base.DEFAULT_DATE_TIME_FORMATTER, timezone = "GMT+8")
    open var updatedAt: LocalDateTime = LocalDateTime.now()

    @CreatedBy
    @Column(updatable = false)
    @JsonIgnore
    @ApiModelProperty("创建者")
    open var createdBy: String? = null

    @LastModifiedBy
    @JsonIgnore
    @ApiModelProperty("更新者")
    open var updatedBy: String? = null
}