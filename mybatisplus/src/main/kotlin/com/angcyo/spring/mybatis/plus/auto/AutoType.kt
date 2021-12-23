package com.angcyo.spring.mybatis.plus.auto

/**
 * 当前的字段, 需要归类到那个查询类型中
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/12/23
 */
enum class AutoType {

    /**保存查询中使用*/
    SAVE,

    /**删除查询中使用*/
    DELETE,

    /**查询中使用*/
    QUERY,

    /**更新查询中使用*/
    UPDATE,

    /**移除查询中使用*/
    REMOVE,

    /**重置查询中使用*/
    RESET,
}