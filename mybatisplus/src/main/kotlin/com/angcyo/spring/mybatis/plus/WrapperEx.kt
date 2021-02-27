package com.angcyo.spring.mybatis.plus

import com.baomidou.mybatisplus.core.conditions.AbstractWrapper
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper

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