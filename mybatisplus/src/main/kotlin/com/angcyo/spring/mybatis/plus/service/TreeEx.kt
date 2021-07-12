package com.angcyo.spring.mybatis.plus.service

import com.angcyo.spring.mybatis.plus.auto.getMember
import com.angcyo.spring.mybatis.plus.auto.setMember
import com.angcyo.spring.mybatis.plus.keyValue
import com.angcyo.spring.mybatis.plus.tree.ITree
import com.angcyo.spring.mybatis.plus.tree.isTopId

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/06/22
 */

/**将集合打包成树结构*/
fun <T : ITree<T>> List<T>.buildTree(needNoParentChild: Boolean = true): List<T> {
    return buildTree(needNoParentChild, getParentId = {
        it.parentId
    }, getParentIds = {
        it.parentIds
    }, setChildList = { node, childList ->
        node.childList = childList
    })
}

/**通过反射, 获取或者设置对应数结构的属性值*/
fun <T> List<T>.buildTree(
    needNoParentChild: Boolean = false,
    parentIdField: String,
    parentIdsField: String,
    setChildListField: String
): List<T> {
    return buildTree(needNoParentChild, getParentId = {
        it.getMember(parentIdField) as? Long
    }, getParentIds = {
        it.getMember(parentIdsField) as? String
    }, setChildList = { node, childList ->
        node.setMember(setChildListField, childList)
    })
}

/**将集合打包成树结构
 * [needNoParentChild] 是否需要返回无头的child list
 * [getParentId] 返回节点的父id
 * [getParentIds] 返回节点的父id集合 ,x, 的格式
 * */
fun <T> List<T>.buildTree(
    needNoParentChild: Boolean = false,
    getParentId: (node: T) -> Long?,
    getParentIds: (node: T) -> String?,
    setChildList: (node: T, childList: List<T>) -> Unit
): List<T> {

    //顶点节点
    val topList = mutableListOf<T>()
    //根据key, 存储节点
    val parentMap = hashMapOf<String, T>()
    //根据key, 存储子节点
    val childListMap = hashMapOf<String, MutableList<T>>()

    //具有parent的子节点map的key值列表. 剩下的key, 对应的数据就是无头的child list
    val haveParentChildKeyList = mutableListOf<String>()

    this.forEach { node ->
        val key = getParentIds(node) //node.parentIds
        if (!key.isNullOrEmpty()) {
            //根据key 存储节点
            val currentNode = parentMap[key] ?: node
            parentMap[key] = currentNode

            //初始化子节点存储容器
            val childList = childListMap[key] ?: mutableListOf()
            childListMap[key] = childList

            //设置子节点
            setChildList(currentNode, childList)

            //有头的key
            haveParentChildKeyList.add(key)

            if (getParentId(node).isTopId()) {
                //是顶点

                //初始化顶点数据
                val parentNode = parentMap[key] ?: node
                parentMap[key] = parentNode

                //顶点的子节点集合
                val parentChildList = childListMap[key] ?: mutableListOf()
                childListMap[key] = parentChildList
            } else {
                //非顶点
                val parentKey = key.substring(0, key.length - "${node?.keyValue()},".length)

                //拿到数据父节点的子集合列表
                val parentChildList = childListMap[parentKey] ?: mutableListOf()
                childListMap[parentKey] = parentChildList

                //加入到子集合中
                parentChildList.add(node)
            }
        }

        if (getParentId(node).isTopId()) {
            topList.add(node)
        }
    }

    //--无头的child list
    if (needNoParentChild) {
        childListMap.keys.forEach { key ->
            if (haveParentChildKeyList.contains(key)) {
                //有parent
            } else {
                //无parent 的child list
                val childList = childListMap[key] ?: emptyList()
                topList.addAll(childList)
            }
        }
    }

    return topList
}