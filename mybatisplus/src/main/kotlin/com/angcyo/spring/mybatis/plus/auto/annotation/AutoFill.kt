package com.angcyo.spring.mybatis.plus.auto.annotation

import com.angcyo.spring.mybatis.plus.auto.PlaceholderAutoMybatisService
import kotlin.reflect.KClass

/**
 * 自动填充被注解注释的字段属性
 *
 * 自动从另一个[com.angcyo.spring.mybatis.plus.auto.IBaseAutoMybatisService]服务中,
 * 查询出结果, 赋值给当前字段.
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/06/01
 */

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class AutoFill(

    /**需要访问的服务类名
     * 调用的方法[com.angcyo.spring.mybatis.plus.auto.IBaseAutoMybatisService.listOf]*/
    val service: KClass<*> = PlaceholderAutoMybatisService::class,

    /**需要访问的服务全路径类名*/
    val serviceName: String = "",

    /**
     * 从哪个属性名中获取查询的参数,需要传给服务的查询参数
     * 默认是注解当前[属性名Query]字符串
     * 比如:nameQuery*/
    val query: String = "",

    /**简单的列查询
     * 查询列, 值就是注解属性的值*/
    val queryColumn: String = "",

    /**获取查询列值的属性key,
     * 默认是注解属性的值*/
    val queryValue: String = "",

    /**查询到结果后,需要从结果对象的那个属性值赋值给当前注解属性
     * 默认是id*/
    val field: String = "id",

    /**当查询到的数据为空或null时, 返回错误给调用者*/
    val errorOnNull: Boolean = true,
)
