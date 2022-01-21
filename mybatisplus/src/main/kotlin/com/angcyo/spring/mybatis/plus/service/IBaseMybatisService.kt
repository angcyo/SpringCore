package com.angcyo.spring.mybatis.plus.service

import com.angcyo.spring.base.AppProperties
import com.angcyo.spring.base.beanOf
import com.angcyo.spring.base.data.ifError
import com.angcyo.spring.base.data.ifNotExist
import com.angcyo.spring.base.extension.apiError
import com.angcyo.spring.mybatis.plus.*
import com.angcyo.spring.mybatis.plus.auto.*
import com.angcyo.spring.mybatis.plus.auto.param.BaseAutoPageParam
import com.angcyo.spring.mybatis.plus.table.BaseAuditTable
import com.angcyo.spring.mybatis.plus.tree.IBaseTree
import com.angcyo.spring.mybatis.plus.tree.isTopId
import com.angcyo.spring.util.L
import com.angcyo.spring.util.copyTo
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper
import com.baomidou.mybatisplus.core.metadata.IPage
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import com.baomidou.mybatisplus.extension.service.IService
import com.gitee.sunchenbin.mybatis.actable.utils.ColumnUtils
import org.springframework.transaction.annotation.Transactional
import java.io.Serializable

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/05/28
 */
interface IBaseMybatisService<Table> : IService<Table> {

    //<editor-fold desc="Base">

    fun tableClass() = entityClass

    fun Any.isTable() = javaClass.isAssignableFrom(tableClass())

    fun Any.toTable(): Table {
        return if (isTable()) {
            this as Table
        } else {
            //新的表
            val newTable = newTable()
            //拷贝属性到新表
            //BeanUtils.copyProperties(it, newTable as Any)
            copyTo(newTable as Any)
            newTable
        }
    }

    fun newTable() = tableClass().newInstance()

    /**获取一个[QueryWrapper]
     * 是否需要加上[noDelete]?*/
    fun queryWrapper(filterDelete: Boolean = false): QueryWrapper<Table> {
        return QueryWrapper<Table>().apply {
            if (filterDelete) {
                noDelete()
            }
        }
    }

    /**获取一个[UpdateWrapper]*/
    fun updateWrapper(): UpdateWrapper<Table> {
        return UpdateWrapper<Table>()
    }

    //</editor-fold desc="Base">

    //<editor-fold desc="max count limit">

    fun QueryWrapper<Table>.maxCountLimit(maxCountLimit: Long? = beanOf(AppProperties::class.java).maxCountLimit): QueryWrapper<Table> {
        if ((maxCountLimit ?: 0) > 0) {
            last("LIMIT $maxCountLimit")
        }
        return this
    }

    fun UpdateWrapper<Table>.maxCountLimit(maxCountLimit: Long? = beanOf(AppProperties::class.java).maxCountLimit): UpdateWrapper<Table> {
        if ((maxCountLimit ?: 0) > 0) {
            last("LIMIT $maxCountLimit")
        }
        return this
    }

    //</editor-fold desc="max count limit">

    //<editor-fold desc="ex">

    fun noDelete2(wrapper: QueryWrapper<Table>): QueryWrapper<Table> {
        return wrapper.noDelete(tableClass())
    }

    /**设置软删除数据¬*/
    fun UpdateWrapper<Table>.setDelete(flag: Int = BaseAuditTable.DELETE): UpdateWrapper<Table> {
        set(BaseAuditTable::deleteFlag.columnName(), flag)
        return this
    }

    fun QueryWrapper<Table>.noDelete(): QueryWrapper<Table> {
        return noDelete(tableClass())
    }

    fun isBaseAuditTable() = BaseAuditTable::class.java.isAssignableFrom(tableClass())

    /**处理排序字段
     * [com.angcyo.spring.mybatis.plus.auto.core.AutoParse._handleOrder]*/
    fun sort(wrapper: QueryWrapper<Table>, param: Any): QueryWrapper<Table> {
        wrapper.sort(param)
        return wrapper
    }

    //<editor-fold desc="ex">

    //<editor-fold desc="From">

    /**通过其他关联表快速查询本表的列表数据
     * 首先, 最终返回的数据是本表的数据.
     *
     * [com.angcyo.spring.mybatis.plus.service.SelectFrom]
     *
     * [com.angcyo.spring.security.service.PermissionService.getUserPermission]
     * */
    fun selectFrom(from: SelectFrom, filterDelete: Boolean = true): List<Table> {
        var keyColumnName: String? = null
        entityClass.eachField {
            if (ColumnUtils.isKey(it, entityClass)) {
                keyColumnName = it.name.toLowerName()
                return@eachField
            }
        }
        if (keyColumnName.isNullOrEmpty()) {
            apiError("未声明主键")
        }

        var sql = ""

        _recursionFrom(from) { selectFrom, isLast ->
            val fromTable = selectFrom.fromTable
            val fromColumn = selectFrom.fromColumn
            val fromWhere = selectFrom.fromWhere

            if (fromTable.isNullOrEmpty()) {
                apiError("无效的表名")
            }

            if (fromColumn.isNullOrEmpty()) {
                apiError("无效的列名")
            }

            if (isLast) {
                if (fromWhere.isNullOrEmpty()) {
                    apiError("无效的查询条件")
                }
            }

            sql = if (isLast) {
                "SELECT $fromColumn FROM $fromTable WHERE $fromWhere"
            } else {
                "SELECT $fromColumn FROM $fromTable WHERE $fromWhere in ($sql)"
            }
        }

        L.i("selectFrom:$sql")

        return list(queryWrapper(filterDelete).apply {
            if (from.column.isEmpty()) {
                apply(sql)
            } else {
                inSql(from.column, sql)
            }
        })
    }

    /**递归*/
    fun _recursionFrom(from: SelectFrom, each: (SelectFrom, isLast: Boolean) -> Unit) {
        val subFrom = from.from
        if (subFrom == null) {
            //递归
        } else {
            _recursionFrom(subFrom, each)
            //一直到最深度, 然后返回再处
        }
        each(from, subFrom == null)
    }

    fun _getAllFrom(from: SelectFrom, result: MutableList<SelectFrom>) {
        result.add(from)
        val subFrom = from.from
        if (subFrom != null) {
            //递归
            _getAllFrom(subFrom, result)
        }
    }

    /**将一组关联的数据, 重置成新的数据
     * [list] 最终的关联数据
     * [queryList] 用来查询已存在的数据
     * [equalTo] 判断旧数据是否和新数据相同*/
    @Transactional
    fun resetFrom(
        list: List<Table>,
        queryList: QueryWrapper<Table>.() -> Unit,
        equalTo: (existTable: Table, table: Table) -> Boolean
    ): Boolean {
        //获取所有已存在的数据
        val existTableList = list(queryWrapper(true).apply(queryList))
        val targetTableList = mutableListOf<Table>()
        targetTableList.addAll(list)

        //需要移除的记录
        val removeList = mutableListOf<Table>()
        //需要保存的记录
        val saveList = mutableListOf<Table>()
        //需要更新的记录
        val updateList = mutableListOf<Table>()

        existTableList.forEach { existTable ->
            val find = list.find {
                equalTo(existTable, it)
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
            return removeByIds(removeList.mapTo(mutableListOf()) {
                it.getMember(it!!.keyName()) as Serializable
            }).ifError("移除数据失败[${removeList}]")
        }
        if (updateList.isNotEmpty()) {
            return updateBatchById(updateList).ifError("更新数据失败[${updateList}]")
        }
        if (saveList.isNotEmpty()) {
            return saveBatch(saveList).ifError("保存数据失败[${saveList}]")
        }
        return true
    }

    /**[queryColumn] 需要查询的列
     * [queryValue] 值
     * [equalField] 判断相等的属性名
     * ```
     * enrollItemReService.resetFrom(
     *    enrollItemReList,
     *    EnrollItemReTable::enrollId.columnName(),
     *    table.id!!,
     *    EnrollItemReTable::itemId.name
     *  )
     * ```
     * */
    @Transactional
    fun resetFrom(list: List<Table>, queryColumn: String, queryValue: Any, equalField: String): Boolean {
        return resetFrom(list, queryList = {
            eq(queryColumn.toLowerName(), queryValue)
        }, { existTable, table ->
            existTable.getMember(equalField) == table.getMember(equalField)
        })
    }

    /**
     * ```
     * contestClazzService.resetFrom(clazzList, ContestClazzReTable::clazzId.name) {
     *   eq(ContestClazzReTable::customerId.columnName(), customerId)
     *   eq(ContestClazzReTable::contestId.columnName(), contestId)
     * }
     * ```
     * */
    @Transactional
    fun resetFrom(list: List<Table>, equalField: String, queryList: QueryWrapper<Table>.() -> Unit): Boolean {
        return resetFrom(list, queryList) { existTable, table ->
            existTable.getMember(equalField) == table.getMember(equalField)
        }
    }

    //</editor-fold desc="From">

    //<editor-fold desc="Dsl">

    /**如果数据存在, 则更新.
     * 不存在, 则保存*/
    @Transactional
    fun saveOrUpdate(entity: Table, filterDelete: Boolean = true, dsl: QueryWrapper<Table>.() -> Unit): Table {
        val queryWrapper = queryWrapper(filterDelete).apply {
            last("LIMIT 1")
            dsl()
        }

        val one = list(queryWrapper).firstOrNull()

        if (one == null) {
            save(entity).ifError()
        } else {
            (entity as Any).keyField()?.set(entity, one.keyValue())
            updateById(entity).ifError()
        }
        return entity//getOne(queryWrapper, false)
    }

    /**Dsl Remove
     * ```
     * removeQuery {
     *   eq(EnrollItemsReTable::enrollId.columnName(), id)
     * }
     * ```
     *
     * [error] 返回false时, 异常提示*/
    @Transactional
    fun removeQuery(
        error: String? = null,
        filterDelete: Boolean = true,
        dsl: QueryWrapper<Table>.() -> Unit
    ): Boolean {
        return remove(queryWrapper(filterDelete).apply(dsl)).apply {
            if (!this && error != null) {
                apiError(error)
            }
        }
    }

    /**Dsl Update*/
    @Transactional
    fun updateQuery(entity: Table, error: String? = null, dsl: UpdateWrapper<Table>.() -> Unit): Boolean {
        return update(entity, updateWrapper().apply(dsl)).apply {
            if (!this && error != null) {
                apiError(error)
            }
        }
    }

    /**Dsl Update
     * ```
     *  updateQuery {
     *    setSql("${BaseAuditTable::deleteFlag.columnName()} = ${BaseAuditTable.DELETE}")
     *    eq(EnrollTable::id.columnName(), id)
     *  }
     * ```
     * */
    @Transactional
    fun updateQuery(error: String? = null, dsl: UpdateWrapper<Table>.() -> Unit): Boolean {
        return update(updateWrapper().apply(dsl)).apply {
            if (!this && error != null) {
                apiError(error)
            }
        }
    }

    /**逻辑删除
     * [dsl] 中是需要删除的条件
     * */
    @Transactional
    fun deleteQuery(error: String? = null, dsl: UpdateWrapper<Table>.() -> Unit): Boolean {
        return update(updateWrapper().apply {
            setDelete()
            dsl()
        }).apply {
            if (!this && error != null) {
                apiError(error)
            }
        }
    }

    /**Dsl Query*/
    fun listQuery(filterDelete: Boolean = true, dsl: QueryWrapper<Table>.() -> Unit): List<Table> {
        return list(queryWrapper(filterDelete).apply(dsl))
    }

    fun listQueryOne(limit: Long = 1, filterDelete: Boolean = true, dsl: QueryWrapper<Table>.() -> Unit): Table? {
        return list(queryWrapper(filterDelete).apply {
            last("LIMIT $limit")
            dsl()
        }).firstOrNull()
    }

    /**查询最新的一条数据*/
    fun listQueryNewOne(
        limit: Long = 1,
        filterDelete: Boolean = true,
        dsl: QueryWrapper<Table>.() -> Unit
    ): Table? {
        return list(queryWrapper(filterDelete).apply {
            orderByDesc(BaseAuditTable::id.columnName())
            last("LIMIT $limit")
            dsl()
        }).firstOrNull()
    }

    /**in语句不能使用空数据, 这里只是做了一个提前的空数据判断返回*/
    fun listQueryIn(
        coll: Collection<*>,
        filterDelete: Boolean = true,
        dsl: QueryWrapper<Table>.() -> Unit
    ): List<Table> {
        if (coll.isEmpty()) {
            return emptyList()
        }
        return list(queryWrapper(filterDelete).apply(dsl))
    }

    /**Dsl Page*/
    fun pageQuery(
        pageIndex: Long = 1,
        pageSize: Long = BaseAutoPageParam.PAGE_SIZE,
        searchCount: Boolean = true,
        filterDelete: Boolean = true,
        dsl: QueryWrapper<Table>.() -> Unit
    ): IPage<Table> {
        val page = Page<Table>(pageIndex, pageSize, searchCount)
        page.maxLimit = pageSize
        return page(page, queryWrapper(filterDelete).apply(dsl))
    }

    /**Dsl Count*/
    fun countQuery(filterDelete: Boolean = true, dsl: QueryWrapper<Table>.() -> Unit): Long {
        return count(queryWrapper(filterDelete).apply(dsl))
    }

    //</editor-fold desc="Dsl">

    //<editor-fold desc="Tree">

    /**保存一个[IBaseTree]*/
    @Transactional
    fun saveTree(entity: Table) {
        //检查
        if (entity is IBaseTree) {
            if (entity.parentId.isTopId()) {
                //顶级
            } else {
                countQuery {
                    eq(entity.keyName(), entity.parentId)
                }.ifNotExist("指定的parentId[${entity.parentId}]不存在")
            }
        }
        //保存
        save(entity).ifError("保存失败")
        //更新
        if (entity is IBaseTree) {
            val p = if (entity.parentId.isTopId()) {
                //顶级
                IBaseTree.PARENT_SPLIT
            } else {
                val parentIds = (getById(entity.parentId) as IBaseTree).parentIds
                parentIds
            }
            entity.parentIds = "${p}${entity.keyValue()}${IBaseTree.PARENT_SPLIT}"
            updateById(entity).ifError("更新失败")
        }
    }

    @Transactional
    fun updateTreeParentId(entityId: Long, newParentId: Long) {
        updateTreeParentId(getById(entityId).ifError("数据[$entityId]不存在")!!, newParentId)
    }

    /**修改当前树节点的[parentId]
     * 并将节点下的子节点统一更新*/
    @Transactional
    fun updateTreeParentId(entity: Table, newParentId: Long) {
        val tree = entity as IBaseTree
        if (tree.parentId == newParentId) {
            return
        }

        tree.parentId = newParentId

        val oldParentIds = tree.parentIds

        //顶
        val p = if (newParentId.isTopId()) {
            IBaseTree.PARENT_SPLIT
        } else {
            countQuery {
                eq(entity.keyName(), entity.parentId)
            }.ifNotExist("指定的parentId[${entity.parentId}]不存在")

            val newTree = getById(newParentId) as IBaseTree
            newTree.parentIds
        }

        tree.apply {
            parentIds = "${p}${entity.keyValue()}${IBaseTree.PARENT_SPLIT}"
        }

        updateById(entity).ifError("更新失败")

        val newParentIds = tree.parentIds
        updateQuery("更新失败") {
            val column = IBaseTree::parentIds.columnName()
            setSql("$column = REPLACE($column,'$oldParentIds','$newParentIds')")

            likeRight(column, oldParentIds)
        }
    }

    /**查询某一个节点下的所有子节点
     * [parentId] 父节点id, 顶层使用-1*/
    fun listTreeNode(parentId: Long): List<Table> {
        return if (parentId.isTopId()) {
            listQuery {
                eq(IBaseTree::parentId.columnName(), parentId)
            }
        } else {
            val parent = getById(parentId).ifError("parentId[$parentId]不存在") as IBaseTree
            listQuery {
                likeRight(IBaseTree::parentIds.columnName(), parent.parentIds)
            }.dropWhile {
                (it as Any).keyValue() == parentId
            }
        }
    }

    /**查询当前节点, 以及所有parent节点的集合*/
    fun listTreeParentNode(id: Long): List<Table> {
        val table = getById(id)
        if (table is IBaseTree) {
            val parentIds = table.parentIds
            if (parentIds.isNullOrEmpty()) {
                return emptyList()
            }
            val idList = parentIds.split(IBaseTree.PARENT_SPLIT).filter { it.isNotBlank() }
            return listQuery {
                `in`(table.keyName(), idList)
            }
        } else {
            apiError("非树结构类型")
        }
    }

    //</editor-fold desc="Tree">
}