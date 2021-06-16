package com.angcyo.spring.mybatis.plus.service

import com.angcyo.spring.base.data.ifError
import com.angcyo.spring.base.extension.apiError
import com.angcyo.spring.mybatis.plus.auto.eachField
import com.angcyo.spring.mybatis.plus.auto.getMember
import com.angcyo.spring.mybatis.plus.auto.setMember
import com.angcyo.spring.mybatis.plus.keyName
import com.angcyo.spring.mybatis.plus.toLowerName
import com.angcyo.spring.util.L
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper
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

    /**获取一个[QueryWrapper]*/
    fun queryWrapper(): QueryWrapper<Table> {
        return QueryWrapper<Table>()
    }

    /**获取一个[UpdateWrapper]*/
    fun updateWrapper(): UpdateWrapper<Table> {
        return UpdateWrapper<Table>()
    }

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

    /**Dsl Query*/
    fun listQuery(dsl: QueryWrapper<Table>.() -> Unit): List<Table> {
        return list(queryWrapper().apply(dsl))
    }

    /**Dsl Count*/
    fun countQuery(dsl: QueryWrapper<Table>.() -> Unit): Int {
        return count(queryWrapper().apply(dsl))
    }
}