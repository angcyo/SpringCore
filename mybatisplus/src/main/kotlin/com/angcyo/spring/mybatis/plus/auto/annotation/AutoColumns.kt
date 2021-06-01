package com.angcyo.spring.mybatis.plus.auto.annotation

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/05/28
 */

/**被标识的字段, 对应的Value, 标识需要选择的列, 多个用;分割*/
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class AutoColumns
