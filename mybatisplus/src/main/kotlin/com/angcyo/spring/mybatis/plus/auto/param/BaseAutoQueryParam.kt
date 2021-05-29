package com.angcyo.spring.mybatis.plus.auto.param

import com.angcyo.spring.mybatis.plus.auto.annotation.AutoWhere
import io.swagger.annotations.ApiModelProperty

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/05/29
 */
abstract class BaseAutoQueryParam : IAutoParam {

    companion object {
        const val SPLIT = ";"
    }

    @AutoWhere
    @ApiModelProperty("通过id查询记录")
    var id: Long? = null

    //<editor-fold desc="order">

    /**查询时, 就会生效
     * [com.angcyo.spring.mybatis.plus.auto.AutoParse._handleOrder]*/
    @ApiModelProperty("需要降序排序字段(从大->小), 多个用;分割")
    var desc: String? = null

    /**查询时, 就会生效
     * [com.angcyo.spring.mybatis.plus.auto.AutoParse._handleOrder]*/
    @ApiModelProperty("需要升序排序字段(从小->大), 多个用;分割")
    var asc: String? = null

    //</editor-fold desc="order">

    init {
        //asc = "id" //按照id升序排列
    }
}