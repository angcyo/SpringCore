package com.angcyo.spring.base

import com.angcyo.spring.util.L
import com.angcyo.spring.util.copyTo
import org.springframework.aop.framework.AopContext
import java.util.*


/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/11/04
 */

fun threadName() = Thread.currentThread().name

/**是有具有[Class]*/
fun haveClass(name: String): Boolean {
    return try {
        Class.forName(name)
        true
    } catch (e: Exception) {
        false
    }
}

/**将当前对象转换成新对象[T]*/
inline fun <reified T> Any.toObj(dsl: T.() -> Unit = {}): T {
    val any = this
    val returnClass = T::class.java

    return if (returnClass.isAssignableFrom(any.javaClass)) {
        any as T
    } else {
        val newAny = returnClass.newInstance()
        any.copyTo(newAny as Any)
        newAny
    }.apply(dsl)
}

inline fun <reified T> List<*>.toObjList(dsl: T.(index: Int) -> Unit = {}): List<T> {
    val result = mutableListOf<T>()
    forEachIndexed { index, any ->
        any?.toObj<T> {
            this.dsl(index)
        }?.apply {
            result.add(this)
        }
    }
    return result
}

fun classOf(name: String): Class<*>? {
    return try {
        Class.forName(name)
    } catch (e: Exception) {
        null
    }
}

fun <T> Class<T>.logName() = if (L.isDebug) {
    name
} else {
    simpleName
}


/**
 * 根据Bean的名字, 扩展获取Bean对象
 * */
inline fun <reified T> String.bean() = Base.getBean<T>(this)

/**
 * 根据Class, 扩展获取Bean对象
 * */
fun <T> Class<T>.bean() = Base.getBean(this)

fun <T> beanOf(beanName: String): T? = Base.getBean<T>(beanName)

fun <T> beanOf(type: Class<T>): T = Base.getBean(type)

inline fun <reified T> beanOf(): T = Base.getBean(T::class.java)

/**获取[application.properties]中, 配置的值*/
fun String.propertyValue() = Base.applicationContext.environment.getProperty(this)

/**获取暴露的代理对象
 * 需要开启:[@EnableAspectJAutoProxy(exposeProxy = true)]*/
inline fun <reified Obj> proxyObj(): Obj? = AopContext.currentProxy() as? Obj?