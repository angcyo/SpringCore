package com.angcyo.spring.mybatis.plus.auto.core

import com.angcyo.spring.base.AppProperties
import com.angcyo.spring.base.app
import com.angcyo.spring.base.beanOf
import com.angcyo.spring.base.extension.apiError
import com.angcyo.spring.base.logName
import com.angcyo.spring.mybatis.plus.auto.*
import com.angcyo.spring.mybatis.plus.auto.annotation.*
import com.angcyo.spring.mybatis.plus.auto.param.BaseAutoPageParam
import com.angcyo.spring.mybatis.plus.auto.param.BaseAutoQueryParam
import com.angcyo.spring.mybatis.plus.auto.param.IAutoParam
import com.angcyo.spring.mybatis.plus.toLowerName
import com.angcyo.spring.mybatis.plus.toSafeSql
import com.angcyo.spring.util.L
import com.angcyo.spring.util.size
import com.baomidou.mybatisplus.core.conditions.AbstractWrapper
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import org.springframework.context.expression.BeanFactoryResolver
import org.springframework.core.convert.support.DefaultConversionService
import org.springframework.expression.ExpressionParser
import org.springframework.expression.spel.standard.SpelExpressionParser
import org.springframework.expression.spel.support.StandardEvaluationContext
import java.lang.reflect.Field
import kotlin.math.min


/**
 * [com.angcyo.spring.mybatis.plus.auto.IBaseAutoMybatisService]
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/05/28
 */
class AutoParse<Table> {

    companion object {

        /**xx.xx.xx*/
        const val OBJ_DOT = "."

        /**处理查询的数据是否已存在
         * 保存前, 更新前的检查*/
        fun handleExistError(param: IAutoParam, type: AutoType) {
            //数据已存在, 抛出异常
            val errorBuilder = StringBuilder()
            AutoGroupHelper.getQueryFieldByType(param, type, true).forEach { queryField ->
                val field = queryField.field
                val fieldValue = field.get(param)
                val existError = queryField.query.existError
                val ignoreError = queryField.query.ignoreExistError
                //检查值
                if (fieldValue != null && !ignoreError) {
                    if (errorBuilder.isNotEmpty()) {
                        errorBuilder.append(" or ")
                    }
                    if (existError.isNotEmpty()) {
                        errorBuilder.append(existError)
                    } else {
                        errorBuilder.append("[${field.name}:${fieldValue}]已存在")
                    }
                }
            }
            if (errorBuilder.isEmpty()) {
                apiError("无法保存数据,数据已存在.")
            } else {
                apiError(errorBuilder)
            }
        }

        /**处理长度提示*/
        fun handleLengthTip(name: String, predicate: String, min: Long, max: Long): String {
            return buildString {
                append("[${name}]${predicate}")

                if (min != Long.MIN_VALUE && max != Long.MAX_VALUE) {
                    append("需要在[$min~$max]之间")
                } else if (max == Long.MAX_VALUE) {
                    append("需要[>=$min]")
                } else {
                    append("需要[<=$max]")
                }
            }
        }
    }

    /**
     * 获取对象的属性值
     * [key] 需要获取的属性名, 支持 obj.id
     * */
    fun Any.getObjMember(key: String): Any? {
        if (key.isEmpty()) {
            parseError("[${javaClass.logName()}]无效的属性")
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
        val page = Page<Table>(param.pageIndex, param.pageSize, param.searchCount)
        val maxCountLimit: Long = beanOf(AppProperties::class.java).maxCountLimit
        page.maxLimit = min(maxCountLimit, param.pageSize)
        return page
    }

    /**自动验证数据的合法性
     * 根据[Check]注解, 检查字段值*/
    fun parseCheck(param: IAutoParam, type: AutoType) {
        param.eachAnnotation<AutoCheck> { field ->
            this.checks.find { it.type == type }?.apply {

                val fieldValue = field.get(param)
                val error = this.error

                //检查null
                if (checkNull) {
                    if (fieldValue == null) {
                        parseError(error.ifEmpty { "[${field.name}]不能为null" })
                    }
                }

                //检查空对象
                if (checkEmpty) {
                    if (field.isClass(String::class.java)) {
                        if (fieldValue == null || (fieldValue as String).isEmpty()) {
                            parseError(error.ifEmpty { "[${field.name}]不能为空" })
                        }
                    } else if (field.isClass(List::class.java)) {
                        if (fieldValue == null || (fieldValue as List<*>).isEmpty()) {
                            parseError(error.ifEmpty { "[${field.name}]不能为空" })
                        }
                    }
                }

                //检查字符串/数组的长度
                if (checkLength || (min != Long.MIN_VALUE || max != Long.MAX_VALUE)) {
                    if (field.isClass(String::class.java)) {
                        if (fieldValue == null || ((fieldValue as String).length < min || fieldValue.length > max)) {
                            parseError(error.ifEmpty { handleLengthTip(field.name, "的长度", min, max) })
                        }
                    } else if (field.isClass(List::class.java)) {
                        if (fieldValue == null || ((fieldValue as List<*>).size() < min || fieldValue.size() > max)) {
                            parseError(error.ifEmpty { handleLengthTip(field.name, "的长度", min, max) })
                        }
                    } else if (field.isClass(Array::class.java)) {
                        if (fieldValue == null || ((fieldValue as Array<*>).size() < min || fieldValue.size() > max)) {
                            parseError(error.ifEmpty { handleLengthTip(field.name, "的长度", min, max) })
                        }
                    }
                }

                //检查数字的大小
                if (checkSize || (min != Long.MIN_VALUE || max != Long.MAX_VALUE)) {
                    if (field.isClass(Number::class.java)) {
                        if (fieldValue == null || ((fieldValue as Number).toLong() < min || fieldValue.toLong() > max)) {
                            parseError(error.ifEmpty {
                                handleLengthTip(
                                    field.name,
                                    "的大小",
                                    min,
                                    max
                                )
                            })
                        }
                    }
                }
            }
        }
    }

    /**根据[param]声明的约束, 自动赋值给[queryWrapper]
     * [org.springframework.beans.BeanUtils.getPropertyDescriptors]
     * */
    fun parseQuery(
        queryWrapper: QueryWrapper<Table>,
        param: IAutoParam,
        jumpEmptyQuery: Boolean = false,
        type: AutoType = AutoType.QUERY
    ): QueryWrapper<Table> {
        //选择列
        _handleSelector(queryWrapper, param)

        //查询
        _handleQuery(type, queryWrapper, param, jumpEmptyQuery)

        //排序
        _handleOrder(queryWrapper, param)

        val targetSql = queryWrapper.targetSql
        L.i("parseQuery sql->$targetSql")
        return queryWrapper
    }

    /**根据[param]声明的约束, 自动赋值给[updateWrapper] */
    fun parseUpdate(
        updateWrapper: UpdateWrapper<Table>,
        param: IAutoParam,
        jumpEmptyQuery: Boolean = false,
        type: AutoType = AutoType.UPDATE
    ): UpdateWrapper<Table> {
        _handleQuery(type, updateWrapper, param, jumpEmptyQuery)
        val targetSql = updateWrapper.targetSql
        L.i("parseUpdate sql->$targetSql")
        return updateWrapper
    }

    fun parseQueryByUpdate(
        queryWrapper: QueryWrapper<Table>,
        param: IAutoParam,
        jumpEmptyQuery: Boolean = false,
        type: AutoType = AutoType.UPDATE
    ): QueryWrapper<Table> {
        _handleQuery(type, queryWrapper, param, jumpEmptyQuery)
        val targetSql = queryWrapper.targetSql
        L.i("parseQueryByUpdate sql->$targetSql")
        return queryWrapper
    }

    /**自动解析并填充对象
     * [com.angcyo.spring.mybatis.plus.auto.annotation.AutoFill]
     * @return 是否解析成功, 没有出现错误*/
    fun parseFill(param: IAutoParam): Boolean {
        var haveError = false

        val fillList = AnnotationHelper.parseAnnotations(param, AutoFill::class.java)

        for (fill in fillList) {

            if (!fill.annotation.force) {
                //非强制填充
                val value = fill.field.get(param)
                if (value != null) {
                    //已经有值了, 跳过填充
                    continue
                }
            }

            if (!_handleFill(fill.annotation, fill.field, param)) {
                haveError = true
                break
            }
        }
        return !haveError
    }

    /**塞入默认值*/
    fun parseDefaultValue(param: IAutoParam, type: AutoType = AutoType.SAVE) {
        AutoGroupHelper.getQueryFieldByType(param, type, false).forEach {
            val field = it.field
            val defaultValue = it.query.defaultValue
            val fieldValue = field.get(param)
            if (fieldValue == null && defaultValue.isNotEmpty()) {
                //需要设置默认值
                //Spring类型转换
                val value = DefaultConversionService.getSharedInstance().convert(defaultValue, field.type)
                field.set(param, value)
            }
        }
    }

    /**
     * 检查数据是否已存在
     * [com.angcyo.spring.mybatis.plus.auto.annotation.AutoSave]*/
    fun parseSaveCheck(
        queryWrapper: QueryWrapper<Table>,
        param: IAutoParam,
        jumpEmptyQuery: Boolean = false,
        type: AutoType = AutoType.SAVE
    ): QueryWrapper<Table> {
        //查询
        _handleQuery(type, queryWrapper, param, jumpEmptyQuery)

        val targetSql = queryWrapper.targetSql
        L.i("parseSaveCheck sql->$targetSql")

        return queryWrapper
    }

    /**
     * 检查数据是否已存在
     * [com.angcyo.spring.mybatis.plus.auto.annotation.AutoDelete]*/
    fun parseDeleteCheck(
        queryWrapper: QueryWrapper<Table>,
        param: IAutoParam,
        jumpEmptyQuery: Boolean = false,
        type: AutoType = AutoType.DELETE
    ): QueryWrapper<Table> {
        //查询
        _handleQuery(type, queryWrapper, param, jumpEmptyQuery)

        val targetSql = queryWrapper.targetSql
        L.i("parseDeleteCheck sql->$targetSql")

        return queryWrapper
    }

    /**
     * 更新检查数据是否已存在
     * [com.angcyo.spring.mybatis.plus.auto.annotation.AutoUpdate]*/
    fun parseUpdateCheck(
        queryWrapper: QueryWrapper<Table>,
        param: IAutoParam,
        jumpEmptyQuery: Boolean = false,
        type: AutoType = AutoType.UPDATE
    ): QueryWrapper<Table> {
        //查询
        _handleQuery(type, queryWrapper, param, jumpEmptyQuery)

        val targetSql = queryWrapper.targetSql
        L.i("parseUpdateCheck sql->$targetSql")

        return queryWrapper
    }

    fun _handleFill(fill: AutoFill, field: Field, obj: IAutoParam): Boolean {

        //反射获取对应服务
        var service: Any? = null
        var serviceClass: Class<*>? = null

        if (fill.spEL.isNotEmpty()) {
            //使用Spring表达式语言（简称SpEL）解析

            //创建SpEL表达式的解析器
            val parser: ExpressionParser = SpelExpressionParser()
            val exp = parser.parseExpression(fill.spEL)
            //取出解析结果
            val context = StandardEvaluationContext(obj)
            context.setBeanResolver(BeanFactoryResolver(app))
            //@userAccountService.test(#this, #root, id) //当前对象
            val result = exp.getValue(context)

            //Spring类型转换
            val value = DefaultConversionService.getSharedInstance().convert(result, field.type)
            field.set(obj, value)
            return true
        } else if (fill.service !is PlaceholderAutoMybatisService) {
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
                    parseError("无效的填充服务")
                }
                //需要调用指定的方法
                val argList = fill.methodParamField.split("|")
                val args = mutableListOf<Any?>()
                argList.forEach { key ->
                    if (key.isNotEmpty()) {
                        if (key == "this") {
                            args.add(obj)
                        } else {
                            args.add(obj.getObjMember(key))
                        }
                    }
                }
                //1.开始调用方法
                val methodResult = if (args.isEmpty()) {
                    service.invokeMethod(fill.serviceMethod)
                } else {
                    service.invokeMethodClass(fill.serviceMethod, serviceClass, *args.toTypedArray())
                }
                //2.获取方法返回值
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
                val queryParam = obj.getObjMember(queryParamKey) ?: parseError("无效的查询参数")

                //查询结果
                result = if (queryColumn.isEmpty()) {
                    //未指定查询列, 则可能是需要根据[IAutoParam]参数自动查询
                    if (queryParam is IAutoParam) {
                        service.autoList(queryParam)
                    } else {
                        parseError("无效的查询")
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

                if (targetResult.isEmpty() && fill.errorOnNull) {
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
     * [jumpField] 是否要跳过当前的字段
     * [com.angcyo.spring.mybatis.plus.auto.AutoParse._handleWhere]*/
    fun <Wrapper : AbstractWrapper<Table, String, Wrapper>> _handleQuery(
        type: AutoType,
        wrapper: AbstractWrapper<Table, String, Wrapper>,
        param: IAutoParam,
        jumpEmptyQuery: Boolean = false,
        jumpField: (field: Field) -> Boolean = { false }
    ) {
        val queryGroup = AutoGroupHelper.parseAutoQuery(param, type) ?: apiError("无法处理的查询")

        if (queryGroup.isQueryEmpty() && (queryGroup.jumpEmpty || jumpEmptyQuery)) {
            //跳过空查询
        } else {
            if (wrapper.isEmptyOfWhere) {
                //空查询
                _handleQueryGroup(wrapper, queryGroup, jumpEmptyQuery, jumpField)
            } else {
                wrapper.and {
                    _handleQueryGroup(it, queryGroup, jumpEmptyQuery, jumpField)
                }
            }
        }
    }

    /**处理[QueryGroup]*/
    fun <Wrapper : AbstractWrapper<Table, String, Wrapper>> _handleQueryGroup(
        wrapper: AbstractWrapper<Table, String, Wrapper>,
        group: QueryGroup,
        jumpEmptyQuery: Boolean = false, //空查询时, 是否不拼接false
        jumpField: (field: Field) -> Boolean = { false }
    ) {
        val queryFieldList = group.queryFieldList?.filter { !jumpField(it.field) }
        val childGroupList = group.childQueryGroupList

        if (group.isQueryEmpty()) {
            //空查询时, 插入的sql
            if (group.jumpEmpty || jumpEmptyQuery) {
                //jump
            } else {
                wrapper.last("FALSE")
            }
        } else {
            //需要组装查询

            val param = group.obj!!

            //查询字段拼装
            queryFieldList?.forEach { queryField ->
                val field = queryField.field
                val fieldValue = field.get(param)
                val where = queryField.query.where

                val ignoreHandle = where == WhereEnum.ignore

                if (!ignoreHandle) {
                    if (fieldValue == null) {
                        //空值处理, 异常检查
                        //[com.angcyo.spring.mybatis.plus.auto.core.AutoGroupHelper._ignoreField]
                        if (queryField.query.checkNull) {
                            parseError(queryField.query.nullError.ifEmpty { "参数[${field.name}]未指定" })
                        }
                    }
                }

                if (!ignoreHandle) {
                    //处理列的查询sql
                    val column = queryField.query.column.ifEmpty { field.name }.toLowerName()

                    if (group.or) {
                        wrapper.or {
                            _handleWhere(it, column, where, fieldValue)
                        }
                    } else {
                        _handleWhere(wrapper, column, where, fieldValue)
                    }
                }
            }

            //查询分组拼装
            childGroupList?.forEach { queryGroup ->
                if (queryGroup.isQueryEmpty() && (queryGroup.jumpEmpty || jumpEmptyQuery)) {
                    //跳过空查询
                } else {
                    if (group.or) {
                        wrapper.or {
                            _handleQueryGroup(it, queryGroup, jumpEmptyQuery, jumpField)
                        }
                    } else {
                        _handleQueryGroup(wrapper, queryGroup, jumpEmptyQuery, jumpField)
                    }
                }
            }
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
            WhereEnum.ignore -> Unit //忽略
            else -> {
                //其他条件
                val valueClass = value?.javaClass
                if (valueClass?.isList() == true) {
                    //列表情况下
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
                            wrapper.groupBy(valueList.joinToString(",").toSafeSql())
                        }
                        WhereEnum.`in` -> wrapper.`in`(column, valueList)
                        WhereEnum.notIn -> wrapper.notIn(column, valueList)
                        else -> Unit
                    }
                } else {
                    //单对象情况下
                    when (where) {
                        WhereEnum.groupBy -> {
                            wrapper.groupBy(value.toString().toSafeSql())
                        }
                        else -> Unit
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
                queryWrapper.sortDesc(desc.split(BaseAutoQueryParam.SPLIT))
            }

            val asc = param.asc
            if (!asc.isNullOrEmpty()) {
                //升序
                queryWrapper.sortAsc(asc.split(BaseAutoQueryParam.SPLIT))
            }
        }
    }
}