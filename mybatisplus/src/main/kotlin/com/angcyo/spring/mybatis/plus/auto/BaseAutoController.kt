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

    //<editor-fold desc="泛型获取">

    open fun getQueryClass(): Class<*> = ReflectionKit.getSuperClassGenericType(
        this.javaClass,
        BaseAutoController::class.java,
        3
    ) as Class

    /**获取返回值类型*/
    open fun getReturnClass(): Class<*> = ReflectionKit.getSuperClassGenericType(
        this.javaClass,
        BaseAutoController::class.java,
        4
    ) as Class

    //</editor-fold desc="泛型获取">

    //<editor-fold desc="core">

    @Autowired
    lateinit var autoService: AutoService

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

    @ApiOperation("[通用]单表数据新增接口")
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

    open fun autoSaveAfter(param: SaveParam, table: Table) {
        //no op
    }

    //</editor-fold desc="save">

    //<editor-fold desc="delete">

    /**@return true 拦截后面的操作*/
    open fun autoDeleteBefore(param: QueryParam): Boolean {
        return false
    }

    @ApiOperation("[通用]单表数据id软删除接口")
    @PostMapping("/delete.auto")
    open fun autoDelete(@RequestBody(required = true) param: QueryParam): Result<Boolean> {
        autoService.autoFill(param)
        if (autoDeleteBefore(param)) {
            return Result.ok()
        }
        val delete = autoService.autoDelete(param)
        autoDeleteAfter(param, delete)
        return delete.result()
    }

    open fun autoDeleteAfter(param: QueryParam, delete: Boolean) {
        //no op
    }


    //</editor-fold desc="delete">

    //<editor-fold desc="remove">

    /**@return true 拦截后面的操作*/
    open fun autoRemoveBefore(param: QueryParam): Boolean {
        return false
    }

    @ApiOperation("[通用]单表数据id移除数据(真删除)接口", hidden = true)
    @PostMapping("/remove.auto")
    open fun autoRemove(@RequestBody(required = true) param: QueryParam): Result<Boolean> {
        autoService.autoFill(param)
        if (autoRemoveBefore(param)) {
            return Result.ok()
        }
        val remove = autoService.autoRemove(param)
        autoRemoveAfter(param, remove)
        return remove.result()
    }

    open fun autoRemoveAfter(param: QueryParam, remove: Boolean) {
        //no op
    }

    //</editor-fold desc="remove">

    //<editor-fold desc="update">

    /**@return true 拦截后面的操作*/
    open fun autoUpdateBefore(param: SaveParam): Boolean {
        return false
    }

    open fun autoUpdateAfter(param: SaveParam, update: List<Table>?) {
        //no op
    }

    @ApiOperation("[通用]单表根据数据id更新接口")
    @PostMapping("/update.auto")
    open fun autoUpdate(@RequestBody(required = true) param: SaveParam): Result<Return> {
        autoService.autoFill(param)
        if (autoUpdateBefore(param)) {
            return Result.ok()
        }
        val result = autoService.autoUpdateByKey(param)
        autoUpdateAfter(param, result)
        return result?.toReturnList()?.lastOrNull().result()
    }

    //</editor-fold desc="update">

    //<editor-fold desc="query">

    /**@return true 拦截后面的操作*/
    open fun autoQueryBefore(param: QueryParam): Boolean {
        return false
    }

    @ApiOperation("[通用]单表数据单条查询接口")
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

    open fun autoQueryAfter(param: QueryParam, query: IPage<Table>) {
        //no op
    }

    //</editor-fold desc="query">

    //<editor-fold desc="list">

    /**@return true 拦截后面的操作*/
    open fun autoListBefore(param: QueryParam): Boolean {
        return false
    }

    @ApiOperation("[通用]单表列表查询接口")
    @PostMapping("/list.auto")
    open fun autoList(@RequestBody(required = false) param: QueryParam?): Result<List<Return>> {
        val queryParam = param ?: getQueryClass().newInstance() as QueryParam
        autoService.autoFill(queryParam)
        if (autoListBefore(queryParam)) {
            return Result.ok()
        }
        val list = autoService.autoList(queryParam)
        autoListAfter(queryParam, list)
        return list.toReturnList().result()
    }

    open fun autoListAfter(param: QueryParam, list: List<Table>) {
        //no op
    }

    //</editor-fold desc="list">

    //<editor-fold desc="page">

    /**@return true 拦截后面的操作*/
    open fun autoPageBefore(param: QueryParam): Boolean {
        return false
    }

    @ApiOperation("[通用]单表数据分页查询接口")
    @PostMapping("/page.auto")
    open fun autoPage(@RequestBody(required = false) param: QueryParam?): Result<IPage<Return>> {
        val queryParam = param ?: getQueryClass().newInstance() as QueryParam
        autoService.autoFill(queryParam)
        if (autoPageBefore(queryParam)) {
            return Result.ok()
        }

        if (queryParam !is BaseAutoPageParam) {
            apiError("分页查询参数类型不匹配")
        }

        val page = autoService.autoPage(queryParam)
        autoPageAfter(queryParam, page)

        val result = page.records.toReturnList()
        val resultPage = result.toIPage(page)
        return resultPage.result()
    }

    open fun autoPageAfter(param: QueryParam, query: IPage<Table>) {
        //no op
    }

    //</editor-fold desc="page">
}