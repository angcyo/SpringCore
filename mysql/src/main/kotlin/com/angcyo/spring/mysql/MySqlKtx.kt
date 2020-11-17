package com.angcyo.spring.mysql

import com.angcyo.spring.base.data.Result
import com.angcyo.spring.base.data.ResultPage
import org.springframework.data.domain.Page
import kotlin.math.min

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/11/13
 */

fun <T> Page<T>.pageOk(msg: String? = "Success") = when {
    else -> Result(msg = msg,
            data = ResultPage(
                    size = min(this.size.toLong(), this.totalElements),
                    totalPages = this.totalPages.toLong(),
                    totalSize = this.totalElements,
                    records = this.toList()
            ))
}

fun <T> Collection<T>.pageOk(msg: String? = "Success") = when {
    else -> Result(msg = msg,
            data = ResultPage(
                    size = this.size.toLong(),
                    totalPages = 1,
                    totalSize = this.size.toLong(),
                    records = this.toList()
            ))
}


