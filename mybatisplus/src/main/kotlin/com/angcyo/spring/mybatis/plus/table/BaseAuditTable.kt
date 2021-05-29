package com.angcyo.spring.mybatis.plus.table

import com.angcyo.spring.base.Base
import com.baomidou.mybatisplus.annotation.FieldFill
import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableField
import com.baomidou.mybatisplus.annotation.TableId
import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonIgnore
import com.gitee.sunchenbin.mybatis.actable.annotation.IsAutoIncrement
import com.gitee.sunchenbin.mybatis.actable.annotation.IsKey
import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDateTime
import javax.persistence.Column
import javax.persistence.EntityListeners
import javax.persistence.MappedSuperclass

/**2020-11-07
 * https://www.cnblogs.com/niceyoo/p/10908647.html
 * 自动填充字段, 必须指定为null,否则不会自动填充
 * 使用Bean更新数据时, 字段为null, 则不会更新此字段
 * */

@MappedSuperclass
@EntityListeners(value = [AuditingEntityListener::class])
@ApiModel("基础表结构信息")
abstract class BaseAuditTable {

    @IsKey
    @IsAutoIncrement
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty("数据Id")
    @com.gitee.sunchenbin.mybatis.actable.annotation.Column(comment = "主键")
    open var id: Long? = null

    @Column(updatable = false)
    @ApiModelProperty("创建时间")
    @DateTimeFormat(pattern = Base.DEFAULT_DATE_TIME_FORMATTER)
    @JsonFormat(pattern = Base.DEFAULT_DATE_TIME_FORMATTER, timezone = "GMT+8")
    @TableField(fill = FieldFill.INSERT)
    @com.gitee.sunchenbin.mybatis.actable.annotation.Column(comment = "记录创建时间")
    open var createdAt: LocalDateTime? = null

    @ApiModelProperty("更新时间")
    @DateTimeFormat(pattern = Base.DEFAULT_DATE_TIME_FORMATTER)
    @JsonFormat(pattern = Base.DEFAULT_DATE_TIME_FORMATTER, timezone = "GMT+8")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @com.gitee.sunchenbin.mybatis.actable.annotation.Column(comment = "记录更新时间")
    open var updatedAt: LocalDateTime? = null

    @CreatedBy
    @Column(updatable = false)
    @JsonIgnore
    @ApiModelProperty("创建者")
    @TableField(fill = FieldFill.INSERT)
    @com.gitee.sunchenbin.mybatis.actable.annotation.Column(comment = "记录创建者")
    open var createdBy: String? = null

    @LastModifiedBy
    @JsonIgnore
    @ApiModelProperty("更新者")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @com.gitee.sunchenbin.mybatis.actable.annotation.Column(comment = "记录更新者")
    open var updatedBy: String? = null

    //@TableLogic(value = "0", delval = "1")
    //@TableField(fill = FieldFill.INSERT) //,使用delete语句时自动生效
    @ApiModelProperty("逻辑删除（0 未删除、1 删除）")
    @com.gitee.sunchenbin.mybatis.actable.annotation.Column(comment = "逻辑删除（0 未删除、1 删除）")
    open var deleteFlag: Int? = 0
}