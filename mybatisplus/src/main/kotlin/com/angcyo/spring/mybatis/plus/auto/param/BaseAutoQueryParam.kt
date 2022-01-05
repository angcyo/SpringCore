package com.angcyo.spring.mybatis.plus.auto.param

import io.swagger.annotations.ApiModelProperty

/**
 * 两个特殊字段 or 和 and
 * [com.angcyo.spring.mybatis.plus.auto.core.AutoParse._handleQuery]
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/05/29
 */
abstract class BaseAutoQueryParam : BaseAutoParam() {

    companion object {
        const val SPLIT = ";"
    }

    //<editor-fold desc="order">

    /**查询时, 就会生效
     * [com.angcyo.spring.mybatis.plus.auto.core.AutoParse._handleOrder]*/
    @ApiModelProperty("[通用查询参数]需要降序排序字段(从大->小), 多个用,分割.(查询接口时使用)", hidden = true)
    var desc: String? = null

    /**查询时, 就会生效
     * [com.angcyo.spring.mybatis.plus.auto.core.AutoParse._handleOrder]*/
    @ApiModelProperty("[通用查询参数]需要升序排序字段(从小->大), 多个用,分割.(查询接口时使用)", hidden = true)
    var asc: String? = null

    //</editor-fold desc="order">

    init {
        asc = "id" //默认按照id升序排列
    }
}