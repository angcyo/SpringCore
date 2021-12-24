package com.angcyo.spring.mybatis.plus.auto.core

import com.angcyo.spring.mybatis.plus.auto.AutoType
import com.angcyo.spring.mybatis.plus.auto.annotation
import com.angcyo.spring.mybatis.plus.auto.annotation.AutoQuery
import com.angcyo.spring.mybatis.plus.auto.annotation.AutoQueryGroup
import com.angcyo.spring.mybatis.plus.auto.annotation.Query
import com.angcyo.spring.mybatis.plus.auto.annotation.WhereEnum
import com.angcyo.spring.mybatis.plus.auto.eachField
import com.angcyo.spring.mybatis.plus.auto.getAnnotation
import com.angcyo.spring.mybatis.plus.auto.param.IAutoParam
import java.lang.reflect.Field

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/12/23
 */
object AutoGroupHelper {

    /**解析需要组合查询条件的分组信息*/
    fun parseAutoQuery(param: IAutoParam, type: AutoType): QueryGroup? {
        var result: QueryGroup? = null

        //获取声明的分组信息
        val autoQueryGroup: AutoQueryGroup? = param.getAnnotation()
        if (autoQueryGroup == null) {
            //无分组信息
            result = defaultAutoQueryGroup(param, type)
        } else {
            //具有分组信息
            val autoQueryGroupQuery = autoQueryGroup.queries.find {
                it.type == type
            }
            result = if (autoQueryGroupQuery == null) {
                defaultAutoQueryGroup(param, type)
            } else {
                //找到了对应类型的分组
                val expression = GroupPatternParse().parse(autoQueryGroupQuery.pattern)
                if (expression.groupList.isNotEmpty() || expression.expressionList.isNotEmpty()) {
                    getQueryGroup(param, expression, autoQueryGroupQuery.jumpEmpty)
                } else {
                    defaultAutoQueryGroup(param, type)
                }
            }
        }
        return result
    }

    /**默认的查询分组*/
    fun defaultAutoQueryGroup(param: IAutoParam, type: AutoType) = QueryGroup().apply {
        obj = param
        or = false
        queryFieldList = getQueryFieldByType(param, type, true)
    }

    /**获取分组以及分组中的字段*/
    fun getQueryGroup(param: IAutoParam, expression: GroupPatternExpression, jumpEmpty: Boolean): QueryGroup {
        return QueryGroup().apply {
            obj = param
            or = expression.op == GroupPatternExpression.OR
            this.jumpEmpty = jumpEmpty

            //直接获取分组名
            expression.groupList.apply {
                if (isNotEmpty()) {
                    val groupList = mutableListOf<QueryGroup>()
                    childQueryGroupList = groupList

                    forEach { groupName ->
                        groupList.add(QueryGroup().apply {
                            obj = param
                            or = false
                            this.jumpEmpty = jumpEmpty
                            queryFieldList = getQueryFieldByGroup(param, groupName, true)
                        })
                    }
                }
            }

            //还有分组表达式
            expression.expressionList.apply {
                if (isNotEmpty()) {
                    val groupList = mutableListOf<QueryGroup>()
                    childQueryGroupList = groupList

                    forEach { groupPatternExpression ->
                        groupList.add(getQueryGroup(param, groupPatternExpression, jumpEmpty))
                    }
                }
            }
        }
    }

    /**获取指定类型[type]的所有字段*/
    fun getQueryFieldByType(param: IAutoParam, type: AutoType, checkIgnore: Boolean): List<QueryField> {
        val result = mutableListOf<QueryField>()
        param.eachField { field ->
            field.annotation<AutoQuery> {
                queries.find { it.type == type }?.let {
                    if (!checkIgnore || !_ignoreField(it, field, param)) {
                        result.add(QueryField(field, it))
                    }
                }
            }
        }
        return result
    }

    /**获取指定分组名的所有字段*/
    fun getQueryFieldByGroup(param: IAutoParam, group: String, checkIgnore: Boolean): List<QueryField> {
        val result = mutableListOf<QueryField>()
        param.eachField { field ->
            field.annotation<AutoQuery> {
                queries.find { it.groups.contains(group) }?.let {
                    if (!checkIgnore || !_ignoreField(it, field, param)) {
                        result.add(QueryField(field, it))
                    }
                }
            }
        }
        return result
    }

    /**是否需要忽略字段*/
    fun _ignoreField(query: Query, field: Field, obj: IAutoParam): Boolean {
        if (query.where == WhereEnum.ignore) {
            return true
        }
        //忽略空字段
        if (query.ignoreNull) {
            return field.get(obj) == null && !query.checkNull
        }
        return false
    }
}