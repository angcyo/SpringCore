package com.angcyo.spring.mybatis.plus.service

import com.angcyo.spring.mybatis.plus.columnName
import com.angcyo.spring.mybatis.plus.table.BaseAuditTable

/**
 * 连表查询参数构建
 * [com.angcyo.spring.mybatis.plus.service.IBaseMybatisService.selectFrom]
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/06/08
 */
class SelectFrom {

    /**从什么表, 主动转换成表名*/
    var fromTable: String? = null

    /**获取什么列, 主动转换成表列名*/
    var fromColumn: String? = null

    /**获取条件, 有注入风险. 只在最内的查询时支持全条件有效, 其他都是查询列 in 的判断*/
    var fromWhere: String? = null

    /**递归*/
    var from: SelectFrom? = null

    /**当前表需要查询的列, 默认是id
     * 决定当前语句, 是否需要使用 in 包含*/
    var column: String = BaseAuditTable::id.columnName()
}