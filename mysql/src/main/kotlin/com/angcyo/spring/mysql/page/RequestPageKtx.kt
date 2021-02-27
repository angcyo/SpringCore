package com.angcyo.spring.mysql.page

import com.angcyo.spring.base.page.RequestPage
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import kotlin.math.max

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/02/27
 */


/**将请求body中的[RequestPage]转成jpa的[PageRequest]*/
fun RequestPage?.pageable(vararg sortProperties: String, desc: Boolean = true): Pageable? {
    var result: Pageable? = null

    val orderList = mutableListOf<Sort.Order>()
    orderList.addAll(sortProperties.toList().orderList(desc))

    if (this == null || this.requestSize < 0) {
        if (orderList.isNotEmpty()) {
            //查询所有, 并且需要排序
            result = PageRequest.of(0, Int.MAX_VALUE, Sort.by(orderList))
        }
    } else {
        orderList.addAll(orderList())
        result = PageRequest.of(max(0, this.requestPage - 1).toInt(), this.requestSize.toInt(), Sort.by(orderList))
    }
    return result
}

fun RequestPage?.pageable(): Pageable? {
    var result: Pageable? = null
    if (this == null || this.requestSize < 0) {
        //no op
    } else {
        val orderList = mutableListOf<Sort.Order>()
        orderList.addAll(orderList())
        result = PageRequest.of(max(0, this.requestPage - 1).toInt(), this.requestSize.toInt(), Sort.by(orderList))
    }
    return result
}

/**排序方式集合*/
fun RequestPage.orderList(): List<Sort.Order> {
    val result = mutableListOf<Sort.Order>()

    if (!desc.isNullOrEmpty()) {
        desc?.split(RequestPage.SPLIT)?.forEach {
            if (it.isNotBlank()) {
                result.add(Sort.Order.desc(it))
            }
        }
    }

    if (!asc.isNullOrEmpty()) {
        asc?.split(RequestPage.SPLIT)?.forEach {
            if (it.isNotBlank()) {
                result.add(Sort.Order.asc(it))
            }
        }
    }

    return result
}

fun Array<String>.orderList(desc: Boolean = true): List<Sort.Order> {
    return toList().orderList(desc)
}

fun Iterable<String>.orderList(desc: Boolean = true): List<Sort.Order> {
    val result = mutableListOf<Sort.Order>()

    this.forEach {
        if (it.isNotBlank()) {
            if (desc) {
                //降序排序, 从大->小
                result.add(Sort.Order.desc(it))
            } else {
                //升序排序, 从小->大
                result.add(Sort.Order.asc(it))
            }
        }
    }

    return result
}

/**排序[Pageable]*/
fun sortBy(vararg sortProperties: String, desc: Boolean = true): Pageable {
    val orderList = mutableListOf<Sort.Order>()
    orderList.addAll(sortProperties.toList().orderList(desc))
    return PageRequest.of(0, Int.MAX_VALUE, Sort.by(orderList))
}

fun requestSortBy(vararg sortProperties: String, desc: Boolean = true): RequestPage {
    return RequestPage().apply {
        requestPage = 0
        requestSize = Long.MAX_VALUE
        if (desc) {
            this.desc = sortProperties.joinToString(";")
        } else {
            this.asc = sortProperties.joinToString(";")
        }
    }
}