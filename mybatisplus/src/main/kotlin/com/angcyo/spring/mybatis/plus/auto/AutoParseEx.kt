package com.angcyo.spring.mybatis.plus.auto

import com.angcyo.spring.mybatis.plus.auto.extension.AutoParseException
import com.angcyo.spring.mybatis.plus.auto.param.BaseAutoQueryParam
import com.angcyo.spring.mybatis.plus.columnName
import com.angcyo.spring.mybatis.plus.table.BaseAuditTable
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.baomidou.mybatisplus.core.toolkit.ReflectionKit
import org.springframework.util.ReflectionUtils
import java.lang.reflect.AnnotatedElement
import java.lang.reflect.Field
import java.lang.reflect.Method

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/05/29
 */

inline fun Class<*>.eachField(each: (Field) -> Unit) {
    for (field in ReflectionKit.getFieldList(this)) {
        each(field)
    }
}

/**快速获取注解类*/
fun <Auto : Annotation> AnnotatedElement.annotation(
    annotationClass: Class<Auto>,
    dsl: Auto.() -> Unit = {}
): Auto? {
    val auto = getDeclaredAnnotation(annotationClass)
    return if (auto != null) {
        //isAccessible = true
        if (this is Field) {
            ReflectionUtils.makeAccessible(this)
        }
        auto.dsl()
        auto
    } else {
        null
    }
}

/**快速获取注解类*/
inline fun <reified Auto : Annotation> AnnotatedElement.annotation(dsl: Auto.() -> Unit = {}): Auto? {
    val auto = getDeclaredAnnotation(Auto::class.java)
    return if (auto != null) {
        //isAccessible = true
        if (this is Field) {
            ReflectionUtils.makeAccessible(this)
        }
        auto.dsl()
        auto
    } else {
        null
    }
}

/**枚举对象中所有包含指定注解的字段*/
inline fun <reified Auto : Annotation> Any.eachAnnotation(dsl: Auto.(field: Field) -> Unit) {
    ReflectionKit.getFieldList(this.javaClass).forEach { field ->
        field.annotation<Auto> {
            this.dsl(field)
        }
    }
}

/**获取对象中所有指定注解的字段*/
inline fun <reified Auto : Annotation> Any.annotations(checkNullValue: Boolean = false): List<Field> {
    val result = mutableListOf<Field>()
    for (field in ReflectionKit.getFieldList(this.javaClass)) {
        val annotation = field.annotation<Auto>()
        if (annotation != null) {
            if (checkNullValue) {
                if (field.get(this) != null) {
                    result.add(field)
                }
            } else {
                result.add(field)
            }
        }
    }
    return result
}

/**是否有指定的注解*/
inline fun <reified Auto : Annotation> Any.haveAnnotation(checkNullValue: Boolean = false): Boolean {
    var have = false
    for (field in ReflectionKit.getFieldList(this.javaClass)) {
        val annotation = field.annotation<Auto>()
        if (annotation != null) {
            have = if (checkNullValue) {
                field.get(this) != null
            } else {
                true
            }
        }
        if (have) {
            break
        }
    }
    return have
}

/**反射获取去对象的方法*/
fun Any.getMethod(methodName: String, clz: Class<*> = javaClass): Method? {
    var searchType: Class<*>? = clz
    while (searchType != null) {
        val methods = if (searchType.isInterface) searchType.methods else ReflectionUtils.getDeclaredMethods(searchType)
        for (method in methods) {
            if (methodName == method.name) {
                return method
            }
        }
        searchType = searchType.superclass
    }
    return null
}

/**反射调用对象的方法*/
fun Any.invokeMethod(methodName: String, vararg args: Any?): Any? = invokeMethodClass(methodName, javaClass, *args)

fun Any.invokeMethodClass(methodName: String, clz: Class<*> = javaClass, vararg args: Any?): Any? =
    ReflectionUtils.invokeMethod(getMethod(methodName, clz)!!, this, *args)

/**
 * 从一个对象中, 获取指定的成员对象
 */
fun Any?.getMember(member: String): Any? {
    return ReflectionKit.getFieldValue(this, member)
}

/**设置成员的值*/
fun Any?.setMember(member: String, value: Any?): Boolean {
    val cls: Class<*> = this?.javaClass ?: return false
    val fieldMaps = ReflectionKit.getFieldMap(cls)
    return try {
        val field = fieldMaps[member]
        field!!.isAccessible = true
        field.set(this, value)
        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}

fun Any.isList() = isClass(List::class.java)

fun <T> Any.isClass(cls: Class<T>) = if (this is Field) {
    cls.isAssignableFrom(type)
} else {
    cls.isAssignableFrom(javaClass)
}

inline fun parseError(message: Any, cause: Throwable? = null): Nothing =
    throw AutoParseException(message.toString(), cause)

/**将对象集合, 转换成对象某一个属性[property]的集合*/
fun <T> List<Any>.toProList(property: String): List<T> {
    val result = mutableListOf<T>()
    forEach {
        result.add(it.getMember(property) as T)
    }
    return result
}

/**未删除的数据*/
fun <Table> QueryWrapper<Table>.noDelete(table: Class<Table>): QueryWrapper<Table> {
    if (BaseAuditTable::class.java.isAssignableFrom(table)) {
        eq(BaseAuditTable::deleteFlag.columnName(), 0)
    }
    return this
}

//<editor-fold desc="排序">

/**处理排序字段
 * [com.angcyo.spring.mybatis.plus.auto.AutoParse._handleOrder]*/
fun <Table> QueryWrapper<Table>.sort(param: Any?): QueryWrapper<Table> {
    if (param != null && param is BaseAutoQueryParam) {
        val desc = param.desc
        if (!desc.isNullOrEmpty()) {
            //降序
            sortDesc(desc.split(BaseAutoQueryParam.SPLIT))
        }

        val asc = param.asc
        if (!asc.isNullOrEmpty()) {
            //升序
            sortAsc(asc.split(BaseAutoQueryParam.SPLIT))
        }
    }
    return this
}

/**降序, 从大到小*/
fun <Table> QueryWrapper<Table>.sortDesc(vararg columns: String): QueryWrapper<Table> {
    return sortDesc(columns.toList())
}

fun <Table> QueryWrapper<Table>.sortDesc(columnList: List<String>): QueryWrapper<Table> {
    val size = columnList.size
    if (size == 1) {
        orderByDesc(columnList[0])
    } else if (size > 1) {
        val other = columnList.slice(1 until size)
        orderByDesc(columnList[0], *other.toTypedArray())
    }
    return this
}

/**升序, 从小到大*/
fun <Table> QueryWrapper<Table>.sortAsc(vararg columns: String): QueryWrapper<Table> {
    return sortAsc(columns.toList())
}

fun <Table> QueryWrapper<Table>.sortAsc(columnList: List<String>): QueryWrapper<Table> {
    val size = columnList.size
    if (size == 1) {
        orderByAsc(columnList[0])
    } else if (size > 1) {
        val other = columnList.slice(1 until size)
        orderByAsc(columnList[0], *other.toTypedArray())
    }
    return this
}


//</editor-fold desc="排序">

//<editor-fold desc="分组">

fun <Table> QueryWrapper<Table>.group(vararg columns: String): QueryWrapper<Table> {
    return group(columns.toList())
}

fun <Table> QueryWrapper<Table>.group(columnList: List<String>): QueryWrapper<Table> {
    val size = columnList.size
    if (size == 1) {
        groupBy(columnList[0])
    } else if (size > 1) {
        val other = columnList.slice(1 until size)
        groupBy(columnList[0], *other.toTypedArray())
    }
    return this
}

//</editor-fold desc="分组">