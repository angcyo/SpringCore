package com.angcyo.spring.mybatis.plus.auto

import com.angcyo.spring.base.beanOf
import com.angcyo.spring.mybatis.plus.auto.annotation.*
import com.angcyo.spring.mybatis.plus.auto.param.BaseAutoPageParam
import com.angcyo.spring.mybatis.plus.auto.param.BaseAutoQueryParam
import com.angcyo.spring.mybatis.plus.auto.param.IAutoParam
import com.angcyo.spring.mybatis.plus.auto.param.PlaceholderAutoParam
import com.angcyo.spring.mybatis.plus.toLowerName
import com.angcyo.spring.mybatis.plus.toSafeSql
import com.angcyo.spring.util.L
import com.baomidou.mybatisplus.core.conditions.AbstractWrapper
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper
import com.baomidou.mybatisplus.core.toolkit.ReflectionKit
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import java.lang.reflect.Field

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/05/28
 */
class AutoParse<Table> {

    /**请求页码*/
    fun page(param: BaseAutoPageParam): Page<Table> {
        val page = Page<Table>(param.pageIndex, param.pageSize)
        page.maxLimit = param.pageSize
        return page
    }

    /**根据[param]声明的约束, 自动赋值给[queryWrapper]
     * [org.springframework.beans.BeanUtils.getPropertyDescriptors]
     * */
    fun parseQuery(queryWrapper: QueryWrapper<Table>, param: IAutoParam): QueryWrapper<Table> {
        //选择列
        _handleSelector(queryWrapper, param)

        //查询
        _handleQuery(queryWrapper, param)

        //排序
        _handleOrder(queryWrapper, param)

        val targetSql = queryWrapper.targetSql
        L.i("sql->$targetSql")
        return queryWrapper
    }

    /**根据[param]声明的约束, 自动赋值给[updateWrapper] */
    fun parseUpdate(updateWrapper: UpdateWrapper<Table>, param: IAutoParam): UpdateWrapper<Table> {
        _handleQuery(updateWrapper, param) {
            it.annotation<AutoUpdateBy>() == null
        }
        return updateWrapper
    }

    /**自动解析并填充对象
     * [com.angcyo.spring.mybatis.plus.auto.annotation.AutoFill]
     * @return 是否解析成功, 没有出现错误*/
    fun parseFill(param: IAutoParam): Boolean {
        var haveError = false
        param.eachAnnotation<AutoFill> { field ->
            if (!_handleFill(this, field, param)) {
                haveError = true
            }
        }
        return !haveError
    }

    fun _handleFill(fill: AutoFill, field: Field, obj: IAutoParam): Boolean {

        //反射获取对应服务
        var service: Any? = null
        if (fill.service !is PlaceholderAutoMybatisService) {
            service = beanOf(fill.service.java)
        } else if (fill.serviceName.isNotEmpty()) {
            service = beanOf(fill.serviceName)
        }

        //通过服务传递参数查询数据
        if (service != null && service is IBaseAutoMybatisService<*>) {

            val queryColumn = fill.queryColumn
            val queryValueKey = fill.queryValueField
            val result = if (queryColumn.isEmpty()) {
                val queryParamKey = fill.queryParamField.ifEmpty {
                    "${field.name}Query"
                }
                when (val queryParam = obj.getMember(queryParamKey)) {
                    is IAutoParam -> service.autoList(queryParam)
                    else -> service.autoList(PlaceholderAutoParam())
                }
            } else {
                val queryValue = obj.getMember(queryValueKey.ifEmpty { queryColumn })
                if (queryValue == null) {
                    emptyList()
                } else {
                    service.listOf(hashMapOf(queryColumn to queryValue))
                }
            }

            val targetResult = mutableListOf<Any?>()

            //需要获取到的数据字段
            if (fill.targetField.isEmpty()) {
                //为空表示需要获取整个对象
                targetResult.addAll(result)
            } else {
                for (item in result) {
                    targetResult.add(item.getMember(fill.targetField))
                }
            }

            //对数据进行赋值处理
            if (field.isList()) {
                field.set(obj, targetResult)

                if (targetResult.isNullOrEmpty() && fill.errorOnNull) {
                    return false
                }
            } else {
                val first = targetResult.firstOrNull()
                field.set(obj, first)
                if (first == null && fill.errorOnNull) {
                    return false
                }
            }
        }
        return true
    }

    /**处理需要指定选择返回的列*/
    fun _handleSelector(queryWrapper: QueryWrapper<Table>, param: IAutoParam) {
        val columnList = mutableListOf<String>()

        param.eachAnnotation<AutoColumns> { field ->
            val fieldValue = field.get(param)
            if (fieldValue is String) {
                fieldValue.split(BaseAutoQueryParam.SPLIT).mapTo(columnList) { it.toLowerName() }
            }
        }

        queryWrapper.select(*columnList.toTypedArray())
    }

    /**处理查询语句*/
    fun <Wrapper : AbstractWrapper<Table, String, Wrapper>> _handleQuery(
        wrapper: AbstractWrapper<Table, String, Wrapper>,
        param: IAutoParam,
        jumpField: (field: Field) -> Boolean = { false }
    ) {
        val autoWhereFieldList = mutableListOf<Field>()

        var and: Any? = null
        var andList: List<Any?>? = null

        var or: Any? = null
        var orList: List<Any?>? = null

        ReflectionKit.getFieldList(param.javaClass).forEach { field ->
            field.annotation<AutoWhere> {
                if (jumpField.invoke(field)) {
                    //jump
                } else {
                    val fieldValue = field.get(param)

                    if (fieldValue == null) {
                        //空值处理
                        if (value == WhereEnum.isNull ||
                            value == WhereEnum.isNotNull
                        ) {
                            autoWhereFieldList.add(field)
                        }
                    } else {
                        if (field.name == "and") {
                            //特殊字段, and, 等价于 WhereEnum.and
                            if (fieldValue.isList()) {
                                andList = fieldValue as List<Any?>?
                            } else {
                                and = fieldValue
                            }
                        } else if (field.name == "or") {
                            //特殊字段, or, 等价于 WhereEnum.or
                            if (fieldValue.isList()) {
                                orList = fieldValue as List<Any?>?
                            } else {
                                or = fieldValue
                            }
                        } else {
                            autoWhereFieldList.add(field)
                        }
                    }
                }
            }
        }

        //默认字段的 条件处理
        if (autoWhereFieldList.isNotEmpty()) {
            val autoWhere = param.javaClass.annotation<AutoWhere>()
            if (autoWhere == null || autoWhere.value == WhereEnum.and) {
                //默认所有字段 and 条件处理
                wrapper.and { w ->
                    autoWhereFieldList.forEach { field ->
                        _handleWhere(w, field, param)
                    }
                }
            }
        }

        //and条件
        if (and != null && and is IAutoParam) {
            wrapper.and { w ->
                _handleQuery(w, and as IAutoParam)
            }
        }

        //and list条件
        if (!andList.isNullOrEmpty()) {
            andList?.forEach { _and ->
                if (_and != null && _and is IAutoParam) {
                    wrapper.and { wrapper ->
                        _handleQuery(wrapper, _and)
                    }
                }
            }
        }

        //or条件
        if (or != null && or is IAutoParam) {
            wrapper.or { w ->
                _handleQuery(w, or as IAutoParam)
            }
        }

        //or list 条件
        if (!orList.isNullOrEmpty()) {
            orList?.forEach { _or ->
                if (_or != null && _or is IAutoParam) {
                    wrapper.or { wrapper ->
                        _handleQuery(wrapper, _or)
                    }
                }
            }
        }
    }

    fun <Wrapper : AbstractWrapper<Table, String, Wrapper>> _handleWhere(
        wrapper: AbstractWrapper<Table, String, Wrapper>,
        field: Field,
        obj: Any
    ) {
        field.annotation<AutoWhere> {
            val column = field.name.toLowerName()
            val fieldValue = field.get(obj)
            _handleWhere(wrapper, column, value, fieldValue)
        }
    }

    /**处理Where表达式*/
    fun <Wrapper : AbstractWrapper<Table, String, Wrapper>> _handleWhere(
        wrapper: AbstractWrapper<Table, String, Wrapper>,
        column: String,
        where: WhereEnum,
        value: Any?
    ) {
        when (where) {
            WhereEnum.eq -> wrapper.eq(column, value)
            WhereEnum.ne -> wrapper.ne(column, value)
            WhereEnum.gt -> wrapper.gt(column, value)
            WhereEnum.ge -> wrapper.ge(column, value)
            WhereEnum.lt -> wrapper.lt(column, value)
            WhereEnum.le -> wrapper.le(column, value)
            WhereEnum.like -> wrapper.like(column, value)
            WhereEnum.notLike -> wrapper.notLike(column, value)
            WhereEnum.likeLeft -> wrapper.likeLeft(column, value)
            WhereEnum.likeRight -> wrapper.likeRight(column, value)
            WhereEnum.isNull -> wrapper.isNull(column)
            WhereEnum.isNotNull -> wrapper.isNotNull(column)
            WhereEnum.inSql -> wrapper.inSql(column, value?.toString()?.toSafeSql())
            WhereEnum.notInSql -> wrapper.notInSql(column, value?.toString()?.toSafeSql())
            WhereEnum.exists -> wrapper.exists(column, value?.toString()?.toSafeSql())
            WhereEnum.notExists -> wrapper.notExists(column, value?.toString()?.toSafeSql())
            WhereEnum.last -> wrapper.last(value?.toString()?.toSafeSql())
            WhereEnum.apply -> wrapper.apply(value?.toString()?.toSafeSql())
            else -> {
                val valueClass = value?.javaClass
                if (valueClass?.isList() == true) {
                    val valueList = value as List<*>
                    when (where) {
                        WhereEnum.between -> wrapper.between(
                            column,
                            valueList.getOrNull(0),
                            valueList.getOrNull(1)
                        )
                        WhereEnum.notBetween -> wrapper.notBetween(
                            column,
                            valueList.getOrNull(0),
                            valueList.getOrNull(1)
                        )
                        WhereEnum.groupBy -> {
                            val stringList = valueList as List<String>
                            wrapper.groupBy(stringList.first().toString().toSafeSql(), *stringList.toTypedArray())
                        }
                        WhereEnum.`in` -> wrapper.`in`(column, valueList)
                        WhereEnum.notIn -> wrapper.notIn(column, valueList)
                    }
                } else {
                    when (where) {
                        WhereEnum.groupBy -> {
                            wrapper.groupBy(value.toString().toSafeSql())
                        }
                    }
                }
            }
        }
    }

    /**处理排序字段*/
    fun _handleOrder(queryWrapper: QueryWrapper<Table>, param: IAutoParam) {
        if (param is BaseAutoQueryParam) {
            val desc = param.desc
            if (!desc.isNullOrEmpty()) {
                //降序
                val array = desc.split(BaseAutoQueryParam.SPLIT).map { it.toLowerName() }.toTypedArray()
                queryWrapper.orderByDesc(array.first(), *array)
            }

            val asc = param.asc
            if (!asc.isNullOrEmpty()) {
                //升序
                val array = asc.split(BaseAutoQueryParam.SPLIT).map { it.toLowerName() }.toTypedArray()
                queryWrapper.orderByAsc(array.first(), *array)
            }
        }
    }
}