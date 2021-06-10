package com.angcyo.spring.mybatis.plus.auto

import com.angcyo.spring.base.aspect.LogMethodTime
import com.angcyo.spring.base.extension.apiError
import com.angcyo.spring.base.logName
import com.angcyo.spring.mybatis.plus.auto.annotation.*
import com.angcyo.spring.mybatis.plus.auto.param.BaseAutoPageParam
import com.angcyo.spring.mybatis.plus.auto.param.IAutoParam
import com.angcyo.spring.mybatis.plus.keyField
import com.angcyo.spring.mybatis.plus.keyName
import com.angcyo.spring.mybatis.plus.keyValue
import com.angcyo.spring.mybatis.plus.service.IBaseMybatisService
import com.angcyo.spring.mybatis.plus.table.BaseAuditTable
import com.angcyo.spring.mybatis.plus.toLowerName
import com.angcyo.spring.util.copyTo
import com.angcyo.spring.util.size
import com.baomidou.mybatisplus.core.metadata.IPage
import org.springframework.transaction.annotation.Transactional
import java.io.Serializable

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/05/28
 */
interface IBaseAutoMybatisService<Table> : IBaseMybatisService<Table> {

    fun Any.isTable() = javaClass.isAssignableFrom(entityClass)

    fun Any.toTable(): Table {
        return if (isTable()) {
            this as Table
        } else {
            //新的表
            val newTable = entityClass.newInstance()
            //拷贝属性到新表
            //BeanUtils.copyProperties(it, newTable as Any)
            copyTo(newTable as Any)
            newTable
        }
    }

    /**构建一个解析器*/
    fun buildAutoParse() = AutoParse<Table>()

    /**获取数量*/
    @LogMethodTime
    fun autoCount(param: IAutoParam): Int {
        autoFill(param)
        return count(buildAutoParse().parseQuery(queryWrapper(), param))
    }

    /**根据[map], 查询出所有数据
     * [ignoreNull] 当value为null时, 跳过where*/
    @LogMethodTime
    fun listOf(map: Map<String, Any?>, ignoreNull: Boolean = true): List<Table> {
        return list(queryWrapper().apply {
            map.forEach { entry ->
                val column = entry.key.toLowerName()
                val value = entry.value
                if (value is List<*>) {
                    `in`(column, value)
                } else {
                    if (ignoreNull && value == null) {
                    } else {
                        eq(column, value)
                    }
                }
            }
        })
    }

    /**
     * 通过[IAutoParam]对象查询出来的数据库记录,
     * 重新赋值给[IAutoParam]对象中用[com.angcyo.spring.mybatis.plus.auto.annotation.AutoWhere]声明的属性
     * */
    fun fillWhereField(list: List<Table>, param: IAutoParam) {
        if (list.isEmpty()) {
            return
        }
        if (list.size() == 1) {
            val first = list.first()
            param.eachAnnotation<AutoWhere> { field ->
                val key = column.ifEmpty { field.name }
                //赋值属性值
                field.set(param, first.getMember(key))
            }
        } else {
            apiError("数据数量不匹配")
        }
    }

    /**自动查询, 并且获取到返回值, 自动赋值, 然后在自动解析[AutoFill]填充*/
    fun <T : IAutoParam> autoQueryFill(param: T): T {
        val list = autoList(param)
        fillWhereField(list, param)
        val autoParse = buildAutoParse()
        autoParse.parseFill(param)
        return param
    }

    /** 自动填充字段
     * [com.angcyo.spring.mybatis.plus.auto.annotation.AutoFill]*/
    fun <T : IAutoParam> autoFill(param: T): T {
        val autoParse = buildAutoParse()
        autoParse.parseFill(param)
        return param
    }

    /**自动检查数据有效性*/
    fun <T : IAutoParam> autoCheck(param: T): T {
        val autoParse = buildAutoParse()
        autoParse.parseCheck(param)
        return param
    }

    /**根据[param], 自动查询出所有数据*/
    @LogMethodTime
    fun autoList(param: IAutoParam): List<Table> {
        autoFill(param)
        return list(buildAutoParse().parseQuery(queryWrapper(), param))
    }

    /**根据[param], 自动分页查询出数据*/
    @LogMethodTime
    fun autoPage(param: BaseAutoPageParam): IPage<Table> {
        val autoParse = buildAutoParse()
        autoFill(param)
        return page(autoParse.page(param), autoParse.parseQuery(queryWrapper(), param))
    }

    /**根据[param], 自动保存数据*/
    @LogMethodTime
    @Transactional
    fun autoSave(param: IAutoParam): Table {
        val autoParse = buildAutoParse()
        autoFill(param)

        val keyValue = param.keyValue()
        if (keyValue != null) {
            //如果指定了主键, 则使用主键自动更新
            autoUpdateByKey(param)
            return getById(keyValue as Serializable)
        }

        //否则检查数据是否合法, 保存数据
        val count = count(autoParse.parseSaveCheck(queryWrapper(), param))
        if (count > 0) {
            val errorBuilder = StringBuilder()
            param.eachAnnotation<AutoSaveCheck> { field ->
                val fieldValue = field.get(param)
                if (fieldValue != null) {
                    if (error.isNotEmpty()) {
                        errorBuilder.append(error)
                    } else {
                        errorBuilder.append("[${fieldValue}]已存在")
                    }
                }
            }
            if (errorBuilder.isEmpty()) {
                apiError("无法保存数据,数据已存在.")
            } else {
                apiError(errorBuilder)
            }
        }
        val table = param.toTable()
        if (save(table)) {
            //no op
        } else {
            apiError("[${entityClass.logName()}]保存失败")
        }

        return table
    }

    @LogMethodTime
    @Transactional
    fun autoSaveOrUpdates(vararg tables: Any): Boolean {
        return autoSaveOrUpdate(tables.toList())
    }

    /**根据[tables], 自动保存或者根据条件更新数据
     * 每一条记录都会更具条件进行更新操作, 更新失败进行保存操作(可配置开关)
     *
     * 支持的数据类型
     * [Table]
     * [com.angcyo.spring.mybatis.plus.auto.param.IAutoParam]
     * @return 操作是否全部成功*/
    @LogMethodTime
    @Transactional
    fun autoSaveOrUpdate(tableList: List<Any>, config: AutoQueryConfig? = null): Boolean {
        val autoParse = buildAutoParse()

        //更新失败的列表
        val updateFailList = mutableListOf<Table>()

        //更新成功的列表
        val updateSuccessList = mutableListOf<Table>()

        //需要直接保存的记录
        val saveList = mutableListOf<Table>()

        for (table in tableList) {
            val isTable = table.isTable()
            val isAutoParam = table is IAutoParam

            var fillSuccess = true
            if (isAutoParam) {
                //自动填充数据
                fillSuccess = autoParse.parseFill(table as IAutoParam)
            }

            //操作的表对象
            val targetTable: Table = table.toTable()

            if (targetTable is BaseAuditTable && targetTable.id ?: 0 > 0) {
                //通过id更新记录
                if (updateById(targetTable)) {
                    updateSuccessList.add(targetTable)
                } else {
                    updateFailList.add(targetTable)
                    apiError("ID更新失败:${targetTable.id}")
                }
            } else if (targetTable is IAutoParam && targetTable.haveAnnotation<AutoUpdateBy>(true)) {
                //根据条件更新记录
                val count = count(autoParse.parseQueryByUpdate(queryWrapper(), targetTable))

                if (count > 0) {
                    //存在数据
                    if (update(targetTable, autoParse.parseUpdate(updateWrapper(), targetTable))) {
                        updateSuccessList.add(targetTable)
                    } else {
                        val autoQuery = targetTable.javaClass.annotation<AutoQuery>()
                        if (config?.updateFailToSave == true || autoQuery?.updateFailToSave == true) {
                            saveList.add(targetTable)
                        } else {
                            updateFailList.add(targetTable)
                            apiError("[autoSaveOrUpdate]查询更新失败:${targetTable.javaClass}")
                        }
                    }
                } else {
                    //不存在数据, 直接保存
                    saveList.add(targetTable)
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

        if (saveList.isNotEmpty()) {
            //需要直接保存的记录
            val saveResult = saveBatch(saveList)

            if (!saveResult) {
                apiError("保存失败:${saveList.size()}")
            }

            return saveResult && updateFailList.isEmpty()

        }

        return updateFailList.isEmpty()
    }

    @Transactional
    fun autoResets(vararg tables: Any): Boolean {
        return autoReset(tables.toList())
    }

    /**
     * 自动重置数据
     * 1. 先根据条件查询原有的数据集合
     * 2. 比对新的数据集合
     * 3. 进行删除/更新/新增操作
     *
     * 支持的数据类型
     * [Table]
     * [com.angcyo.spring.mybatis.plus.auto.param.IAutoParam]
     *
     * [com.angcyo.spring.mybatis.plus.auto.annotation.AutoResetBy]
     * @return 操作是否全部成功
     * */
    @LogMethodTime
    @Transactional
    fun autoReset(tableList: List<Any>): Boolean {
        val tableMap = hashMapOf<String, MutableList<Any>>()
        val valueQueryMap = hashMapOf<String, MutableList<Any>>()
        val noAnnotationTableList = mutableListOf<Any>()

        for (table in tableList) {

            //自动填充数据
            if (table is IAutoParam) {
                autoFill(table)
            }

            val resetByField = table.annotations<AutoResetBy>(true).firstOrNull()
            if (resetByField == null) {
                noAnnotationTableList.add(table)
            } else {
                //关键key 用于匹配是否相同记录
                val primaryKey = resetByField.name

                //归类
                val list = tableMap[primaryKey] ?: mutableListOf()
                list.add(table)
                tableMap[primaryKey] = list

                resetByField.get(table)?.let {
                    val valueList = valueQueryMap[primaryKey] ?: mutableListOf()
                    valueList.add(it)
                    valueQueryMap[primaryKey] = valueList
                }
            }
        }

        //归类后处理
        tableMap.forEach { entry ->
            val primaryKey = entry.key
            val handleTableList = entry.value

            //数据库中的记录
            val existTableList = list(queryWrapper().apply {
                `in`(primaryKey.toLowerName(), valueQueryMap[primaryKey])
            })

            //需要操作的记录
            val targetTableList = mutableListOf<Table>()
            handleTableList.forEach {
                targetTableList.add(it.toTable())
            }

            //需要移除的记录
            val removeList = mutableListOf<Table>()
            //需要保存的记录
            val saveList = mutableListOf<Table>()
            //需要更新的记录
            val updateList = mutableListOf<Table>()

            existTableList.forEach { existTable ->
                val find = targetTableList.find {
                    val v1 = existTable.getMember(primaryKey)
                    val v2 = it.getMember(primaryKey)
                    v1 != null && v1 == v2
                }

                if (find == null) {
                    //已经存在的记录, 在新的记录中没有找到, 则需要删除
                    removeList.add(existTable)
                } else {
                    val idName = find.keyName()
                    find.setMember(idName, existTable.getMember(idName))
                    updateList.add(find)
                }
            }
            targetTableList.removeAll(updateList)
            saveList.addAll(targetTableList)

            //开始操作
            if (removeList.isNotEmpty()) {
                removeByIds(removeList.mapTo(mutableListOf()) {
                    it.getMember(it!!.keyName()) as Long
                })
            }
            if (updateList.isNotEmpty()) {
                updateBatchById(updateList)
            }
            if (saveList.isNotEmpty()) {
                saveBatch(saveList)
            }
        }

        if (noAnnotationTableList.isNotEmpty()) {
            autoSaveOrUpdate(noAnnotationTableList, AutoQueryConfig().apply {
                updateFailToSave = true
            })
        }

        return true
    }

    @Transactional
    fun autoUpdateByKey(vararg tables: Any): Boolean {
        return autoUpdateByKey(tables.toList())
    }

    /**使用主键, 自动更新
     * [com.angcyo.spring.mybatis.plus.MybatisExKt.keyField]
     *
     * 支持的数据类型
     * [Table]
     * [com.angcyo.spring.mybatis.plus.auto.param.IAutoParam]
     * */
    @Transactional
    fun autoUpdateByKey(tableList: List<Any>): Boolean {
        if (tableList.isEmpty()) {
            return true
        }

        val updateTableList = mutableListOf<Table>()
        var keyFieldName = "id"

        tableList.forEach { table ->
            if (table is IAutoParam) {
                autoFill(table)
            }

            val keyField = table.keyField()
            if (keyField?.get(table) == null) {
                apiError("未指定主键,无法更新")
            }
            keyFieldName = keyField.name.toLowerName()
            updateTableList.add(table.toTable())
        }

        if (updateTableList.isEmpty()) {
            return true
        }

        //判断需要更新的数据是否存在
        val count = if (updateTableList.size() > 1) {
            count(queryWrapper().apply {
                `in`(keyFieldName, updateTableList.mapTo(mutableListOf()) {
                    (it as Any).keyValue()
                })
            })
        } else {
            count(queryWrapper().apply {
                eq(keyFieldName, (updateTableList.first() as Any).keyValue())
            })
        }

        if (count <= 0) {
            apiError("数据不存在, 无法更新")
        }

        //批量更新
        return updateBatchById(updateTableList)
    }
}