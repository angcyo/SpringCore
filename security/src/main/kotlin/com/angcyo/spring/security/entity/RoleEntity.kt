package com.angcyo.spring.security.entity

import com.angcyo.spring.mysql.entity.BaseAuditEntity
import io.swagger.annotations.ApiModelProperty
import javax.persistence.Entity
import javax.persistence.Table

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/11/07
 *
 * 角色权限
 */

@Entity
@Table(name = "role_entity")
data class RoleEntity(
        @ApiModelProperty("授权的用户Id")
        var authId: Long = 0,
        @ApiModelProperty("授权的用户角色")
        var role: String? = null,
        @ApiModelProperty("角色描述")
        var des: String? = null,
) : BaseAuditEntity()