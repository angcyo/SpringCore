package com.angcyo.spring.mybatis.plus.auto

/**
 * 当前的字段, 需要归类到那个查询类型中
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/12/23
 */
enum class AutoType {

    /**保存查询中使用,
     * 如果查询出来的数据大于0时, 标识数据已存在. 异常处理. 终止保存
     *
     * 当指定的sql查询, 能查询出数据时, 数据已存在, 不保存.
     * */
    SAVE,

    /**删除查询中使用
     * 必须指定主键.
     * 指定主键后, sql查询出来的数据都删除, 软删除.
     * */
    DELETE,

    /**查询中使用
     * 根据指定的查询条件, 查询数据并返回
     *
     * 拼接对应的sql, 查询并返回数据.
     * */
    QUERY,

    /**更新查询中使用
     * 必须指定主键.
     * 指定主键后, sql查询出来的数据都进行更新.
     *
     * 当指定的sql查询, 能查询出数据时, 才更新数据
     * */
    UPDATE,

    /**更新查询时, 检查数据是否已存在, 如果存在终止更新
     *
     * 当[UPDATE]查询通过后, 继续判断是否可以保存成新数据的检查
     * */
    UPDATE_CHECK,

    /**移除查询中使用
     * 必须指定主键.
     * 指定主键后, sql查询出来的数据都删除, 硬删除.*/
    REMOVE,

    /**重置查询中使用*/
    RESET,
}