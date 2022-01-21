package com.angcyo.spring.mybatis.plus.service

import com.angcyo.spring.mybatis.plus.auto.param.BaseAutoPageParam
import com.angcyo.spring.mybatis.plus.auto.sort
import com.baomidou.mybatisplus.core.metadata.IPage

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/06/18
 */

@Deprecated("已废弃", ReplaceWith("com.angcyo.spring.mybatis.plus.auto.IBaseAutoMybatisService"))
interface IControllerService<
        Table,
        /**保存时的参数类型*/
        SaveParam,
        /**查询时的参数类型*/
        QueryParam> : IBaseMybatisService<Table> {

    //<editor-fold desc="save">

    fun saveTable(param: SaveParam): Table

    //</editor-fold desc="save">

    //<editor-fold desc="delete">

    fun deleteTable(param: SaveParam): Boolean {
        return false
    }

    //</editor-fold desc="delete">

    //<editor-fold desc="remove">

    fun removeTable(param: SaveParam): Boolean {
        return false
    }

    //</editor-fold desc="remove">

    //<editor-fold desc="update">

    fun updateTable(param: SaveParam): Boolean {
        return false
    }

    //</editor-fold desc="update">

    //<editor-fold desc="query">

    fun queryTable(param: QueryParam): List<Table> {
        return emptyList()
    }

    //</editor-fold desc="query">

    //<editor-fold desc="list">

    fun listTable(): List<Table> {
        return listQuery {
        }
    }

    //</editor-fold desc="list">

    //<editor-fold desc="page">

    fun pageTable(
        pageIndex: Long = 1,
        pageSize: Long = BaseAutoPageParam.PAGE_SIZE,
        searchCount: Boolean = true,
        param: QueryParam
    ): IPage<Table> {
        return pageQuery(pageIndex, pageSize, searchCount) {
            sort(param)
        }
    }

    //</editor-fold desc="page">


}