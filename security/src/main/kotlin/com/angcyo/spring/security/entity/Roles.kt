package com.angcyo.spring.security.entity

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