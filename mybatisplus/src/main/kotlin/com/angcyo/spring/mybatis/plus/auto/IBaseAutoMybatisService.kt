package com.angcyo.spring.mybatis.plus.auto

import com.angcyo.spring.base.aspect.LogMethodTime
import com.angcyo.spring.base.data.ifError
import com.angcyo.spring.base.data.toBeanList
import com.angcyo.spring.base.extension.apiError
import com.angcyo.spring.base.logName
import com.angcyo.spring.mybatis.plus.*
import com.angcyo.spring.mybatis.plus.auto.annotation.*
import com.angcyo.spring.mybatis.plus.auto.core.AutoParse
import com.angcyo.spring.mybatis.plus.auto.param.BaseAutoPageParam
import com.angcyo.spring.mybatis.plus.auto.param.BaseAutoQueryParam
import com.angcyo.spring.mybatis.plus.auto.param.IAutoParam
import com.angcyo.spring.mybatis.plus.auto.param.PlaceholderAutoParam
import com.angcyo.spring.mybatis.plus.service.IBaseMybatisService
import com.angcyo.spring.mybatis.plus.table.BaseAuditTable
import com.angcyo.spring.util.size
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper
import com.baomidou.mybatisplus.core.metadata.IPage
import org.springframework.transaction.annotation.Transactional
import java.io.Serializable

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/05/28
 */
interface IBaseAutoMybatisService<Table> : IBaseMybatisService<Table> {

    /**构建一个解析器*/
    fun buildAutoParse() = AutoParse<Table>()

    /**获取数量*/
    @LogMethodTime
    fun autoCount(param: IAutoParam): Long {
        autoFill(param)
        return count(buildAutoParse().parseQuery(queryWrapper(true), param))
    }

    /**根据[map], 查询出所有数据
     * [ignoreNull] 当value为null时, 跳过where*/
    @LogMethodTime
    @AutoFillRef("com.angcyo.spring.mybatis.plus.auto.core.AutoParse._handleFill")
    fun listOf(map: Map<String, Any?>, ignoreNull: Boolean = true): List<Table> {
        return list(queryWrapper(true).apply {
            map.forEach { entry ->
                val column = entry.key.toLowerName()
                val value = entry.value
                if (value is List<*>) {
                    if (value.isEmpty()) {
                        //WHERE (id IN ()) 这种语法会报错
                        and {
                            it.last("FALSE") //强制塞入 false, 停止语句执行
                        }
                    } else {
                        `in`(column, value)
                    }
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
     * 重新赋值给[IAutoParam]对象中用[com.angcyo.spring.mybatis.plus.auto.annotation.Query]声明的属性
     * */
    fun fillWhereField(list: List<Table>, param: IAutoParam) {
        if (list.isEmpty()) {
            return
        }
        if (list.size() == 1) {
            val first = list.first()
            param.eachAnnotation<Query> { field ->
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
        autoFill(param)
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
    fun <T : IAutoParam> autoCheck(param: T, type: AutoType): T {
        val autoParse = buildAutoParse()
        autoParse.parseCheck(param, type)
        return param
    }

    /**根据[req], 自动查询一条数据*/
    @LogMethodTime
    fun autoQuery(req: IAutoParam? = null): Table? {
        val param = req ?: PlaceholderAutoParam()
        autoFill(param)
        return list(buildAutoParse().parseQuery(queryWrapper(true), param).maxCountLimit(1)).firstOrNull()
    }

    /**根据[req], 自动查询出所有数据*/
    @LogMethodTime
    @AutoFillRef("com.angcyo.spring.mybatis.plus.auto.core.AutoParse._handleFill")
    fun autoList(req: IAutoParam? = null): List<Table> {
        val param = req ?: BaseAutoQueryParam()
        autoFill(param)
        return list(buildAutoParse().parseQuery(queryWrapper(true), param, true).maxCountLimit())
    }

    /**根据[req], 自动分页查询出数据*/
    @LogMethodTime
    fun autoPage(req: BaseAutoPageParam? = null): IPage<Table> {
        val param = req ?: BaseAutoPageParam()
        val autoParse = buildAutoParse()
        autoFill(param)
        return page(autoParse.page(param), autoParse.parseQuery(queryWrapper(true), param, true))
    }

    fun <R> autoPage2(req: BaseAutoPageParam?, cls: Class<R>, initBean: R.(Table) -> Unit = {}): IPage<R> {
        val page = autoPage(req)
        val result = page.records.toBeanList(cls, initBean)
        val resultPage = result.toIPage(page)
        return resultPage
    }

    /**根据[param], 自动保存数据*/
    @LogMethodTime
    @Transactional
    fun autoSave(param: IAutoParam): Table {
        val autoParse = buildAutoParse()
        autoParse.parseFill(param)

        val keyValue = param.keyValue()
        if (keyValue != null) {
            //如果指定了主键, 则使用主键自动更新
            autoUpdateByKey(param)
            return getById(keyValue as Serializable)
        }

        //否则检查数据是否合法, 保存数据
        val count = count(autoParse.parseSaveCheck(queryWrapper(true), param))
        if (count > 0) {
            //数据已存在, 抛出异常
            AutoParse.handleExistError(param, AutoType.SAVE)
        } else {
            //数据不存在, 检查是否需要插入默认值
            autoParse.parseDefaultValue(param)
        }
        val table = param.toTable()
        if (table != param && table is IAutoParam) {
            autoParse.parseFill(table)
        }
        if (save(table)) {
            onSaveTable(listOf(table))
        } else {
            apiError("[${entityClass.logName()}]保存失败")
        }

        return table
    }

    /**保存数据时的回调通知*/
    fun onSaveTable(list: List<Table>) {

    }

    /**软删除, 必须指定主键*/
    @LogMethodTime
    @Transactional
    fun autoDelete(param: IAutoParam): Boolean {
        return autoRemove(param, false)
    }

    /**真删除, 必须指定主键
     * [remove] 硬删除, 否则就是软删除*/
    @LogMethodTime
    @Transactional
    fun autoRemove(param: IAutoParam, remove: Boolean = true): Boolean {
        val autoParse = buildAutoParse()
        autoFill(param)

        val keyField = param.keyField() ?: apiError("未指定主键id")
        val keyValue = keyField.get(param) ?: apiError("未指定主键${keyField.name}值")

        val type = if (remove) AutoType.REMOVE else AutoType.DELETE

        //否则检查数据是否合法, 保存数据
        val count = count(autoParse.parseDeleteCheck(queryWrapper(true), param, true, type))

        if (count > 0) {
            //查询后, 有数据则删除
            return if (remove) {
                removeById(keyValue as Serializable)
            } else {
                //开始软删除
                updateById(newTable().apply {
                    if (this is BaseAuditTable) {
                        this.id = keyValue as Long
                        deleteFlag = BaseAuditTable.DELETE //软删除
                    } else {
                        apiError("表结构有误,无法删除")
                    }
                })
            }.apply {
                if (this) {
                    //删除成功
                    onDeleteTable(keyValue as Serializable, remove)
                }
            }
        } else if (count == 0L) {
            return true
        } else {
            apiError("数据[$keyValue]不存在无法删除")
        }
    }

    /**删除数据时的回调通知
     * [remove] 移除还是软删*/
    fun onDeleteTable(id: Serializable, remove: Boolean) {

    }

    /**如果有主键, 则直接使用主键, 自动更新.
     * 如果没有主键, 则通过查询条件自动更新
     * [maxCountLimit] 允许更新的最大数据数量*/
    @LogMethodTime
    @Transactional
    fun autoUpdate(table: Any, maxCountLimit: Long = 1): Boolean {
        val autoParse = buildAutoParse()

        if (table is IAutoParam) {
            //自动填充数据
            if (!autoParse.parseFill(table)) {
                apiError("处理失败")
            }
        }

        //操作的表对象
        val targetTable: Table = table.toTable()
        if (targetTable != table && targetTable is IAutoParam) {
            if (!autoParse.parseFill(targetTable as IAutoParam)) {
                apiError("处理失败")
            }
        }

        //主键的值
        val keyValue = (targetTable as Any).keyValue()
        val result = if (keyValue == null) {
            //未指定主键
            if (table is IAutoParam) {
                update(targetTable, autoParse.parseUpdate(updateWrapper(), table).maxCountLimit(maxCountLimit))
            } else if (targetTable is IAutoParam) {
                update(targetTable, autoParse.parseUpdate(updateWrapper(), targetTable).maxCountLimit(maxCountLimit))
            } else {
                false
            }
        } else {
            updateById(targetTable)
        }

        return result
    }

    @LogMethodTime
    @Transactional
    fun autoSaveOrUpdates(vararg tables: Any): Boolean {
        return autoSaveOrUpdate(tables.toList())
    }

    /**根据[tables], 自动保存或者根据条件更新数据
     * 每一条记录都会根据条件进行更新操作, 更新失败进行保存操作(可配置开关)
     *
     * 支持的数据类型
     * [Table]
     * [com.angcyo.spring.mybatis.plus.auto.param.IAutoParam]
     * @return 操作是否全部成功*/
    @LogMethodTime
    @Transactional
    fun autoSaveOrUpdate(tableList: List<Any>, config: QueryConfig? = null): Boolean {
        val autoParse = buildAutoParse()

        //更新失败的列表
        val updateFailList = mutableListOf<Table>()

        //更新成功的列表
        val updateSuccessList = mutableListOf<Table>()

        //需要直接保存的记录
        val saveList = mutableListOf<Table>()

        for (table in tableList) {
            if (table.isList()) {
                apiError("数据类型异常[List]")
            }

            val isAutoParam = table is IAutoParam

            var fillSuccess = true
            if (isAutoParam) {
                //自动填充数据
                fillSuccess = autoParse.parseFill(table as IAutoParam)
            }

            //操作的表对象
            val targetTable: Table = table.toTable()
            if (targetTable != table && targetTable is IAutoParam) {
                fillSuccess = autoParse.parseFill(targetTable as IAutoParam)
            }

            if (targetTable is BaseAuditTable && (targetTable.id ?: 0) > 0) {
                //通过id更新记录
                if (updateById(targetTable)) {
                    updateSuccessList.add(targetTable)
                } else {
                    updateFailList.add(targetTable)
                    apiError("ID更新失败:${targetTable.id}")
                }
            } else if (targetTable is IAutoParam && targetTable.haveAnnotation<AutoUpdateBy>(true)) {
                //根据条件更新记录
                val count = count(autoParse.parseQueryByUpdate(queryWrapper(true), targetTable))

                if (count > 0) {
                    //存在数据
                    if (update(targetTable, autoParse.parseUpdate(updateWrapper(), targetTable))) {
                        updateSuccessList.add(targetTable)
                    } else {
                        val autoQuery = targetTable.javaClass.annotation<AutoQueryConfig>()
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

            if (saveResult) {
                onSaveTable(saveList)
            } else {
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
    fun autoReset(tableList: List<Any>, fill: Boolean = true): Boolean {
        val tableMap = hashMapOf<String, MutableList<Any>>()
        val valueQueryMap = hashMapOf<String, MutableList<Any>>()
        val noAnnotationTableList = mutableListOf<Any>()

        for (table in tableList) {

            //自动填充数据
            if (fill && table is IAutoParam) {
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
            val existTableList = list(queryWrapper(true).apply {
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
                    //复制主键的值
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
                    it.getMember(it!!.keyName()) as Serializable
                }).ifError("移除数据失败[${removeList}]")
            }
            if (updateList.isNotEmpty()) {
                if (updateList.size() > BaseAutoPageParam.PAGE_SIZE) {
                    apiError("更新的数据量太大")
                }
                updateBatchById(updateList).ifError("更新数据失败[${updateList}]")
            }
            if (saveList.isNotEmpty()) {
                saveBatch(saveList).ifError("保存数据失败[${saveList}]")
            }
        }

        if (noAnnotationTableList.isNotEmpty()) {
            autoSaveOrUpdate(noAnnotationTableList, QueryConfig().apply {
                updateFailToSave = true
            }).ifError("自动更新数据失败[${noAnnotationTableList}]")
        }

        return true
    }

    @Transactional
    fun autoUpdateByKey(vararg tables: Any): List<Table>? {
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
    fun autoUpdateByKey(tableList: List<Any>): List<Table>? {

        if (tableList.isEmpty()) {
            return null
        }

        val autoParse = buildAutoParse()

        //需要更新的表数据
        val updateTableList = mutableListOf<Table>()

        //主键列表, 用来实现查询返回
        val keyList = mutableListOf<Serializable>()

        //临时存储
        var keyFieldValue: Any? = null

        tableList.forEach { table ->
            if (table is IAutoParam) {
                autoFill(table)
            }

            val keyField = table.keyField()
            keyFieldValue = keyField?.get(table)
            if (keyFieldValue == null) {
                apiError("未指定主键值,无法更新")
            } else {
                keyList.add(keyFieldValue as Serializable)
            }

            if (table is IAutoParam) {
                //查询数据有效性
                var count = count(autoParse.parseUpdateCheck(queryWrapper(true), table))
                if (count <= 0) {
                    apiError("数据[$keyFieldValue]不存在, 无法更新")
                }

                //检查数据是否能被更新
                count = count(autoParse.parseSaveCheck(queryWrapper(true), table, false, AutoType.UPDATE_CHECK))
                if (count > 0) {
                    //数据已存在, 抛出异常
                    AutoParse.handleExistError(table, AutoType.UPDATE_CHECK)
                }
            }

            updateTableList.add(table.toTable())
        }

        if (updateTableList.isEmpty()) {
            return null
        }

        if (updateTableList.size() > BaseAutoPageParam.PAGE_SIZE) {
            apiError("更新的数据量太大")
        }

        //批量更新
        if (updateBatchById(updateTableList)) {
            return listByIds(keyList)
        }
        return null
    }

    /**
     * 根据自动解析查询参数, 查询出来的结果, 更新表
     * ```
     * updateWrapper.set(BaseAuditTable::id.columnName(), xxx)
     * ```
     * [param] 查询参数解析对象
     * */
    @Transactional
    fun autoUpdateByQuery(
        param: IAutoParam,
        error: String? = null,
        dsl: UpdateWrapper<Table>.() -> Unit = {}
    ): Boolean {
        autoFill(param)
        return updateQuery {
            buildAutoParse().parseUpdate(this, param)
            dsl()
        }
    }
}