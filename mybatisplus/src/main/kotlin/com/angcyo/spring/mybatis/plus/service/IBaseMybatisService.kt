package com.angcyo.spring.mybatis.plus.service

import com.angcyo.spring.base.data.ifError
import com.angcyo.spring.base.data.ifNotExist
import com.angcyo.spring.base.extension.apiError
import com.angcyo.spring.mybatis.plus.*
import com.angcyo.spring.mybatis.plus.auto.eachField
import com.angcyo.spring.mybatis.plus.auto.getMember
import com.angcyo.spring.mybatis.plus.auto.param.BaseAutoPageParam
import com.angcyo.spring.mybatis.plus.auto.param.BaseAutoQueryParam
import com.angcyo.spring.mybatis.plus.auto.setMember
import com.angcyo.spring.mybatis.plus.table.BaseAuditTable
import com.angcyo.spring.mybatis.plus.tree.IBaseTree
import com.angcyo.spring.mybatis.plus.tree.ITree
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

    /**获取一个[QueryWrapper]*/
    fun queryWrapper(): QueryWrapper<Table> {
        return QueryWrapper<Table>()
    }

    /**获取一个[UpdateWrapper]*/
    fun updateWrapper(): UpdateWrapper<Table> {
        return UpdateWrapper<Table>()
    }

    fun noDelete2(wrapper: QueryWrapper<Table>): QueryWrapper<Table> {
        return wrapper.noDelete()
    }

    fun isBaseAuditTable() = BaseAuditTable::class.java.isAssignableFrom(tableClass())

    /**未删除的数据*/
    fun QueryWrapper<Table>.noDelete(): QueryWrapper<Table> {
        if (isBaseAuditTable()) {
            eq(BaseAuditTable::deleteFlag.columnName(), 0)
        }
        return this
    }

    /**处理排序字段
     * [com.angcyo.spring.mybatis.plus.auto.AutoParse._handleOrder]*/
    fun QueryWrapper<Table>.sort(param: Any): QueryWrapper<Table> {
        if (param is BaseAutoQueryParam) {
            val desc = param.desc
            if (!desc.isNullOrEmpty()) {
                //降序
                orderByDesc(desc)
            }

            val asc = param.asc
            if (!asc.isNullOrEmpty()) {
                //升序
                orderByAsc(asc)
            }
        }
        return this
    }

    //</editor-fold desc="Base">

    //<editor-fold desc="From">

    /**通过其他关联表快速查询本表的列表数据
     * 首先, 最终返回的数据是本表的数据.
     *
     * [com.angcyo.spring.mybatis.plus.service.SelectFrom]
     *
     * [com.angcyo.spring.security.service.PermissionService.getUserPermission]
     * */
    fun selectFrom(from: SelectFrom): List<Table> {
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

        return list(queryWrapper().apply {
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
    ) {
        //获取所有已存在的数据
        val existTableList = list(queryWrapper().apply(queryList))
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
            removeByIds(removeList.mapTo(mutableListOf()) {
                it.getMember(it!!.keyName()) as Serializable
            }).ifError("移除数据失败[${removeList}]")
        }
        if (updateList.isNotEmpty()) {
            updateBatchById(updateList).ifError("更新数据失败[${updateList}]")
        }
        if (saveList.isNotEmpty()) {
            saveBatch(saveList).ifError("保存数据失败[${saveList}]")
        }
    }

    /**[queryColumn] 需要查询的列
     * [queryValue] 值
     * [equalField] 判断相等的属性名*/
    fun resetFrom(list: List<Table>, queryColumn: String, queryValue: Any, equalField: String) {
        resetFrom(list, queryList = {
            eq(queryColumn.toLowerName(), queryValue)
        }, { existTable, table ->
            existTable.getMember(equalField) == table.getMember(equalField)
        })
    }

    fun resetFrom(list: List<Table>, equalField: String, queryList: QueryWrapper<Table>.() -> Unit) {
        resetFrom(list, queryList) { existTable, table ->
            existTable.getMember(equalField) == table.getMember(equalField)
        }
    }

    //</editor-fold desc="From">

    //<editor-fold desc="Dsl">

    /**Dsl Remove
     * [error] 返回false时, 异常提示*/
    fun removeQuery(error: String? = null, dsl: QueryWrapper<Table>.() -> Unit): Boolean {
        return remove(queryWrapper().apply(dsl)).apply {
            if (!this && error != null) {
                apiError(error)
            }
        }
    }

    /**Dsl Update*/
    fun updateQuery(entity: Table, error: String? = null, dsl: UpdateWrapper<Table>.() -> Unit): Boolean {
        return update(entity, updateWrapper().apply(dsl)).apply {
            if (!this && error != null) {
                apiError(error)
            }
        }
    }

    /**Dsl Update*/
    fun updateQuery(error: String? = null, dsl: UpdateWrapper<Table>.() -> Unit): Boolean {
        return update(updateWrapper().apply(dsl)).apply {
            if (!this && error != null) {
                apiError(error)
            }
        }
    }

    /**Dsl Query*/
    fun listQuery(dsl: QueryWrapper<Table>.() -> Unit): List<Table> {
        return list(queryWrapper().apply(dsl))
    }

    /**Dsl Page*/
    fun pageQuery(
        pageIndex: Long = 1,
        pageSize: Long = BaseAutoPageParam.PAGE_SIZE,
        dsl: QueryWrapper<Table>.() -> Unit
    ): IPage<Table> {
        val page = Page<Table>(pageIndex, pageSize)
        page.maxLimit = pageSize
        return page(page, queryWrapper().apply(dsl))
    }

    /**Dsl Count*/
    fun countQuery(dsl: QueryWrapper<Table>.() -> Unit): Int {
        return count(queryWrapper().apply(dsl))
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

    /**查询某一个节点下的所有子节点*/
    fun listClazz(parentId: Long): List<Table> {
        return if (parentId.isTopId()) {
            listQuery {
                eq(IBaseTree::parentId.columnName(), parentId)
                noDelete()
            }
        } else {
            val parent = getById(parentId).ifError("parentId[$parentId]不存在") as IBaseTree
            listQuery {
                noDelete()
                likeRight(IBaseTree::parentIds.columnName(), parent.parentIds)
            }.dropWhile {
                (it as Any).keyValue() == parentId
            }
        }
    }

    /**将集合打包成树结构*/
    fun <T : ITree<T>> buildTree(list: List<T>): List<T> {
        //顶点节点
        val topList = mutableListOf<T>()
        //根据key, 存储节点
        val parentMap = hashMapOf<String, T>()
        //根据key, 存储子节点
        val childListMap = hashMapOf<String, MutableList<T>>()

        //具有parent的子节点map的key值列表. 剩下的key, 对应的数据就是无头的child list
        val haveParentChildKeyList = mutableListOf<String>()

        list.forEach { node ->
            val key = node.parentIds
            if (!key.isNullOrBlank()) {
                //根据key 存储节点
                val currentNode = parentMap[key] ?: node
                parentMap[key] = currentNode

                //初始化子节点存储容器
                val childKey = key
                val childList = childListMap[childKey] ?: mutableListOf()
                childListMap[childKey] = childList

                //非顶点
                if (!node.parentId.isTopId()) {
                    //子节点
                    val parentKey = childKey.substring(0, childKey.length - "${node.keyValue()},".length)

                    val parentNode = parentMap[parentKey] ?: node
                    parentMap[parentKey] = parentNode

                    val parentChildList = childListMap[parentKey] ?: mutableListOf()
                    childListMap[parentKey] = parentChildList

                    //节点属于那个parent
                    parentChildList.add(node)
                    parentNode.childList = parentChildList
                    haveParentChildKeyList.add(parentKey)
                }
            }

            if (node.parentId.isTopId()) {
                topList.add(node)
            }
        }

        return topList
    }

    //</editor-fold desc="Tree">
}