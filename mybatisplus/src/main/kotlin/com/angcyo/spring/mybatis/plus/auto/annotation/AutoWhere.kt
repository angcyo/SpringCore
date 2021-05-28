package com.angcyo.spring.mybatis.plus.auto.annotation

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/05/28
 */

@Target(AnnotationTarget.FIELD) //VM将在运行期也保留注释，因此可以通过反射机制读取注解的信息
@Retention(AnnotationRetention.RUNTIME) //将此注解包含在javadoc中
@MustBeDocumented
annotation class AutoWhere(
    /**需要进行的比较条件*/
    val value: Where = Where.eq
)
