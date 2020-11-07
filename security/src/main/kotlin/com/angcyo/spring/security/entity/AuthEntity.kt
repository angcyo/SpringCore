package com.angcyo.spring.security.entity

import com.angcyo.spring.mysql.entity.BaseAuditEntity
import io.swagger.annotations.ApiModelProperty
import javax.persistence.Entity
import javax.persistence.Table
import javax.persistence.Transient

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/11/07
 *
 * 授权用户记录表, 记录用户账号, 用户密码, 角色权限
 */

@Entity
@Table(name = "auth_entity")
data class AuthEntity(
        @ApiModelProperty("登录用户名")
        var username: String? = null,
        @ApiModelProperty("登录用户密码(已加密)")
        var password: String? = null,
        @ApiModelProperty("是否激活账号")
        var enable: Boolean = true,
        @ApiModelProperty("用户对应的角色信息")
        @Transient
        var roles: List<RoleEntity>? = null,
) : BaseAuditEntity()