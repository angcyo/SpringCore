package com.angcyo.spring.core.http

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty

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