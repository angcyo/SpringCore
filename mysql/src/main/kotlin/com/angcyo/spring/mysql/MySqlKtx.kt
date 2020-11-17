package com.angcyo.spring.mysql

import com.angcyo.spring.base.data.Result
import org.springframework.data.domain.Page

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/11/13
 */

fun <T> Page<T>.ok(msg: String? = "Success") = when {
    else -> Result(msg = msg,
            data = com.angcyo.spring.base.data.ResultPage(
                    size = this.size.toLong(),
                    totalPages = this.totalPages.toLong(),
                    totalSize = this.totalElements,
                    records = this.toList()
            ))
}
