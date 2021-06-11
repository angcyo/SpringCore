package com.angcyo.spring.mybatis.plus.auto

import com.baomidou.mybatisplus.core.mapper.BaseMapper
import org.apache.ibatis.annotations.Param
import org.apache.ibatis.annotations.Select

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/05/28
 */

interface IBaseAutoMapper<Table> : BaseMapper<Table> {

    /**直接执行sql语句, 有注入风险
     * [org.apache.ibatis.executor.resultset.DefaultResultSetHandler.getRowValue]*/
    @Select("\${sql}")
    fun sqlMaps(@Param("sql") sql: String): List<Map<String, Any>>

}