package com.angcyo.spring.mybatis.plus.auto

import com.angcyo.spring.mybatis.plus.auto.annotation.AutoColumns
import com.angcyo.spring.mybatis.plus.auto.annotation.AutoWhere
import com.angcyo.spring.mybatis.plus.auto.annotation.WhereEnum
import com.angcyo.spring.mybatis.plus.auto.param.BaseAutoPageParam
import com.angcyo.spring.mybatis.plus.auto.param.BaseAutoQueryParam
import com.angcyo.spring.mybatis.plus.auto.param.IAutoParam
import com.angcyo.spring.mybatis.plus.toLowerName
import com.angcyo.spring.mybatis.plus.toSafeSql
import com.angcyo.spring.util.L
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
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
    fun parse(queryWrapper: QueryWrapper<Table>, param: IAutoParam): QueryWrapper<Table> {
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
    fun _handleQuery(queryWrapper: QueryWrapper<Table>, param: IAutoParam) {
        val autoWhereFieldList = mutableListOf<Field>()

        var and: Any? = null
        var andList: List<Any?>? = null

        var or: Any? = null
        var orList: List<Any?>? = null

        ReflectionKit.getFieldList(param.javaClass).forEach { field ->
            field.annotation<AutoWhere> {
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
                        if (fieldValue.javaClass.isAssignableFrom(List::class.java)) {
                            andList = fieldValue as List<Any?>?
                        } else {
                            and = fieldValue
                        }
                    } else if (field.name == "or") {
                        //特殊字段, or, 等价于 WhereEnum.or
                        if (fieldValue.javaClass.isAssignableFrom(List::class.java)) {
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

        //默认字段的 条件处理
        if (autoWhereFieldList.isNotEmpty()) {
            val autoWhere = param.javaClass.annotation<AutoWhere>()
            if (autoWhere == null || autoWhere.value == WhereEnum.and) {
                //默认所有字段 and 条件处理
                queryWrapper.and { wrapper ->
                    autoWhereFieldList.forEach { field ->
                        _handleWhere(wrapper, field, param)
                    }
                }
            }
        }

        //and条件
        if (and != null && and is IAutoParam) {
            queryWrapper.and { wrapper ->
                _handleQuery(wrapper, and as IAutoParam)
            }
        }

        //and list条件
        if (!andList.isNullOrEmpty()) {
            andList?.forEach { _and ->
                if (_and != null && _and is IAutoParam) {
                    queryWrapper.and { wrapper ->
                        _handleQuery(wrapper, _and)
                    }
                }
            }
        }

        //or条件
        if (or != null && or is IAutoParam) {
            queryWrapper.or { wrapper ->
                _handleQuery(wrapper, or as IAutoParam)
            }
        }

        //or list 条件
        if (!orList.isNullOrEmpty()) {
            orList?.forEach { _or ->
                if (_or != null && _or is IAutoParam) {
                    queryWrapper.or { wrapper ->
                        _handleQuery(wrapper, _or)
                    }
                }
            }
        }
    }

    fun _handleWhere(queryWrapper: QueryWrapper<Table>, field: Field, obj: Any) {
        field.annotation<AutoWhere> {
            val column = field.name.toLowerName()
            val fieldValue = field.get(obj)
            _handleWhere(queryWrapper, column, value, fieldValue)
        }
    }

    /**处理Where表达式*/
    fun _handleWhere(queryWrapper: QueryWrapper<Table>, column: String, where: WhereEnum, value: Any?) {
        when (where) {
            WhereEnum.eq -> queryWrapper.eq(column, value)
            WhereEnum.ne -> queryWrapper.ne(column, value)
            WhereEnum.gt -> queryWrapper.gt(column, value)
            WhereEnum.ge -> queryWrapper.ge(column, value)
            WhereEnum.lt -> queryWrapper.lt(column, value)
            WhereEnum.le -> queryWrapper.le(column, value)
            WhereEnum.like -> queryWrapper.like(column, value)
            WhereEnum.notLike -> queryWrapper.notLike(column, value)
            WhereEnum.likeLeft -> queryWrapper.likeLeft(column, value)
            WhereEnum.likeRight -> queryWrapper.likeRight(column, value)
            WhereEnum.isNull -> queryWrapper.isNull(column)
            WhereEnum.isNotNull -> queryWrapper.isNotNull(column)
            WhereEnum.inSql -> queryWrapper.inSql(column, value?.toString()?.toSafeSql())
            WhereEnum.notInSql -> queryWrapper.notInSql(column, value?.toString()?.toSafeSql())
            WhereEnum.exists -> queryWrapper.exists(column, value?.toString()?.toSafeSql())
            WhereEnum.notExists -> queryWrapper.notExists(column, value?.toString()?.toSafeSql())
            WhereEnum.last -> queryWrapper.last(value?.toString()?.toSafeSql())
            WhereEnum.apply -> queryWrapper.apply(value?.toString()?.toSafeSql())
            else -> {
                val valueClass = value?.javaClass
                if (valueClass?.isAssignableFrom(List::class.java) == true) {
                    val valueList = value as List<*>
                    when (where) {
                        WhereEnum.between -> queryWrapper.between(
                            column,
                            valueList.getOrNull(0),
                            valueList.getOrNull(1)
                        )
                        WhereEnum.notBetween -> queryWrapper.notBetween(
                            column,
                            valueList.getOrNull(0),
                            valueList.getOrNull(1)
                        )
                        WhereEnum.groupBy -> {
                            val stringList = valueList as List<String>
                            queryWrapper.groupBy(stringList.first().toString().toSafeSql(), *stringList.toTypedArray())
                        }
                        WhereEnum.`in` -> queryWrapper.`in`(column, valueList)
                        WhereEnum.notIn -> queryWrapper.notIn(column, valueList)
                    }
                } else {
                    when (where) {
                        WhereEnum.groupBy -> {
                            queryWrapper.groupBy(value.toString().toSafeSql())
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