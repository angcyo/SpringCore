package com.angcyo.spring.mybatis.plus.auto.core

import com.angcyo.spring.mybatis.plus.auto.annotation.Query
import java.lang.reflect.Field

/**
 *
 * 查询组, 以及一组中的字段
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/12/23
 */

class QueryGroup {

    /**需要操作的对象*/
    var obj: Any? = null

    /**组内每个条件 是否需要用 or 包裹
     * 默认是 and */
    var or: Boolean = false

    /**当查询条件为空时, 是否需要跳过语句拼接*/
    var jumpEmpty: Boolean = false

    /**需要组装的查询字段集合 */
    var queryFieldList: List<QueryField>? = null

    /**子分组集合 */
    var childQueryGroupList: List<QueryGroup>? = null

    /**查询是否是空的*/
    fun isQueryEmpty(): Boolean {
        return queryFieldList.isNullOrEmpty() && childQueryGroupList.isNullOrEmpty()
    }
}

/**查询的被拾取的字段*/
data class QueryField(val field: Field, val query: Query)