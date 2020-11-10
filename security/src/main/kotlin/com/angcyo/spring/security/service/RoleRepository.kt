package com.angcyo.spring.security.service

import com.angcyo.spring.security.entity.RoleEntity
import org.springframework.data.jpa.repository.JpaRepository

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/11/07
 */

interface RoleRepository : JpaRepository<RoleEntity, Long> {
    fun findAllByAuthId(authId: Long): List<RoleEntity>
}