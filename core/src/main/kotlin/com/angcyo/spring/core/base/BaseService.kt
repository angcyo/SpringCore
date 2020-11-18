package com.angcyo.spring.core.base

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.transaction.annotation.Transactional
import javax.annotation.PostConstruct

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/11/17
 *
 * 通用服务
 */

abstract class BaseService<Entity, Repository : JpaRepository<Entity, Long>> {

    @PostConstruct
    fun init() {
    }

    /**删除*/
    fun delete(repository: Repository, id: Long? = null, ids: String? = null): Boolean {
        return when {
            id != null -> {
                deleteById(repository, id)
                true
            }
            ids != null -> {
                deleteByIds(repository, ids)
                true
            }
            else -> {
                false
            }
        }
    }

    /**根据[id], 删除一条记录*/
    @Transactional
    @Modifying
    open fun deleteById(repository: Repository, id: Long) {
        repository.deleteById(id)
    }

    /**根据一组[ids]删除所有记录.[1;2;3;4;]*/
    fun deleteByIds(repository: Repository, ids: String) {
        ids.split(";").forEach {
            try {
                deleteById(repository, it.toLong())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}