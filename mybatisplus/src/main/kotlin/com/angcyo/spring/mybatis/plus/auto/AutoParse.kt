package com.angcyo.spring.mybatis.plus.auto

import com.angcyo.spring.base.beanOf
import com.angcyo.spring.base.logName
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
import com.baomidou.mybatisplus.core.toolkit.ReflectionKit
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import org.springframework.core.convert.support.DefaultConversionService
import java.lang.reflect.Field

/**
 * [com.angcyo.spring.mybatis.plus.auto.IBaseAutoMybatisService]
 *
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
        page.maxLimit = param.pageSize
        return page
    }

    /**自动验证数据的合法性*/
    fun parseCheck(param: IAutoParam) {
        param.eachAnnotation<AutoCheck> { field ->
            val fieldValue = field.get(param)
            val error = this.error

            //
            if (checkNull) {
                if (fieldValue == null) {
                    parseError(error.ifEmpty { "[${field.name}]不能为null" })
                }
            }

            //
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

            //
            if (checkLength) {
                if (field.isClass(String::class.java)) {
                    if (fieldValue == null || ((fieldValue as String).length < min || fieldValue.length > max)) {
                        parseError(error.ifEmpty { "[${field.name}]长度需要在[$min..$max]之间" })
                    }
                } else if (field.isClass(List::class.java)) {
                    if (fieldValue == null || ((fieldValue as List<*>).size() < min || fieldValue.size() > max)) {
                        parseError(error.ifEmpty { "[${field.name}]长度需要在[$min..$max]之间" })
                    }
                }
            }

            //
            if (checkSize) {
                if (field.isClass(Number::class.java)) {
                    if (fieldValue == null || ((fieldValue as Number).toLong() < min || fieldValue.toLong() > max)) {
                        parseError(error.ifEmpty { "[${field.name}]大小需要在[$min..$max]之间" })
                    }
                }
            }
        }
    }

    /**根据[param]声明的约束, 自动赋值给[queryWrapper]
     * [org.springframework.beans.BeanUtils.getPropertyDescriptors]
     * */
    fun parseQuery(queryWrapper: QueryWrapper<Table>, param: IAutoParam): QueryWrapper<Table> {
        //选择列
        _handleSelector(queryWrapper, param)

        //查询
        _handleQuery(queryWrapper, param, AutoQuery::class.java)

        //排序
        _handleOrder(queryWrapper, param)

        val targetSql = queryWrapper.targetSql
        L.i("parseQuery sql->$targetSql")
        return queryWrapper
    }

    /**根据[param]声明的约束, 自动赋值给[updateWrapper] */
    fun parseUpdate(updateWrapper: UpdateWrapper<Table>, param: IAutoParam): UpdateWrapper<Table> {
        _handleQuery(updateWrapper, param, AutoQuery::class.java) {
            it.annotation<AutoUpdateBy>() == null
        }
        return updateWrapper
    }

    fun parseQueryByUpdate(queryWrapper: QueryWrapper<Table>, param: IAutoParam): QueryWrapper<Table> {
        _handleQuery(queryWrapper, param, AutoQuery::class.java) {
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

    /**塞入默认值*/
    fun parseDefaultValue(param: IAutoParam) {
        param.eachAnnotation<AutoSave> { field ->
            val fieldValue = field.get(param)
            if (fieldValue == null && defaultValue.isNotEmpty()) {
                //需要设置默认值
                val value = DefaultConversionService.getSharedInstance().convert(defaultValue, field.type)
                field.set(param, value)
            }
        }
    }

    /**
     * 检查数据是否已存在
     * [com.angcyo.spring.mybatis.plus.auto.annotation.AutoSave]*/
    fun parseSaveCheck(queryWrapper: QueryWrapper<Table>, param: IAutoParam): QueryWrapper<Table> {
        //查询
        _handleQuery(queryWrapper, param, AutoSave::class.java)

        val targetSql = queryWrapper.targetSql
        L.i("parseSaveCheck sql->$targetSql")

        return queryWrapper
    }

    /**
     * 检查数据是否已存在
     * [com.angcyo.spring.mybatis.plus.auto.annotation.AutoDelete]*/
    fun parseDeleteCheck(queryWrapper: QueryWrapper<Table>, param: IAutoParam): QueryWrapper<Table> {
        //查询
        _handleQuery(queryWrapper, param, AutoDelete::class.java)

        val targetSql = queryWrapper.targetSql
        L.i("parseDeleteCheck sql->$targetSql")

        return queryWrapper
    }

    /**
     * 更新检查数据是否已存在
     * [com.angcyo.spring.mybatis.plus.auto.annotation.AutoUpdate]*/
    fun parseUpdateCheck(queryWrapper: QueryWrapper<Table>, param: IAutoParam): QueryWrapper<Table> {
        //查询
        _handleQuery(queryWrapper, param, AutoUpdate::class.java)

        val targetSql = queryWrapper.targetSql
        L.i("parseUpdateCheck sql->$targetSql")

        return queryWrapper
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
                    parseError("无效的填充服务")
                }
                //需要调用指定的方法
                val argList = fill.methodParamField.split("|")
                val args = mutableListOf<Any?>()
                argList.forEach { key ->
                    if (key.isNotEmpty()) {
                        args.add(obj.getObjMember(key))
                    }
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
    fun <Wrapper : AbstractWrapper<Table, String, Wrapper>,
            Where : Annotation,
            Group : Annotation,
            > _handleQuery(
        wrapper: AbstractWrapper<Table, String, Wrapper>,
        param: IAutoParam,
        whereClass: Class<Where>,
        jumpField: (field: Field) -> Boolean = { false }
    ) {
        val group:Group = param.getAnnotation<Group>()


        val autoWhereFieldList = mutableListOf<Field>()

        var and: Any? = null
        var andList: List<Any?>? = null

        var or: Any? = null
        var orList: List<Any?>? = null

        ReflectionKit.getFieldList(param.javaClass).forEach { field ->
            field.annotation(whereClass) {
                if (jumpField.invoke(field)) {
                    //jump
                } else {
                    val fieldValue = field.get(param)

                    if (fieldValue == null) {
                        //空值处理

                        //空值检查处理
                        fun checkNull(where: WhereEnum, checkNull: Boolean, error: String) {
                            if (where == WhereEnum.isNull || where == WhereEnum.isNotNull) {
                                autoWhereFieldList.add(field)
                            }
                            if (checkNull && where != WhereEnum.ignore) {
                                parseError(error.ifEmpty { "参数[${field.name}]未指定" })
                            }
                        }

                        /** [com.angcyo.spring.mybatis.plus.auto.AutoParse._handleWhere]*/
                        when {
                            whereClass.isAssignableFrom(AutoQuery::class.java) -> {
                                this as AutoQuery
                                checkNull(this.where, checkNull, nullError)
                            }
                            whereClass.isAssignableFrom(AutoSave::class.java) -> {
                                this as AutoSave
                                checkNull(this.where, checkNull, existError)
                            }
                            whereClass.isAssignableFrom(AutoDelete::class.java) -> {
                                this as AutoDelete
                                checkNull(this.where, checkNull, nullError)
                            }
                            whereClass.isAssignableFrom(AutoUpdate::class.java) -> {
                                this as AutoUpdate
                                checkNull(this.where, checkNull, nullError)
                            }
                            else -> parseError("参数[$whereClass]类型有误")
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
            val autoWhere = param.javaClass.annotation<AutoQuery>()
            if (autoWhere == null || autoWhere.where == WhereEnum.and) {
                //默认所有字段 and 条件处理
                wrapper.and { w ->
                    autoWhereFieldList.forEach { field ->
                        _handleWhere(w, field, param, whereClass)
                    }
                }
            }
        }

        //and条件
        if (and != null && and is IAutoParam) {
            wrapper.and { w ->
                _handleQuery(w, and as IAutoParam, whereClass, jumpField)
            }
        }

        //and list条件
        if (!andList.isNullOrEmpty()) {
            andList?.forEach { _and ->
                if (_and != null && _and is IAutoParam) {
                    wrapper.and { wrapper ->
                        _handleQuery(wrapper, _and, whereClass, jumpField)
                    }
                }
            }
        }

        //or条件
        if (or != null && or is IAutoParam) {
            wrapper.or { w ->
                _handleQuery(w, or as IAutoParam, whereClass, jumpField)
            }
        }

        //or list 条件
        if (!orList.isNullOrEmpty()) {
            orList?.forEach { _or ->
                if (_or != null && _or is IAutoParam) {
                    wrapper.or { wrapper ->
                        _handleQuery(wrapper, _or, whereClass, jumpField)
                    }
                }
            }
        }
    }

    fun <Wrapper : AbstractWrapper<Table, String, Wrapper>, Where : Annotation> _handleWhere(
        wrapper: AbstractWrapper<Table, String, Wrapper>,
        field: Field,
        obj: Any,
        where: Class<Where>
    ) {
        field.annotation(where) {
            //要查询的列
            var column: String? = null
            var whereEnum: WhereEnum? = null
            var isOr: Boolean = false
            //对应的值
            val fieldValue: Any? = field.get(obj)

            fun handle(where: WhereEnum, columnName: String, or: Boolean) {
                //要查询的列
                column = columnName.ifEmpty { field.name }.toLowerName()
                whereEnum = where
                isOr = or
            }

            when {
                where.isAssignableFrom(AutoQuery::class.java) -> {
                    this as AutoQuery
                    handle(this.where, this.column, this.isOr)
                }
                where.isAssignableFrom(AutoSave::class.java) -> {
                    this as AutoSave
                    handle(this.where, this.column, this.isOr)
                }
                where.isAssignableFrom(AutoDelete::class.java) -> {
                    this as AutoDelete
                    handle(this.where, this.column, this.isOr)
                }
                where.isAssignableFrom(AutoUpdate::class.java) -> {
                    this as AutoUpdate
                    handle(this.where, this.column, this.isOr)
                }
            }

            if (column != null && whereEnum != null) {
                _handleWhere(wrapper, column!!, whereEnum!!, fieldValue, isOr)
            }
        }
    }

    /**处理Where表达式*/
    fun <Wrapper : AbstractWrapper<Table, String, Wrapper>> _handleWhere(
        wrapper: AbstractWrapper<Table, String, Wrapper>,
        column: String,
        where: WhereEnum,
        value: Any?,
        isOr: Boolean
    ) {
        var _wrapper = wrapper

        if (isOr) {
            wrapper.or { w ->
                _wrapper = w
            }
        }

        when (where) {
            WhereEnum.eq -> _wrapper.eq(column, value)
            WhereEnum.ne -> _wrapper.ne(column, value)
            WhereEnum.gt -> _wrapper.gt(column, value)
            WhereEnum.ge -> _wrapper.ge(column, value)
            WhereEnum.lt -> _wrapper.lt(column, value)
            WhereEnum.le -> _wrapper.le(column, value)
            WhereEnum.like -> _wrapper.like(column, value)
            WhereEnum.notLike -> _wrapper.notLike(column, value)
            WhereEnum.likeLeft -> _wrapper.likeLeft(column, value)
            WhereEnum.likeRight -> _wrapper.likeRight(column, value)
            WhereEnum.isNull -> _wrapper.isNull(column)
            WhereEnum.isNotNull -> _wrapper.isNotNull(column)
            WhereEnum.inSql -> _wrapper.inSql(column, value?.toString()?.toSafeSql())
            WhereEnum.notInSql -> _wrapper.notInSql(column, value?.toString()?.toSafeSql())
            WhereEnum.exists -> _wrapper.exists(column, value?.toString()?.toSafeSql())
            WhereEnum.notExists -> _wrapper.notExists(column, value?.toString()?.toSafeSql())
            WhereEnum.last -> _wrapper.last(value?.toString()?.toSafeSql())
            WhereEnum.apply -> _wrapper.apply(value?.toString()?.toSafeSql())
            WhereEnum.ignore -> Unit //忽略
            else -> {
                val valueClass = value?.javaClass
                if (valueClass?.isList() == true) {
                    val valueList = value as List<*>
                    when (where) {
                        WhereEnum.between -> _wrapper.between(
                            column,
                            valueList.getOrNull(0),
                            valueList.getOrNull(1)
                        )
                        WhereEnum.notBetween -> _wrapper.notBetween(
                            column,
                            valueList.getOrNull(0),
                            valueList.getOrNull(1)
                        )
                        WhereEnum.groupBy -> {
                            val stringList = valueList as List<String>
                            _wrapper.groupBy(stringList.joinToString(",").toSafeSql())
                        }
                        WhereEnum.`in` -> _wrapper.`in`(column, valueList)
                        WhereEnum.notIn -> _wrapper.notIn(column, valueList)
                        else -> Unit
                    }
                } else {
                    when (where) {
                        WhereEnum.groupBy -> {
                            _wrapper.groupBy(value.toString().toSafeSql())
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