package com.angcyo.spring.mybatis.plus.auto.annotation

/**
 * 自动查询的Where条件生成规则
 *
 * [com.angcyo.spring.mybatis.plus.auto.annotation.WhereEnum]
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/05/28
 */

@Target(AnnotationTarget.FIELD) //VM将在运行期也保留注释，因此可以通过反射机制读取注解的信息
@Retention(AnnotationRetention.RUNTIME) //将此注解包含在javadoc中
@MustBeDocumented
annotation class AutoQuery(

    /**需要进行的比较条件*/
    val value: WhereEnum = WhereEnum.eq,

    /**可以指定对应的查询列名,
     * 如果不指定为空, 则使用属性名当做查询的列表*/
    val column: String = "",

    /**是否检查空值*/
    val checkNull: Boolean = false,

    /**空异常时的错误提示*/
    val nullError: String = ""
)
