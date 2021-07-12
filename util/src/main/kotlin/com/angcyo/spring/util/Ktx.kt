package com.angcyo.spring.util

import org.springframework.beans.BeanUtils
import org.springframework.context.ConfigurableApplicationContext
import java.lang.Integer.min
import java.net.InetAddress
import java.net.UnknownHostException
import java.nio.charset.Charset
import java.util.*
import kotlin.random.Random.Default.nextInt


/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/11/04
 */

/**复制属性*/
fun Any?.copyTo(new: Any, vararg ignoreProperties: String): Any {
    if (this != null) {
        BeanUtils.copyProperties(this, new, *ignoreProperties)
    }
    return new
}


/**从文本的后面, 一个字符一个字符剔除, 并循环调用剩下的字符串¬
 * [sub]剩下的文本
 * [drop]被剔除的文本*/
inline fun CharSequence.eachDropLast(action: (sub: CharSequence, drop: CharSequence) -> Unit) {
    for (i in 0 until length) {
        val sub = subSequence(0, length - i)
        val drop = subSequence(length - i, length)
        action(sub, drop)
    }
}

/**从文本的前面, 一个字符一个字符剔除, 并循环调用剩下的字符串¬
 * [sub]剩下的文本
 * [drop]被剔除的文本*/
inline fun CharSequence.eachDropFirst(action: (sub: CharSequence, drop: CharSequence) -> Unit) {
    for (i in 0 until length) {
        val sub = subSequence(i, length)
        val drop = subSequence(0, i)
        action(sub, drop)
    }
}

/**限制字符允许的最大字符串
 * [dropEnd]  超出范围后, 丢掉后的字符串, 否则丢掉前面的字符串*/
fun CharSequence.maxLength(maxLength: Int, dropEnd: Boolean = true): CharSequence {
    return if (length > maxLength) {
        if (dropEnd) {
            subSequence(0, maxLength)
        } else {
            subSequence(length - maxLength, length)
        }
    } else {
        this
    }
}

/**host/url*/
fun String?.connectUrl(url: String?): String {
    val h = this?.trimEnd('/') ?: ""
    val u = url?.trimStart('/') ?: ""
    return "$h/$u"
}

fun CharSequence?.des(): String {
    return if (this.isNullOrEmpty()) {
        ""
    } else {
        "($this)"
    }
}

fun CharSequence?.des2(): String {
    return if (this.isNullOrEmpty()) {
        ""
    } else {
        "[$this]"
    }
}

/*----------------------------------------------------------------------------------*/

fun uuid() = UUID.randomUUID().toString()

fun ByteArray.string(charset: Charset = Charset.defaultCharset()) = String(this, charset)
fun ByteArray.string(charset: String?) = String(this, Charset.forName(charset ?: "UTF-8"))

/**一天对应多少毫秒 [86400_000]*/
val oneDay: Long get() = 1000L * oneDaySec

/**一天对应多少秒 [86400_000]*/
val oneDaySec: Long get() = 60 * 60 * 24L

fun Long.prettyByteSize() = PrettyMemoryUtil.prettyByteSize(this)

/*----------------------------------------------------------------------------------*/

fun Any?.str(): String {
    return if (this is String) {
        this
    } else {
        this.toString()
    }
}

/**如果为空, 则执行[action].
 * 原样返回*/
fun <T> T?.elseNull(action: () -> Unit = {}): T? {
    if (this == null) {
        action()
    }
    return this
}

/**复制对象*/
fun <T : Any> T.copyTo(obj: T, ignoreNullField: Boolean = false): T {
    if (ignoreNullField) {
        val ignorePropertiesList = mutableListOf<String>()
        val targetPds = BeanUtils.getPropertyDescriptors(this.javaClass)
        for (targetPd in targetPds) {
            if (targetPd.getValue(targetPd.name) == null) {
                ignorePropertiesList.add(targetPd.name)
            }
        }
        BeanUtils.copyProperties(this, obj, *ignorePropertiesList.toTypedArray())
    } else {
        BeanUtils.copyProperties(this, obj)
    }
    return obj
}

/*----------------------------------------------------------------------------------*/

fun Collection<*>.lastIndex() = size - 1

/**从当前列表中, 随机获取指定数量的数据*/
fun <T> List<T>.randomList(count: Int): List<T> {
    val indexMap = hashMapOf<Int, String>()
    val result = mutableListOf<T>()
    val list = this
    if (list.size <= count) {
        //原始数量不够时
        result.addAll(list)
    } else {
        while (indexMap.size < count) {
            val randomIndex = nextInt(0, list.size)
            if (!indexMap.containsKey(randomIndex)) {
                //随机获取
                indexMap[randomIndex] = ""
                result.add(list[randomIndex])
            }
        }
    }
    return result
}

/**从指定列表中, 随机读取指定数量数据, 直到列表无数据
 * [index] 当前随机数据集合中的索引*/
fun <T> List<T>.eachRandomList(count: Int, action: (index: Int, T) -> Unit) {
    val list = this.toMutableList()
    while (list.isNotEmpty()) {

        //随机获取数据
        val selectList = list.randomList(count)
        list.removeAll(selectList)

        for (i in 0 until min(count, selectList.size)) {
            selectList.getOrNull(i)?.let { item ->
                action(i, item)
            }
        }
    }
}

fun List<*>?.size() = this?.size ?: 0

/**集合是否包含元素[element]
 * 集合为空时返回[nullDef]的值*/
fun <T> List<T>?.containsOrNullDef(element: T?, nullDef: Boolean = true) = this?.contains(element) ?: nullDef

inline fun <T> Iterable<T>.have(predicate: (T) -> Boolean): Boolean {
    return find(predicate) != null
}

/**如果是负数, 则反向取值
 * 如果大于size, 则取模*/
fun <T> List<T>.getSafe(index: Int): T? {
    val newIndex = if (index < 0) {
        size + index
    } else {
        index
    }
    val size = size()
    if (newIndex >= size) {
        return getOrNull(newIndex % size)
    }
    return getOrNull(newIndex)
}

/*----------------------------------------------------------------------------------*/

fun <T> Optional<T>?.getOrNull(def: T? = null): T? {
    return if (this?.isPresent == true) {
        this.get()
    } else {
        def
    }
}

/**获取本机ip*/
fun getLocalHost(): String? {
    try {
        val address = InetAddress.getLocalHost()
        return address.hostAddress
    } catch (e: UnknownHostException) {
        e.printStackTrace()
    }
    return null
}

/**获取Spring服务端口*/
fun ConfigurableApplicationContext.getServerPort(): String? {
    return environment.getProperty("server.port")
}