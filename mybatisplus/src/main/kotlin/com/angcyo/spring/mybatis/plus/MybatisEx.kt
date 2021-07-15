package com.angcyo.spring.mybatis.plus

import com.angcyo.spring.base.toObj
import com.angcyo.spring.mybatis.plus.auto.setMember
import com.angcyo.spring.mybatis.plus.table.BaseAuditTable
import com.baomidou.mybatisplus.core.conditions.AbstractWrapper
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper
import com.gitee.sunchenbin.mybatis.actable.utils.ColumnUtils
import com.gitee.sunchenbin.mybatis.actable.utils.FieldUtils
import com.google.common.base.CaseFormat
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/02/26
 */

/**https://mp.baomidou.com/guide/wrapper.html#querywrapper*/
fun <T> queryWrapper(action: QueryWrapper<T>.() -> Unit): QueryWrapper<T> {
    return QueryWrapper<T>().apply {
        //where条件
        //eq()
        apply(action)
    }
}

fun <T> queryLambdaWrapper(action: LambdaQueryWrapper<T>.() -> Unit): LambdaQueryWrapper<T> {
    return QueryWrapper<T>().lambda().apply {
        apply(action)
    }
}

/**https://mp.baomidou.com/guide/wrapper.html#updatewrapper*/
fun <T> updateWrapper(action: UpdateWrapper<T>.() -> Unit): UpdateWrapper<T> {
    return UpdateWrapper<T>().apply {
        //set()
        //setSql()
        //lambda()
        apply(action)
    }
}

fun <T> updateLambdaWrapper(action: LambdaUpdateWrapper<T>.() -> Unit): LambdaUpdateWrapper<T> {
    return UpdateWrapper<T>().lambda().apply {
        apply(action)
    }
}

/**https://www.cnblogs.com/l-y-h/p/12859477.html*/
fun AbstractWrapper<*, String, *>.deleteFlag(delete: Boolean? = false) {
    when {
        //查询所有
        delete == null -> Unit
        //查询被删除的
        delete -> eq("deleteFlag", 1)
        //查询没被删除的
        else -> ne("deleteFlag", 1)
    }
}

/**UserName 转换成 user_name
 * [com.gitee.sunchenbin.mybatis.actable.utils.ColumnUtils.getTableName]*/
fun lowerName(value: String) = CaseFormat.LOWER_CAMEL.to(
    CaseFormat.LOWER_UNDERSCORE,
    value.replace(ColumnUtils.SQL_ESCAPE_CHARACTER, "")
).lowercase()

fun String.toLowerName() = lowerName(this)

/**user_name 转换成 UserName*/
fun lowerCamel(value: String) = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, value)

fun String.toLowerCamel() = lowerCamel(this)

/**转换成安全的sql语句, 防止sql注入*/
fun String.toSafeSql() = replace("\\", "\\\\")
    .replace("'", "\\'")
    .replace("-", "\\-")

/**获取class对应的表名*/
fun KClass<*>.tableName() = ColumnUtils.getTableName(this.java)
fun Class<*>.tableName() = ColumnUtils.getTableName(this)

fun KProperty<*>.columnName() = name.toLowerName()

/**获取对象结构的主键属性名*/
fun Any.keyName(def: String = "id") = FieldUtils.getKeyField(this)?.name?.toLowerName() ?: def

/**获取对象结构的主键属性
 * [com.gitee.sunchenbin.mybatis.actable.annotation.IsKey]
 * [javax.persistence.Id]
 * [com.baomidou.mybatisplus.annotation.TableId]
 * */
fun Any.keyField() = FieldUtils.getKeyField(this)
fun Any.keyValue() = FieldUtils.getKeyField(this)?.get(this)

/**清空一下不需要传递过来的字段*/
fun Any.clearTableField(newTable: Boolean = true) {
    if (this is BaseAuditTable) {
        if (newTable) {
            id = null
            deleteFlag = BaseAuditTable.NO_DELETE
        }
        createdAt = null
        updatedAt = null
        createdBy = null
        updatedBy = null
    } else {
        if (newTable) {
            setMember("id", null)
            setMember("deleteFlag", BaseAuditTable.NO_DELETE)
        }
        setMember("createdAt", null)
        setMember("updatedAt", null)
        setMember("createdBy", null)
        setMember("updatedBy", null)
    }
}

inline fun <reified T : BaseAuditTable> Any.toTable(newTable: Boolean = false, dsl: T.() -> Unit = {}): T {
    return this.toObj {
        if (newTable) {
            id = null
            deleteFlag = BaseAuditTable.NO_DELETE
        }
        createdAt = null
        updatedAt = null
        createdBy = null
        updatedBy = null
        dsl()
    }
}