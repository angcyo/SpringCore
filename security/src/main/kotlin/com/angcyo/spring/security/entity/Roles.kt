package com.angcyo.spring.security.entity

import org.springframework.security.core.authority.SimpleGrantedAuthority

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/11/09
 */

object Roles {
    /**普通用户*/
    const val USER = "user"

    /**临时用户*/
    const val GUEST = "guest"

    /**管理员*/
    const val ADMIN = "admin"

    /**超级管理员*/
    const val SUPER_ADMIN = "super_admin"

    /**顶级*/
    const val ROOT = "root"
}

/**转换成角色权限*/
fun List<RoleEntity>?.toAuthorities(): List<SimpleGrantedAuthority> {
    val list = mutableListOf<SimpleGrantedAuthority>()
    this?.forEach {
        if (!it.role.isNullOrBlank()) {
            list.add(SimpleGrantedAuthority(it.role))
        }
    }
    return list
}