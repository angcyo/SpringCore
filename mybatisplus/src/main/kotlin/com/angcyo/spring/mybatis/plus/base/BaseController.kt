package com.angcyo.spring.mybatis.plus.base

import com.angcyo.spring.base.data.Result
import com.angcyo.spring.base.data.result
import com.angcyo.spring.base.extension.apiError
import com.angcyo.spring.mybatis.plus.auto.param.BaseAutoPageParam
import com.angcyo.spring.mybatis.plus.service.IControllerService
import com.angcyo.spring.util.copyTo
import com.baomidou.mybatisplus.core.metadata.IPage
import com.baomidou.mybatisplus.core.toolkit.ReflectionKit
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import io.swagger.annotations.ApiOperation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/06/18
 */
abstract class BaseController<
        BaseService : IControllerService<Table, SaveParam, QueryParam>,
        /**对应的表*/
        Table,
        /**保存时的参数类型*/
        SaveParam,
        /**查询时的参数类型*/
        QueryParam,
        /**数据返回的类型*/
        Return
        > {

    //<editor-fold desc="core">

    @Autowired
    lateinit var service: BaseService

    /**获取返回值类型*/
    open fun getReturnClass(): Class<*> = ReflectionKit.getSuperClassGenericType(
        this.javaClass,
        BaseController::class.java,
        4
    ) as Class

    fun Table.toReturn(): Return {
        val any = this as Any
        val returnClass = getReturnClass()

        val newAny = if (returnClass.isAssignableFrom(any.javaClass)) {
            any as Return
        } else {
            val newAny = returnClass.newInstance()
            any.copyTo(newAny as Any)
            newAny as Return
        }

        return newAny
    }

    /**数据结构转换*/
    fun List<Table>.toReturnList(): List<Return> {
        val result = mutableListOf<Return>()
        forEach {
            result.add(it.toReturn())
        }
        return result
    }

    //</editor-fold desc="core">

    //<editor-fold desc="save">

    /**@return true 拦截后面的操作*/
    open fun saveTableBefore(param: SaveParam): Boolean {
        return false
    }

    open fun saveTableAfter(param: SaveParam, table: Table) {
    }

    @ApiOperation("保存数据Base")
    @PostMapping("/save.base")
    open fun saveTable(@RequestBody(required = true) param: SaveParam): Result<Return> {
        if (saveTableBefore(param)) {
            return Result.ok()
        }
        val table = service.saveTable(param)
        saveTableAfter(param, table)
        return table.toReturn().result()
    }

    //</editor-fold desc="save">

    //<editor-fold desc="delete">

    /**@return true 拦截后面的操作*/
    open fun deleteTableBefore(param: SaveParam): Boolean {
        return false
    }

    open fun deleteTableAfter(param: SaveParam, delete: Boolean) {
    }

    @ApiOperation("使用id软删除数据Base")
    @PostMapping("/delete.base")
    open fun deleteTable(@RequestBody(required = true) param: SaveParam): Result<Boolean> {
        if (deleteTableBefore(param)) {
            return Result.ok()
        }
        val delete = service.deleteTable(param)
        deleteTableAfter(param, delete)
        return delete.result()
    }

    //</editor-fold desc="delete">

    //<editor-fold desc="remove">

    /**@return true 拦截后面的操作*/
    open fun removeTableBefore(param: SaveParam): Boolean {
        return false
    }

    open fun removeTableAfter(param: SaveParam, remove: Boolean) {
    }

    @ApiOperation("使用id移除数据(真删除)Base")
    @PostMapping("/remove.base")
    open fun removeTable(@RequestBody(required = true) param: SaveParam): Result<Boolean> {
        if (removeTableBefore(param)) {
            return Result.ok()
        }
        val remove = service.removeTable(param)
        removeTableAfter(param, remove)
        return remove.result()
    }

    //</editor-fold desc="remove">

    //<editor-fold desc="update">

    /**@return true 拦截后面的操作*/
    open fun updateTableBefore(param: SaveParam): Boolean {
        return false
    }

    open fun updateTableAfter(param: SaveParam, update: Boolean) {
    }

    @ApiOperation("使用id更新数据Base")
    @PostMapping("/update.base")
    open fun updateTable(@RequestBody(required = true) param: SaveParam): Result<Boolean> {
        if (updateTableBefore(param)) {
            return Result.ok()
        }
        val result = service.updateTable(param)
        updateTableAfter(param, result)
        return result.result()
    }

    //</editor-fold desc="update">

    //<editor-fold desc="query">

    /**@return true 拦截后面的操作*/
    open fun queryTableBefore(param: QueryParam): Boolean {
        return false
    }

    open fun queryTableAfter(param: QueryParam, query: List<Table>) {
    }

    @ApiOperation("查询单条数据Base")
    @PostMapping("/query.base")
    open fun queryTable(@RequestBody(required = true) param: QueryParam): Result<List<Return>> {
        if (queryTableBefore(param)) {
            return Result.ok()
        }
        val list = service.queryTable(param)
        queryTableAfter(param, list)
        val result = list.toReturnList()
        return result.result()
    }

    //</editor-fold desc="query">

    //<editor-fold desc="list">

    /**@return true 拦截后面的操作*/
    open fun listTableBefore(): Boolean {
        return false
    }

    open fun listTableAfter(list: List<Table>) {
    }

    @ApiOperation("查询所有列表Base")
    @PostMapping("/list.base")
    open fun listTable(): Result<List<Return>> {
        if (listTableBefore()) {
            return Result.ok()
        }
        val list = service.listTable()
        listTableAfter(list)
        return list.toReturnList().result()
    }

    //</editor-fold desc="list">

    //<editor-fold desc="page">

    /**@return true 拦截后面的操作*/
    open fun pageTableBefore(param: QueryParam): Boolean {
        return false
    }

    open fun pageTableAfter(param: QueryParam, query: IPage<Table>) {
    }

    @ApiOperation("分页查询列表Base")
    @PostMapping("/page.base")
    open fun pageTable(@RequestBody(required = true) param: QueryParam): Result<IPage<Return>> {
        if (pageTableBefore(param)) {
            return Result.ok()
        }

        if (param !is BaseAutoPageParam) {
            apiError("参数类型不匹配")
        }

        val page = service.pageTable(param.pageIndex, param.pageSize, param)
        pageTableAfter(param, page)

        val result = page.records.toReturnList()

        val resultPage = Page<Return>()
        resultPage.records = result //数据记录
        resultPage.total = page.total //总数量
        resultPage.size = page.size //每页请求数量
        resultPage.current = page.current //当前页
        resultPage.pages = page.pages //总页数

        return resultPage.result()
    }

    //</editor-fold desc="page">

}