package com.angcyo.spring.mybatis.plus

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
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

/**https://mp.baomidou.com/guide/wrapper.html#updatewrapper*/
fun <T> updateWrapper(action: UpdateWrapper<T>.() -> Unit): UpdateWrapper<T> {
    return UpdateWrapper<T>().apply {
        //set()
        //setSql()
        //lambda()
        apply(action)
    }
}