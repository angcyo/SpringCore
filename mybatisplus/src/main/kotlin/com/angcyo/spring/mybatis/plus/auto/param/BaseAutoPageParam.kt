package com.angcyo.spring.mybatis.plus.auto.param

import io.swagger.annotations.ApiModelProperty

/**
 * 基础的默认查询参数
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/05/29
 */
class BaseAutoPageParam : BaseAutoQueryParam() {

    companion object {

        /**分页请求时, 默认每页请求的数量*/
        const val PAGE_SIZE = 20L
    }

    //<editor-fold desc="page">

    /**
     * page方法, 才会生效
     * [com.angcyo.spring.mybatis.plus.auto.IBaseAutoMybatisService.autoPage]*/
    @ApiModelProperty("[通用分页参数]请求第几页,从1开始.(分页接口时有效)")
    var pageIndex: Long = 1

    /**
     * page方法, 才会生效
     * [com.angcyo.spring.mybatis.plus.auto.IBaseAutoMybatisService.autoPage]*/
    @ApiModelProperty("[通用分页参数]每页请求数据量,默认20.(分页接口时有效)")
    var pageSize: Long = PAGE_SIZE

    /**
     * page方法, 才会生效
     * [com.angcyo.spring.mybatis.plus.auto.IBaseAutoMybatisService.autoPage]*/
    @ApiModelProperty("[通用分页参数]分页请求之前,是否先查询数量.(分页接口时有效)", hidden = true)
    var searchCount: Boolean = true

    //</editor-fold desc="page">
}