package com.angcyo.spring.mybatis.plus.auto.annotation

/**
 * 根据注解标识的字段, 查询出数据集合, 然后对记录进行 删除/新增/保存 操作
 * [com.angcyo.spring.mybatis.plus.auto.IBaseAutoMybatisService.autoReset]
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/06/02
 */

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class AutoResetBy