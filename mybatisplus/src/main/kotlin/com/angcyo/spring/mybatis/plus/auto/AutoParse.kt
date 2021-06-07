package com.angcyo.spring.mybatis.plus.auto

import com.angcyo.spring.base.beanOf
import com.angcyo.spring.base.extension.apiError
import com.angcyo.spring.mybatis.plus.auto.annotation.*
import com.angcyo.spring.mybatis.plus.auto.param.BaseAutoPageParam
import com.angcyo.spring.mybatis.plus.auto.param.BaseAutoQueryParam
import com.angcyo.spring.mybatis.plus.auto.param.IAutoParam
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

    companion object {
        const val OBJ_DOT = "."
    }

    /**
     * 获取对象的属性值
     * [key] 需要获取的属性名, 支持 obj.id
     * */
    fun Any.getObjMember(key: String): Any? {
        if (key.isEmpty()) {
            apiError("无效的属性")
        }
        if (!key.contains(OBJ_DOT)) {
            return getMember(key)
        }
        val keyList = key.split(OBJ_DOT)
        var target: Any? = this
        keyList.forEach { k ->
            if (k.isNotEmpty()) {
                target = target.getMember(k)
            }
        }
        return target
    }

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
        L.i("parseQuery sql->$targetSql")
        return queryWrapper
    }

    /**根据[param]声明的约束, 自动赋值给[updateWrapper] */
    fun parseUpdate(updateWrapper: UpdateWrapper<Table>, param: IAutoParam): UpdateWrapper<Table> {
        _handleQuery(updateWrapper, param) {
            it.annotation<AutoUpdateBy>() == null
        }
        return updateWrapper
    }

    fun parseQueryByUpdate(queryWrapper: QueryWrapper<Table>, param: IAutoParam): QueryWrapper<Table> {
        _handleQuery(queryWrapper, param) {
            it.annotation<AutoUpdateBy>() == null
        }
        return queryWrapper
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
        var serviceClass: Class<*>? = null
        if (fill.service !is PlaceholderAutoMybatisService) {
            serviceClass = fill.service.java
            service = beanOf(fill.service.java)
        } else if (fill.serviceName.isNotEmpty()) {
            serviceClass = Class.forName(fill.serviceName)
            service = beanOf(fill.serviceName)
        }

        var result: List<Any?>? = null

        //------------------------------------调用方法----------------------------------------

        //通过服务传递参数查询数据
        if (service != null) {

            if (fill.serviceMethod.isNotEmpty()) {
                if (serviceClass == null) {
                    apiError("无效的填充服务")
                }
                //需要调用指定的方法
                val argList = fill.methodParamField.split("|")
                val args = mutableListOf<Any?>()
                argList.forEach { key ->
                    args.add(obj.getObjMember(key))
                }
                //1.
                val methodResult = if (args.isEmpty()) {
                    service.invokeMethod(fill.serviceMethod)
                } else {
                    service.invokeMethodClass(fill.serviceMethod, serviceClass, *args.toTypedArray())
                }
                //2.
                result = if (methodResult is List<*>) {
                    methodResult
                } else {
                    mutableListOf(methodResult)
                }
            } else if (service is IBaseAutoMybatisService<*>) {
                val queryColumn = fill.queryColumn
                val queryParamKey = fill.queryParamField.ifEmpty {
                    "${field.name}Query"
                }
                val queryParam = obj.getObjMember(queryParamKey) ?: apiError("无效的查询参数")

                //查询结果
                result = if (queryColumn.isEmpty()) {
                    //未指定查询列, 则可能是需要根据[IAutoParam]参数自动查询
                    if (queryParam is IAutoParam) {
                        service.autoList(queryParam)
                    } else {
                        apiError("无效的查询")
                    }
                } else {
                    //指定了需要查询的列
                    service.listOf(hashMapOf(queryColumn to queryParam))
                }
            }
        }

        //------------------------------------处理结果----------------------------------------

        //返回值处理
        if (result != null) {

            val targetResult = mutableListOf<Any?>()

            //需要获取到的数据字段
            if (fill.targetField.isEmpty()) {
                //为空表示需要获取整个对象
                targetResult.addAll(result)
            } else {
                for (item in result) {
                    targetResult.add(item!!.getObjMember(fill.targetField))
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

    /**处理查询语句
     * [jumpField] 是否要跳过当前的字段*/
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
            //要查询的列
            val column = this.column.ifEmpty { field.name }.toLowerName()
            //对应的值
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