package com.angcyo.spring.mybatis.plus.auto.param

import io.swagger.annotations.ApiModelProperty

/**
 * 基础的默认查询参数
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/05/29
 */
abstract class BaseAutoPageParam : BaseAutoQueryParam() {

    //<editor-fold desc="page">

    /**
     * page方法, 才会生效
     * [com.angcyo.spring.mybatis.plus.auto.IBaseAutoMybatisService.page]*/
    @ApiModelProperty("请求第几页,从1开始")
    var pageIndex: Long = 1

    /**
     * page方法, 才会生效
     * [com.angcyo.spring.mybatis.plus.auto.IBaseAutoMybatisService.page]*/
    @ApiModelProperty("每页请求数据量")
    var pageSize: Long = 20

    //</editor-fold desc="page">
}