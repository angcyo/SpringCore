package com.angcyo.spring.mybatis.plus.table

import com.angcyo.spring.util.Constant
import com.angcyo.spring.util.json.toJson
import com.baomidou.mybatisplus.annotation.FieldFill
import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableField
import com.baomidou.mybatisplus.annotation.TableId
import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import com.gitee.sunchenbin.mybatis.actable.annotation.Column
import com.gitee.sunchenbin.mybatis.actable.annotation.IsAutoIncrement
import com.gitee.sunchenbin.mybatis.actable.annotation.IsKey
import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import org.springframework.cache.annotation.Cacheable
import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDateTime

/**2020-11-07
 * https://www.cnblogs.com/niceyoo/p/10908647.html
 * 自动填充字段, 必须指定为null,否则不会自动填充
 * 使用Bean更新数据时, 字段为null, 则不会更新此字段
 *
 * [com.angcyo.spring.app.audit.UserAuditor]
 * [com.angcyo.spring.mybatis.plus.base.MybatisTableMetaObjectHandler]
 * [com.angcyo.spring.security.UserMybatisTableMetaObjectHandler]
 * */

@ApiModel("基础表结构信息")
@Cacheable
abstract class BaseAuditTable {

    @IsKey
    @IsAutoIncrement
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty("数据Id")
    @Column(comment = "主键")
    var id: Long? = null

    //redis Java 8 date/time type `java.time.LocalDateTime` not supported by default
    @JsonDeserialize(using = LocalDateTimeDeserializer::class)
    @JsonSerialize(using = LocalDateTimeSerializer::class)
    @ApiModelProperty("创建时间", hidden = true)
    @DateTimeFormat(pattern = Constant.DEFAULT_DATE_TIME_FORMATTER)
    @JsonFormat(pattern = Constant.DEFAULT_DATE_TIME_FORMATTER, timezone = "GMT+8")
    @TableField(fill = FieldFill.INSERT)
    @Column(comment = "记录创建时间")
    var createdAt: LocalDateTime? = null

    @JsonDeserialize(using = LocalDateTimeDeserializer::class)
    @JsonSerialize(using = LocalDateTimeSerializer::class)
    @ApiModelProperty("更新时间", hidden = true)
    @DateTimeFormat(pattern = Constant.DEFAULT_DATE_TIME_FORMATTER)
    @JsonFormat(pattern = Constant.DEFAULT_DATE_TIME_FORMATTER, timezone = "GMT+8")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @Column(comment = "记录更新时间")
    var updatedAt: LocalDateTime? = null

    @JsonIgnore
    @ApiModelProperty("创建者", hidden = true)
    @TableField(fill = FieldFill.INSERT)
    @Column(comment = "记录创建者")
    var createdBy: String? = null

    @JsonIgnore
    @ApiModelProperty("更新者", hidden = true)
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @Column(comment = "记录更新者")
    var updatedBy: String? = null

    //@TableLogic(value = "0", delval = "1")
    //@TableField(fill = FieldFill.INSERT) //,使用delete语句时自动生效
    @ApiModelProperty("逻辑删除（0 未删除、1 删除）", hidden = true)
    @Column(comment = "逻辑删除（0 未删除、1 删除）")
    @JsonIgnore
    var deleteFlag: Int? = 0

    override fun toString(): String {
        return toJson() ?: super.toString()
    }
}