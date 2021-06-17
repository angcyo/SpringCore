package com.angcyo.spring.mybatis.plus.tree

import com.angcyo.spring.mybatis.plus.tree.IBaseTree.Companion.TOP_ID

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/06/16
 */
interface IBaseTree {
    companion object {
        const val TOP_ID = -1L
        const val PARENT_SPLIT = ","
    }

    /**父id, 顶级用[TOP_ID]
     * [1]
     * [10]*/
    var parentId: Long?

    /**父ids, 用[PARENT_SPLIT]号
     * [,1,3,5,]*/
    var parentIds: String?
}

fun Long?.isTopId() = this == TOP_ID