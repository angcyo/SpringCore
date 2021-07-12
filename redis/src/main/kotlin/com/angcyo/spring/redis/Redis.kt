package com.angcyo.spring.redis

import com.angcyo.spring.base.servlet.param
import com.angcyo.spring.base.servlet.request
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit
import javax.annotation.Resource


/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/11/04
 */

@Component
class Redis {

    @Resource
    lateinit var redisTemplate: RedisTemplate<String, Any?>

    /**
     * 指定缓存失效时间
     * @param key 键
     * @param time 时间(秒)
     * @return
     */
    fun expire(key: String, time: Long): Boolean {
        return try {
            if (time > 0) {
                redisTemplate.expire(key, time, TimeUnit.SECONDS)
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * 根据key 获取过期时间
     * @param key 键 不能为null
     * @return 时间(秒) 返回0代表为永久有效
     */
    fun getExpire(key: String): Long {
        return redisTemplate.getExpire(key, TimeUnit.SECONDS)
    }

    /**
     * 判断key是否存在
     * @param key 键
     * @return true 存在 false不存在
     */
    fun hasKey(key: String): Boolean {
        return try {
            redisTemplate.hasKey(key)
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * 删除缓存
     * @param keys 可以传一个值 或多个
     */
    fun del(vararg keys: String): Boolean {
        return if (keys.isNotEmpty()) {
            if (keys.size == 1) {
                redisTemplate.delete(keys[0])
            } else {
                redisTemplate.delete(keys.toList()) == 1L
            }
        } else {
            true
        }
    }

    //============================String=============================

    /**
     * 普通缓存获取
     * @param key 键
     * @return 值
     */
    operator fun get(key: String?): Any? {
        return if (key == null) null else redisTemplate.opsForValue()[key]
    }

    /**
     * 普通缓存放入
     * @param key 键
     * @param value 值
     * @return true成功 false失败
     */
    operator fun set(key: String, value: Any?): Boolean {
        return try {
            if (value == null) {
                del(key)
            } else {
                redisTemplate.opsForValue()[key] = value
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * 普通缓存放入并设置时间
     * @param key 键
     * @param value 值
     * @param time 时间(秒) time要大于0 如果time小于等于0 将设置无限期
     * @return true成功 false 失败
     */
    operator fun set(key: String, time: Long, value: Any?): Boolean {
        return try {
            if (time > 0) {
                if (value == null) {
                    del(key)
                } else {
                    redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS)
                }
            } else {
                set(key, value)
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * 递增
     * @param key 键
     * @param by 要增加几(大于0)
     * @return
     */
    fun incr(key: String, delta: Long): Long {
        if (delta < 0) {
            throw RuntimeException("递增因子必须大于0")
        }
        return redisTemplate.opsForValue().increment(key, delta) ?: -1
    }

    /**
     * 递减
     * @param key 键
     * @param by 要减少几(小于0)
     * @return
     */
    fun decr(key: String, delta: Long): Long {
        if (delta < 0) {
            throw RuntimeException("递减因子必须大于0")
        }
        return redisTemplate.opsForValue().increment(key, -delta) ?: -1
    }

    //================================Map=================================

    /**
     * HashGet
     * @param key 键 不能为null
     * @param item 项 不能为null
     * @return 值
     */
    fun hashGet(key: String, item: String): Any? {
        return redisTemplate.opsForHash<Any, Any>()[key, item]
    }

    /**
     * 获取hashKey对应的所有键值
     * @param key 键
     * @return 对应的多个键值
     */
    fun hashGet(key: String): Map<Any, Any> {
        return redisTemplate.opsForHash<Any, Any>().entries(key)
    }

    /**
     * HashSet
     * @param key 键
     * @param map 对应多个键值
     * @return true 成功 false 失败
     */
    fun hashSet(key: String, map: Map<String, Any>): Boolean {
        return try {
            redisTemplate.opsForHash<Any, Any>().putAll(key, map)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * HashSet 并设置时间
     * @param key 键
     * @param map 对应多个键值
     * @param time 时间(秒)
     * @return true成功 false失败
     */
    fun hashSet(key: String, map: Map<String, Any>, time: Long): Boolean {
        return try {
            redisTemplate.opsForHash<Any, Any>().putAll(key, map)
            if (time > 0) {
                expire(key, time)
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     * @param key 键
     * @param item 项
     * @param value 值
     * @return true 成功 false失败
     */
    fun hashSet(key: String, item: String, value: Any?): Boolean {
        return try {
            if (value == null) {
                del(key)
            } else {
                redisTemplate.opsForHash<Any, Any>().put(key, item, value)
                true
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     * @param key 键
     * @param item 项
     * @param value 值
     * @param time 时间(秒)  注意:如果已存在的hash表有时间,这里将会替换原有的时间
     * @return true 成功 false失败
     */
    fun hashSet(key: String, item: String, value: Any?, time: Long): Boolean {
        return try {
            if (value == null) {
                del(key)
            } else {
                redisTemplate.opsForHash<Any, Any>().put(key, item, value)
                if (time > 0) {
                    expire(key, time)
                }
                true
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * 删除hash表中的值
     * @param key 键 不能为null
     * @param item 项 可以使多个 不能为null
     */
    fun hashDel(key: String, vararg item: Any?) {
        redisTemplate.opsForHash<Any, Any>().delete(key, *item)
    }

    /**
     * 判断hash表中是否有该项的值
     * @param key 键 不能为null
     * @param item 项 不能为null
     * @return true 存在 false不存在
     */
    fun hashHasKey(key: String, item: String): Boolean {
        return redisTemplate.opsForHash<Any, Any>().hasKey(key, item)
    }

    /**
     * hash递增 如果不存在,就会创建一个 并把新增后的值返回
     * @param key 键
     * @param item 项
     * @param by 要增加几(大于0)
     * @return
     */
    fun hashIncr(key: String, item: String, by: Double): Double {
        return redisTemplate.opsForHash<Any, Any>().increment(key, item, by)
    }

    /**
     * hash递减
     * @param key 键
     * @param item 项
     * @param by 要减少记(小于0)
     * @return
     */
    fun hashDecr(key: String, item: String, by: Double): Double {
        return redisTemplate.opsForHash<Any, Any>().increment(key, item, -by)
    }

    //============================set=============================

    /**
     * 根据key获取Set中的所有值
     * @param key 键
     * @return
     */
    fun setGet(key: String): Set<Any?>? {
        return try {
            redisTemplate.opsForSet().members(key)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * 根据value从一个set中查询,是否存在
     * @param key 键
     * @param value 值
     * @return true 存在 false不存在
     */
    fun setHasKey(key: String, value: Any?): Boolean {
        return try {
            redisTemplate.opsForSet().isMember(key, value!!)!!
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * 将数据放入set缓存
     * @param key 键
     * @param values 值 可以是多个
     * @return 成功个数
     */
    fun setSet(key: String, vararg values: Any?): Long {
        return try {
            redisTemplate.opsForSet().add(key, *values)!!
        } catch (e: Exception) {
            e.printStackTrace()
            0
        }
    }

    /**
     * 将set数据放入缓存
     * @param key 键
     * @param time 时间(秒)
     * @param values 值 可以是多个
     * @return 成功个数
     */
    fun setSetAndTime(key: String, time: Long, vararg values: Any?): Long {
        return try {
            val count = redisTemplate.opsForSet().add(key, *values)
            if (time > 0) expire(key, time)
            count!!
        } catch (e: Exception) {
            e.printStackTrace()
            0
        }
    }

    /**
     * 获取set缓存的长度
     * @param key 键
     * @return
     */
    fun setGetSetSize(key: String): Long {
        return try {
            redisTemplate.opsForSet().size(key)!!
        } catch (e: Exception) {
            e.printStackTrace()
            0
        }
    }

    /**
     * 移除值为value的
     * @param key 键
     * @param values 值 可以是多个
     * @return 移除的个数
     */
    fun setRemove(key: String, vararg values: Any?): Long {
        return try {
            val count = redisTemplate.opsForSet().remove(key, *values)
            count!!
        } catch (e: Exception) {
            e.printStackTrace()
            0
        }
    }

    //===============================list=================================

    /**
     * 获取list缓存的内容
     * @param key 键
     * @param start 开始
     * @param end 结束  0 到 -1代表所有值
     * @return
     */
    fun getList(key: String, start: Long, end: Long): List<Any?>? {
        return try {
            redisTemplate.opsForList().range(key, start, end)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * 获取list缓存的长度
     * @param key 键
     * @return
     */
    fun getListSize(key: String): Long {
        return try {
            redisTemplate.opsForList().size(key) ?: 0
        } catch (e: Exception) {
            e.printStackTrace()
            0
        }
    }

    /**
     * 通过索引 获取list中的值
     * @param key 键
     * @param index 索引  index>=0时， 0 表头，1 第二个元素，依次类推；index<0时，-1，表尾，-2倒数第二个元素，依次类推
     * @return
     */
    fun getListIndex(key: String, index: Long): Any? {
        return try {
            redisTemplate.opsForList().index(key, index)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * 将list放入缓存
     * @param key 键
     * @param value 值
     * @param time 时间(秒)
     * @return
     */
    fun setList(key: String, value: Any): Boolean {
        return try {
            redisTemplate.opsForList().rightPush(key, value)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * 将list放入缓存
     * @param key 键
     * @param value 值
     * @param time 时间(秒)
     * @return
     */
    fun setList(key: String, value: Any, time: Long): Boolean {
        return try {
            redisTemplate.opsForList().rightPush(key, value)
            if (time > 0) expire(key, time)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * 将list放入缓存
     * @param key 键
     * @param value 值
     * @param time 时间(秒)
     * @return
     */
    fun setList(key: String, value: List<Any?>?): Boolean {
        return try {
            redisTemplate.opsForList().rightPushAll(key, value)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * 将list放入缓存
     * @param key 键
     * @param value 值
     * @param time 时间(秒)
     * @return
     */
    fun setList(key: String, value: List<Any?>?, time: Long): Boolean {
        return try {
            redisTemplate.opsForList().rightPushAll(key, value)
            if (time > 0) expire(key, time)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * 根据索引修改list中的某条数据
     * @param key 键
     * @param index 索引
     * @param value 值
     * @return
     */
    fun updateListIndex(key: String, index: Long, value: Any): Boolean {
        return try {
            redisTemplate.opsForList()[key, index] = value
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * 移除N个值为value
     * @param key 键
     * @param count 移除多少个
     * @param value 值
     * @return 移除的个数
     */
    fun listRemove(key: String, count: Long, value: Any): Long {
        return try {
            val remove = redisTemplate.opsForList().remove(key, count, value)
            remove ?: 0
        } catch (e: Exception) {
            e.printStackTrace()
            0
        }
    }

    //<editor-fold desc="cache">

    /**
     *  Supported glob-style patterns:
     *
     * - h?llo matches hello, hallo and hxllo
     * - h*llo matches hllo and heeeello
     * - h[ae]llo matches hello and hallo, but not hillo
     * - h[^e]llo matches hallo, hbllo, ... but not hello
     * - h[a-b]llo matches hallo and hbllo
     * Use \ to escape special characters if you want to match them verbatim.
     * https://redis.io/commands/keys
     * */
    fun keyList(pattern: String) = redisTemplate.keys(pattern).toList()

    /**移除匹配到key对应的缓存*/
    fun removeCache(pattern: String, all: Boolean = true) {
        val keyList = if (all) keyList("*${pattern}*") else listOf(pattern)
        redisTemplate.delete(keyList)
    }

    /**[removeCache]*/
    fun clearCache(pattern: String, all: Boolean = true) {
        removeCache(pattern, all)
    }

    /**
     * 快速设置 读取缓存
     * [key] 缓存的key
     * [time] 时间(秒) time要大于0 如果time小于等于0 将设置无限期
     * [update] 总是更新缓存, 否则值不一样时才更新
     * [refresh] 强制不使用缓存,并且更新缓存
     * [doValue] 缓存已有的值, 返回缓存数据
     * @return true成功 false 失败
     */
    fun <T> cache(
        key: String,
        time: Long = 1 * 60 * 60 /*1小时*/,
        update: Boolean = false,
        refresh: Boolean = request()?.param("Cache-Refresh") == "true",
        doValue: T?.() -> T
    ): T {
        val value: T? = if (refresh) null else get(key) as? T?
        val result = value.doValue()
        if (update || refresh || value == null || value != result) {
            set(key, time, result)
        }
        return result
    }

    //</editor-fold desc="cache">

}