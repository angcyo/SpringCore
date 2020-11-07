package com.angcyo.spring.security.service

import com.angcyo.spring.security.entity.AuthEntity
import org.springframework.data.jpa.repository.JpaRepository

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/11/07
 */

interface RoleRepository :JpaRepository<AuthEntity, Long>