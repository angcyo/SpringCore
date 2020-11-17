package com.angcyo.spring.core.base

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.jpa.repository.JpaRepository

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/11/17
 *
 * 通用服务
 */

abstract class BaseService<Entity, Repository : JpaRepository<Entity, Long>> {

    @Autowired
    lateinit var repository: Repository

    fun save(entity: Entity): Entity {
        return repository.save(entity)
    }

    /**删除*/
    fun delete(id: Long? = null, ids: String? = null): Boolean {
        return when {
            id != null -> {
                deleteById(id)
                true
            }
            ids != null -> {
                deleteByIds(ids)
                true
            }
            else -> {
                false
            }
        }
    }

    /**根据[id], 删除一条记录*/
    fun deleteById(id: Long) {
        repository.deleteById(id)
    }

    /**根据一组[ids]删除所有记录.[1;2;3;4;]*/
    fun deleteByIds(ids: String) {
        ids.split(";").forEach {
            try {
                deleteById(it.toLong())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}