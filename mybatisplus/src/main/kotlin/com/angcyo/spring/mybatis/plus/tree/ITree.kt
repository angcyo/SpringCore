package com.angcyo.spring.mybatis.plus.tree

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/06/16
 */
interface ITree<T : IBaseTree> : IBaseTree {

    /**子节点集合*/
    var childList: List<T>?
}