package com.angcyo.spring.core.http

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import kotlin.math.max

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/11/13
 */

@ApiModel("分页请求")
open class RequestPage {
    @ApiModelProperty("请求第几页,从1开始")
    var requestPage: Int = 1

    @ApiModelProperty("每页请求数据量")
    var requestSize: Int = 10
}

/**将请求body中的[RequestPage]转成jpa的[PageRequest]*/
fun RequestPage?.pageable(vararg sortProperties: String, desc: Boolean = true): Pageable? {
    var result: Pageable? = null
    var sort: Sort? = null

    if (!sortProperties.isNullOrEmpty()) {
        sort = Sort.by(*sortProperties).run {
            if (desc) {
                //降序排序, 从大->小
                descending()
            } else {
                //升序排序, 从小->大
                ascending()
            }
        }
    }

    if (this == null || this.requestSize < 0) {
        if (sort != null) {
            //查询所有, 并且需要排序
            result = PageRequest.of(0, Int.MAX_VALUE, sort)
        }
    } else {
        result = PageRequest.of(max(0, this.requestPage - 1), this.requestSize, sort ?: Sort.unsorted())
    }
    return result
}

/**排序[Pageable]*/
fun sortBy(vararg sortProperties: String, desc: Boolean = true): Pageable? {
    var sort: Sort = Sort.unsorted()

    if (!sortProperties.isNullOrEmpty()) {
        //降序
        sort = Sort.by(*sortProperties).run {
            if (desc) {
                //降序排序, 从大->小
                descending()
            } else {
                //升序排序, 从小->大
                ascending()
            }
        }
    }

    return PageRequest.of(0, Int.MAX_VALUE, sort)
}