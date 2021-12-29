package com.angcyo.spring.mybatis.plus.auto

import com.angcyo.spring.mybatis.plus.auto.param.IAutoParam
import com.angcyo.spring.mybatis.plus.service.BaseMybatisServiceImpl
import com.angcyo.spring.mybatis.plus.toLowerCamel
import com.baomidou.mybatisplus.core.toolkit.BeanUtils


/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/05/28
 */
open class BaseAutoMybatisServiceImpl<Mapper : IBaseAutoMapper<Table>, Table> :
    BaseMybatisServiceImpl<Mapper, Table>(), IBaseAutoMybatisService<Table> {

    /**同一对象, 只填充一次*/
    var _fillHashCode: Int? = null

    override fun <T : IAutoParam> autoFill(param: T): T {
        val hashCode = param.hashCode()
        if (_fillHashCode == hashCode) {
            return param
        }
        val result = super.autoFill(param)
        _fillHashCode = hashCode
        return result
    }

    /**[com.angcyo.spring.mybatis.plus.auto.IBaseAutoMapper.sqlMaps]*/
    fun sqlMaps(sql: String): List<Map<String, Any>> {
        val result = mutableListOf<Map<String, Any>>()
        for (itemMap in getBaseMapper().sqlMaps(sql)) {
            val map = HashMap<String, Any>()
            itemMap.forEach {
                map[it.key.toLowerCamel()] = it.value
            }
            result.add(map)
        }
        return result
    }

    fun sqlMap(sql: String): Map<String, Any>? {
        val list = getBaseMapper().sqlMaps(sql)
        return list.firstOrNull()
    }

    inline fun <reified T> sqlList(sql: String): List<T> {
        val clz = T::class.java
        val sqlMaps = sqlMaps(sql)
        return BeanUtils.mapsToBeans(sqlMaps, clz)
    }

    inline fun <reified T> sqlOne(sql: String): T? {
        return sqlList<T>(sql).firstOrNull()
    }
}