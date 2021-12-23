package com.angcyo.spring.mybatis.plus.auto.annotation

import com.angcyo.spring.mybatis.plus.auto.AutoType

/**
 * 自动查询的Where条件生成规则
 *
 * [com.angcyo.spring.mybatis.plus.auto.annotation.WhereEnum]
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/05/28
 */

@Target(AnnotationTarget.TYPE_PARAMETER) //VM将在运行期也保留注释，因此可以通过反射机制读取注解的信息
@Retention(AnnotationRetention.RUNTIME) //将此注解包含在javadoc中
@MustBeDocumented
annotation class Query(

    /**查询的类型归类*/
    val type: AutoType = AutoType.QUERY,

    /**需要进行的比较条件*/
    val where: WhereEnum = WhereEnum.eq,

    /**可以指定对应的查询列名,
     * 如果不指定为空, 则使用属性名当做查询的列表*/
    val column: String = "",

    /**当值为null时, 是否要忽略拼接到sql*/
    val ignoreNull: Boolean = true,

    /**查询的时候, 是否检查空值, 如果为空则报错[nullError]*/
    val checkNull: Boolean = false,

    /**空异常时的错误提示*/
    val nullError: String = "",

    /**保存时, 数据已存在是否需要忽略提示[existError]
     * 有错误时, 但是不提示错误.*/
    val ignoreExistError: Boolean = false,

    /**
     * 保存时, 数据已存在的提示
     * 数据已存在时的错误提示
     *[com.angcyo.spring.mybatis.plus.auto.annotation.Query.nullError]
     */
    val existError: String = "",

    /**保存时, 数据的默认值
     * 当当前属性为null时, 需要设置的默认值, 暂且只支持简单数据类型*/
    val defaultValue: String = "",

    /**所在组的名称, 一个字段支持在多个组中*/
    val groups: Array<String> = ["default"]
)
