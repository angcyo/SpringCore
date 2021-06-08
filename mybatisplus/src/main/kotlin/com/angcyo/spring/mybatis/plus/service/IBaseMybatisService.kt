package com.angcyo.spring.mybatis.plus.service

import com.angcyo.spring.base.extension.apiError
import com.angcyo.spring.mybatis.plus.auto.eachField
import com.angcyo.spring.mybatis.plus.toLowerName
import com.angcyo.spring.util.L
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper
import com.baomidou.mybatisplus.extension.service.IService
import com.gitee.sunchenbin.mybatis.actable.utils.ColumnUtils

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

        _recursionForm(from) { selectFrom, isLast ->
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

    fun _recursionForm(from: SelectFrom, each: (SelectFrom, isLast: Boolean) -> Unit) {
        val subFrom = from.from
        if (subFrom == null) {
            //递归
        } else {
            _recursionForm(subFrom, each)
            //一直到最深度, 然后返回再处
        }
        each(from, subFrom == null)
    }

    fun _getAllForm(from: SelectFrom, result: MutableList<SelectFrom>) {
        result.add(from)
        val subFrom = from.from
        if (subFrom != null) {
            //递归
            _getAllForm(subFrom, result)
        }
    }
}