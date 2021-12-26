package com.angcyo.spring.mybatis.plus.auto

/**
 * 自动查询的配置参数
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/06/02
 */
class QueryConfig {

    /**当调用自动更新保存方法, 更新失败时, 自动转为保存操作
     * [com.angcyo.spring.mybatis.plus.auto.IBaseAutoMybatisService.autoSaveOrUpdate]*/
    var updateFailToSave: Boolean = false

}