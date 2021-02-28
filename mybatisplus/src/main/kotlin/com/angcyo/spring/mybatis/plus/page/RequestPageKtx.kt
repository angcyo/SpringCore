package com.angcyo.spring.mybatis.plus.page

import com.angcyo.spring.base.page.RequestPage
import com.angcyo.spring.base.util.queryColumn
import com.baomidou.mybatisplus.core.conditions.interfaces.Func
import com.baomidou.mybatisplus.extension.plugins.pagination.Page

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/02/27
 */

/**将[RequestPage]参数, 转换成mybatisplus分页参数*/
fun <Table> RequestPage.page(): Page<Table> {
    val page = Page<Table>(requestPageIndex, requestPageSize)
    //page.countId = ""
    page.maxLimit = requestPageSize
    return page
}

/**排序*/
fun Func<*, String>.order(page: RequestPage) {
    val desc = page.desc
    if (!desc.isNullOrEmpty()) {
        //降序
        orderByDesc(*desc.split(RequestPage.SPLIT).map { it.queryColumn() }.toTypedArray())
    }

    val asc = page.asc
    if (!asc.isNullOrEmpty()) {
        //升序
        orderByAsc(*asc.split(RequestPage.SPLIT).map { it.queryColumn() }.toTypedArray())
    }
}