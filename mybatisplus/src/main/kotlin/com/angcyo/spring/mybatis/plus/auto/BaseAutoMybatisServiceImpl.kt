package com.angcyo.spring.mybatis.plus.auto

import com.angcyo.spring.mybatis.plus.service.BaseMybatisServiceImpl

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/05/28
 */
open class BaseAutoMybatisServiceImpl<Mapper : IBaseAutoMapper<Table>, Table> :
        BaseMybatisServiceImpl<Mapper, Table>(), IBaseAutoMybatisService<Table> {
}