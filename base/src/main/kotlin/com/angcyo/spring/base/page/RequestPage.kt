package com.angcyo.spring.base.page

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/11/13
 */

@ApiModel("分页请求")
open class RequestPage {

    companion object {
        const val SPLIT = ";"
    }

    @ApiModelProperty("请求第几页,从1开始")
    var requestPage: Long = 1

    @ApiModelProperty("每页请求数据量")
    var requestSize: Long = 20

    @ApiModelProperty("需要降序排序字段(从大->小), 多个用;分割")
    var desc: String? = null

    @ApiModelProperty("需要升序排序字段(从小->大), 多个用;分割")
    var asc: String? = null

    init {
        //asc = "id" //按照id升序排列
    }
}