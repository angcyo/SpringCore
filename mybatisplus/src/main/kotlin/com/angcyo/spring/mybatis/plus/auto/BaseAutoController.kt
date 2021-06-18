package com.angcyo.spring.mybatis.plus.auto

import com.angcyo.spring.base.data.Result
import com.angcyo.spring.base.data.ok
import com.angcyo.spring.base.data.result
import com.angcyo.spring.base.extension.apiError
import com.angcyo.spring.mybatis.plus.auto.param.BaseAutoPageParam
import com.angcyo.spring.mybatis.plus.auto.param.IAutoParam
import com.angcyo.spring.util.copyTo
import com.baomidou.mybatisplus.core.metadata.IPage
import com.baomidou.mybatisplus.core.toolkit.ReflectionKit
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import io.swagger.annotations.ApiOperation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody


/**
 * 针对[com.angcyo.spring.mybatis.plus.auto.IBaseAutoMybatisService]
 * 的自动控制器
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/06/08
 */
abstract class BaseAutoController<
        /**自动服务的提供类*/
        AutoService : IBaseAutoMybatisService<Table>,
        /**对应的表*/
        Table,
        /**保存时的参数类型*/
        SaveParam : IAutoParam,
        /**查询时的参数类型*/
        QueryParam : IAutoParam,
        /**数据返回的类型*/
        Return
        > {

    //<editor-fold desc="core">

    @Autowired
    lateinit var autoService: AutoService

    /**获取返回值类型*/
    open fun getReturnClass(): Class<*> = ReflectionKit.getSuperClassGenericType(
        this.javaClass,
        BaseAutoController::class.java,
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

        if (newAny is IAutoParam) {
            autoService.autoFill(newAny)
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
    open fun autoSaveBefore(param: SaveParam): Boolean {
        return false
    }

    open fun autoSaveAfter(param: SaveParam, table: Table) {
    }

    @ApiOperation("保存数据Auto")
    @PostMapping("/save.auto")
    open fun autoSave(@RequestBody(required = true) param: SaveParam): Result<Return> {
        autoService.autoFill(param)
        if (autoSaveBefore(param)) {
            return Result.ok()
        }
        autoService.autoCheck(param)
        val table = autoService.autoSave(param)
        autoSaveAfter(param, table)
        return table.toReturn().result()
    }

    //</editor-fold desc="save">

    //<editor-fold desc="delete">

    /**@return true 拦截后面的操作*/
    open fun autoDeleteBefore(param: SaveParam): Boolean {
        return false
    }

    open fun autoDeleteAfter(param: SaveParam, delete: Boolean) {
    }

    @ApiOperation("使用id软删除数据Auto")
    @PostMapping("/delete.auto")
    open fun autoDelete(@RequestBody(required = true) param: SaveParam): Result<Boolean> {
        autoService.autoFill(param)
        if (autoDeleteBefore(param)) {
            return Result.ok()
        }
        val delete = autoService.autoDelete(param)
        autoDeleteAfter(param, delete)
        return delete.result()
    }

    //</editor-fold desc="delete">

    //<editor-fold desc="remove">

    /**@return true 拦截后面的操作*/
    open fun autoRemoveBefore(param: SaveParam): Boolean {
        return false
    }

    open fun autoRemoveAfter(param: SaveParam, remove: Boolean) {
    }

    @ApiOperation("使用id移除数据(真删除)Auto")
    @PostMapping("/remove.auto")
    open fun autoRemove(@RequestBody(required = true) param: SaveParam): Result<Boolean> {
        autoService.autoFill(param)
        if (autoRemoveBefore(param)) {
            return Result.ok()
        }
        val remove = autoService.autoRemove(param)
        autoRemoveAfter(param, remove)
        return remove.result()
    }

    //</editor-fold desc="remove">

    //<editor-fold desc="update">

    /**@return true 拦截后面的操作*/
    open fun autoUpdateBefore(param: SaveParam): Boolean {
        return false
    }

    open fun autoUpdateAfter(param: SaveParam, update: Boolean) {
    }

    @ApiOperation("使用id更新数据Auto")
    @PostMapping("/update.auto")
    open fun autoUpdate(@RequestBody(required = true) param: SaveParam): Result<Boolean> {
        autoService.autoFill(param)
        if (autoUpdateBefore(param)) {
            return Result.ok()
        }
        val result = autoService.autoUpdateByKey(param)
        autoUpdateAfter(param, result)
        return result.result()
    }

    //</editor-fold desc="update">

    //<editor-fold desc="query">

    /**@return true 拦截后面的操作*/
    open fun autoQueryBefore(param: QueryParam): Boolean {
        return false
    }

    open fun autoQueryAfter(param: QueryParam, query: IPage<Table>) {
    }

    @ApiOperation("查询单条数据Auto")
    @PostMapping("/query.auto")
    open fun autoQuery(@RequestBody(required = true) param: QueryParam): Result<Return> {
        autoService.autoFill(param)
        if (autoQueryBefore(param)) {
            return Result.ok()
        }
        if (param !is BaseAutoPageParam) {
            apiError("参数类型不匹配")
        }
        param.pageIndex = 1
        param.pageSize = 1
        val page = autoService.autoPage(param)
        autoQueryAfter(param, page)
        val result = page.records.toReturnList()
        return result.firstOrNull().ok()
    }

    //</editor-fold desc="query">

    //<editor-fold desc="list">

    /**@return true 拦截后面的操作*/
    open fun autoListBefore(param: QueryParam): Boolean {
        return false
    }

    open fun autoListAfter(param: QueryParam, list: List<Table>) {
    }

    @ApiOperation("查询所有列表Auto")
    @PostMapping("/list.auto")
    open fun autoList(@RequestBody(required = true) param: QueryParam): Result<List<Return>> {
        autoService.autoFill(param)
        if (autoListBefore(param)) {
            return Result.ok()
        }
        val list = autoService.autoList(param)
        autoListAfter(param, list)
        return list.toReturnList().result()
    }

    //</editor-fold desc="list">

    //<editor-fold desc="page">

    /**@return true 拦截后面的操作*/
    open fun autoPageBefore(param: QueryParam): Boolean {
        return false
    }

    open fun autoPageAfter(param: QueryParam, query: IPage<Table>) {
    }

    @ApiOperation("分页查询列表Auto")
    @PostMapping("/page.auto")
    open fun autoPage(@RequestBody(required = true) param: QueryParam): Result<IPage<Return>> {
        autoService.autoFill(param)
        if (autoPageBefore(param)) {
            return Result.ok()
        }

        if (param !is BaseAutoPageParam) {
            apiError("参数类型不匹配")
        }

        val page = autoService.autoPage(param)
        autoPageAfter(param, page)

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