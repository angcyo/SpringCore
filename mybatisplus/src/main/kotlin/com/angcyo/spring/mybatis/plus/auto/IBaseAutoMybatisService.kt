package com.angcyo.spring.mybatis.plus.auto

import com.angcyo.spring.mybatis.plus.auto.annotation.AutoQuery
import com.angcyo.spring.mybatis.plus.auto.annotation.AutoWhere
import com.angcyo.spring.mybatis.plus.auto.param.BaseAutoPageParam
import com.angcyo.spring.mybatis.plus.auto.param.IAutoParam
import com.angcyo.spring.mybatis.plus.service.IBaseMybatisService
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper
import com.baomidou.mybatisplus.core.metadata.IPage
import org.springframework.beans.BeanUtils

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/05/28
 */
interface IBaseAutoMybatisService<Table> : IBaseMybatisService<Table> {

    /**构建一个解析器*/
    fun buildAutoParse() = AutoParse<Table>()

    /**获取一个[QueryWrapper]*/
    fun queryWrapper(): QueryWrapper<Table> {
        return QueryWrapper<Table>()
    }

    /**获取一个[UpdateWrapper]*/
    fun updateWrapper(): UpdateWrapper<Table> {
        return UpdateWrapper<Table>()
    }

    /**获取数量*/
    fun autoCount(param: IAutoParam): Int {
        return count(buildAutoParse().parseQuery(queryWrapper(), param))
    }

    /**根据[param], 自动查询出所有数据*/
    fun autoList(param: IAutoParam): List<Table> {
        return list(buildAutoParse().parseQuery(queryWrapper(), param))
    }

    /**根据[param], 自动分页查询出数据*/
    fun autoPage(param: BaseAutoPageParam): IPage<Table> {
        val autoParse = buildAutoParse()
        return page(autoParse.page(param), autoParse.parseQuery(queryWrapper(), param))
    }

    fun autoSaveOrUpdate(vararg tables: Any): Boolean {
        return autoSaveOrUpdate(tables.toList())
    }

    /**根据[tables], 自动保存或者根据条件更新数据
     * [Table]
     * [com.angcyo.spring.mybatis.plus.auto.param.IAutoParam]
     * @return 操作是否全部成功*/
    fun autoSaveOrUpdate(tablesList: List<Any>): Boolean {
        val autoParse = buildAutoParse()

        //更新失败的列表
        val updateFailList = mutableListOf<Table>()

        //更新成功的列表
        val updateSuccessList = mutableListOf<Table>()

        //需要直接保存的记录
        val saveList = mutableListOf<Table>()

        for (table in tablesList) {
            val isTable = table.javaClass.isAssignableFrom(entityClass)
            val isAutoParam = table is IAutoParam

            var fillSuccess = true
            if (isAutoParam) {
                //自动填充数据
                fillSuccess = autoParse.parseFill(table as IAutoParam)
            }

            //操作的表对象
            val targetTable: Table = if (isTable) {
                table as Table
            } else {
                //新的表
                val newTable = entityClass.newInstance()
                //拷贝属性到新表
                BeanUtils.copyProperties(table, newTable as Any)
                newTable
            }

            if (targetTable is IAutoParam && targetTable.haveAnnotation<AutoWhere>()) {
                //根据条件更新记录
                if (update(targetTable, autoParse.parseUpdate(updateWrapper(), targetTable))) {
                    updateSuccessList.add(targetTable)
                } else {
                    val autoQuery = targetTable.javaClass.annotation<AutoQuery>()
                    if (autoQuery?.updateFailToSave == true) {
                        saveList.add(targetTable)
                    } else {
                        updateFailList.add(targetTable)
                    }
                }
            } else {
                //save
                if (fillSuccess) {
                    saveList.add(targetTable)
                } else {
                    updateFailList.add(targetTable)
                }
            }
        }

        //需要直接保存的记录
        val saveResult = saveBatch(saveList)

        return saveResult && updateFailList.isEmpty()
    }

}