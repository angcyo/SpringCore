package com.angcyo.spring.base.data

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/11/13
 */

data class ResultPage<T>(
        /**返回的元素数量*/
        var size: Long = -1,
        /**总共的页数*/
        var totalPages: Long = -1,
        /**元素总数量*/
        var totalSize: Long = 0,
        /**当前返回的数据*/
        var records: List<T>? = null
)