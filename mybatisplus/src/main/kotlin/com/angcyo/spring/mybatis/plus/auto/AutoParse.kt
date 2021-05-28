package com.angcyo.spring.mybatis.plus.auto

import com.angcyo.spring.mybatis.plus.auto.param.IAutoParam
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.baomidou.mybatisplus.core.toolkit.ReflectionKit

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/05/28
 */
object AutoParse {

    /**根据[param]声明的约束, 自动赋值给[queryWrapper]
     * [org.springframework.beans.BeanUtils.getPropertyDescriptors]
     * */
    fun <Table> parse(queryWrapper: QueryWrapper<Table>, param: IAutoParam): QueryWrapper<Table> {
        ReflectionKit.getFieldMap(param.javaClass).forEach { entry ->
            val name = entry.key
            val field = entry.value

            println("$name $field")
        }
        return queryWrapper
    }

}