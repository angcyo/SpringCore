package com.angcyo.spring.mybatis.plus.auto

import com.angcyo.spring.mybatis.plus.auto.param.BaseAutoPageParam
import com.angcyo.spring.mybatis.plus.auto.param.IAutoParam
import com.angcyo.spring.mybatis.plus.service.IBaseMybatisService
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.baomidou.mybatisplus.core.metadata.IPage

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/05/28
 */
interface IBaseAutoMybatisService<Table> : IBaseMybatisService<Table> {

    /**构建一个解析器*/
    fun buildAutoParse() = AutoParse<Table>()

    /**获取一个[QueryWrapper]*/
    fun queryWrapper(): QueryWrapper<Table> {
        return QueryWrapper<Table>()
    }

    /**获取数量*/
    fun count(param: IAutoParam): Int {
        return count(buildAutoParse().parse(queryWrapper(), param))
    }

    /**根据[param], 自动查询出所有数据*/
    fun list(param: IAutoParam): List<Table> {
        return list(buildAutoParse().parse(queryWrapper(), param))
    }

    /**根据[param], 自动分页查询出数据*/
    fun page(param: BaseAutoPageParam): IPage<Table> {
        val autoParse = buildAutoParse()
        return page(autoParse.page(param), autoParse.parse(queryWrapper(), param))
    }

}