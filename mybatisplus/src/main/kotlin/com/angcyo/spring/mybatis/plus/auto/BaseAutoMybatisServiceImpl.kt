package com.angcyo.spring.mybatis.plus.auto

import com.angcyo.spring.mybatis.plus.auto.param.IAutoParam
import com.angcyo.spring.mybatis.plus.service.BaseMybatisServiceImpl
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/05/28
 */
open class BaseAutoMybatisServiceImpl<Mapper : BaseAutoMapper<Table>, Table> :
    BaseMybatisServiceImpl<Mapper, Table>(), IBaseAutoMybatisService<Table> {
    /**获取一个[QueryWrapper]*/
    fun queryWrapper(): QueryWrapper<Table> {
        return QueryWrapper<Table>()
    }

    /**获取数量*/
    fun count(param: IAutoParam): Int {
        return count(AutoParse.parse(queryWrapper(), param))
    }
}