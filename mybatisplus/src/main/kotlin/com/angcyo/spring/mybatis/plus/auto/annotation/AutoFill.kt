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

@Target(AnnotationTarget.FIELD, AnnotationTarget.CLASS, AnnotationTarget.TYPE)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class AutoFill(

    /**描述信息*/
    val des: String = "",

    /**使用Spring表达式语言（简称SpEL）解析*/
    val spEL: String = "",

    //<editor-fold desc="数据来源的服务">

    /**需要访问的服务类名
     * 调用的方法[com.angcyo.spring.mybatis.plus.auto.IBaseAutoMybatisService.listOf]*/
    val service: KClass<*> = PlaceholderAutoMybatisService::class,

    /**需要访问的服务全路径类名*/
    val serviceName: String = "",

    /**需要调用的服务方法名
     * 默认list查询, 根据参数自动选择以下方法
     * [com.angcyo.spring.mybatis.plus.auto.IBaseAutoMybatisService.listOf]
     * [com.angcyo.spring.mybatis.plus.auto.IBaseAutoMybatisService.autoList]
     * */
    val serviceMethod: String = "",

    /**从哪个属性中获取方法的参数
     * 为空则不传递参数
     * 支持[|]分割多个参数
     * 支持[obj.id]对象引用*/
    val methodParamField: String = "",

    //</editor-fold desc="数据来源的服务">

    //<editor-fold desc="查询的条件">

    /**
     * 从哪个属性名中获取查询的参数,需要传给服务的查询参数
     * 如果为空不指定, 则会使用[属性名Query]的字段.比如:nameQuery
     * 支持.操作, 比如[obj.id], 则会获取[obj]对象的[id]属性的值
     * */
    val queryParamField: String = "",

    /**简单的列查询
     * 查询列, 值就是注解属性的值
     * 指定需要查询的目标表的列*/
    val queryColumn: String = "",

    //</editor-fold desc="查询的条件">

    //<editor-fold desc="查询后的处理">

    /**查询到结果后,需要从结果对象的那个属性值赋值给当前注解属性
     * 如果为空,就是整个对象
     * 默认是id*/
    val targetField: String = "id",

    /**当查询到的数据为空或null时, 返回错误给调用者*/
    val errorOnNull: Boolean = true,

    //</editor-fold desc="查询后的处理">

    //val where: AutoWhere
)
