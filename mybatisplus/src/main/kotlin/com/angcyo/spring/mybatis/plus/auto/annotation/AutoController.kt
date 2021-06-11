package com.angcyo.spring.mybatis.plus.auto.annotation

import com.angcyo.spring.mybatis.plus.auto.PlaceholderClass
import kotlin.reflect.KClass

/**
 * 自动控制器的一些参数配置
 * [com.angcyo.spring.mybatis.plus.auto.BaseAutoController]
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/06/08
 */

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Deprecated("test")
annotation class AutoController(

    /**列表返回值的数据类型, 默认为表结构*/
    val returnListClass: KClass<*> = PlaceholderClass::class,

    /**查询参数的数据类型*/
    val queryParamClass: KClass<*> = PlaceholderClass::class
)
