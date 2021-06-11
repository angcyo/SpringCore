package com.angcyo.spring.mybatis.plus.auto.annotation

/**
 * 自动解析查询的一些配置
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/06/01
 */

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class AutoQueryConfig(
    /**当调用自动更新保存方法, 更新失败时, 自动转为保存操作
     * [com.angcyo.spring.mybatis.plus.auto.IBaseAutoMybatisService.autoSaveOrUpdate]*/
    val updateFailToSave: Boolean = false,
)
