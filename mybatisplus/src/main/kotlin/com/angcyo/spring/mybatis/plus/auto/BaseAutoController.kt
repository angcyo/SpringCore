package com.angcyo.spring.mybatis.plus.auto

import com.angcyo.spring.base.data.Result
import com.angcyo.spring.base.data.result
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
abstract class BaseAutoController<AutoService : IBaseAutoMybatisService<Table>, Table, QueryParam : BaseAutoPageParam, Return> {

    @Autowired
    lateinit var autoService: AutoService

    /**获取返回值类型*/
    open fun getReturnClass(): Class<*> = ReflectionKit.getSuperClassGenericType(
        this.javaClass,
        BaseAutoController::class.java, 3
    ) as Class

    /**数据结构转换*/
    fun List<Table>.toReturnList(): List<Return> {
        val result = mutableListOf<Return>()
        forEach {
            val any = it as Any
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

            result.add(newAny)
        }
        return result
    }

    @ApiOperation("查询所有列表")
    @PostMapping("/list.auto")
    open fun autoList(@RequestBody(required = true) param: QueryParam): Result<List<Return>> {
        val list = autoService.autoList(param)
        return list.toReturnList().result()
    }

    @ApiOperation("分页查询列表")
    @PostMapping("/page.auto")
    open fun autoPage(@RequestBody(required = true) param: QueryParam): Result<IPage<Return>> {
        val page = autoService.autoPage(param)
        val result = page.records.toReturnList()

        val resultPage = Page<Return>()
        resultPage.records = result //数据记录
        resultPage.total = page.total //总数量
        resultPage.size = page.size //每页请求数量
        resultPage.current = page.current //当前页
        resultPage.pages = page.pages //总页数

        return resultPage.result()
    }

    @ApiOperation("使用id更新数据")
    @PostMapping("/update.auto")
    open fun autoUpdate(@RequestBody(required = true) param: QueryParam): Result<Boolean> {
        val result = autoService.autoUpdateByKey(param)
        return result.result()
    }
}