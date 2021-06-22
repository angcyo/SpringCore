package com.angcyo.spring.mybatis.plus.service

import com.angcyo.spring.mybatis.plus.keyValue
import com.angcyo.spring.mybatis.plus.tree.ITree
import com.angcyo.spring.mybatis.plus.tree.isTopId

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/06/22
 */

/**将集合打包成树结构*/
fun <T : ITree<T>> List<T>.buildTree(): List<T> {
    //顶点节点
    val topList = mutableListOf<T>()
    //根据key, 存储节点
    val parentMap = hashMapOf<String, T>()
    //根据key, 存储子节点
    val childListMap = hashMapOf<String, MutableList<T>>()

    //具有parent的子节点map的key值列表. 剩下的key, 对应的数据就是无头的child list
    val haveParentChildKeyList = mutableListOf<String>()

    this.forEach { node ->
        val key = node.parentIds
        if (!key.isNullOrBlank()) {
            //根据key 存储节点
            val currentNode = parentMap[key] ?: node
            parentMap[key] = currentNode

            //初始化子节点存储容器
            val childList = childListMap[key] ?: mutableListOf()
            childListMap[key] = childList

            //非顶点
            if (!node.parentId.isTopId()) {
                //子节点
                val parentKey = key.substring(0, key.length - "${node.keyValue()},".length)

                val parentNode = parentMap[parentKey] ?: node
                parentMap[parentKey] = parentNode

                val parentChildList = childListMap[parentKey] ?: mutableListOf()
                childListMap[parentKey] = parentChildList

                //节点属于那个parent
                parentChildList.add(node)
                parentNode.childList = parentChildList
                haveParentChildKeyList.add(parentKey)
            }
        }

        if (node.parentId.isTopId()) {
            topList.add(node)
        }
    }

    //--
    childListMap.keys.forEach { key ->
        if (haveParentChildKeyList.contains(key)) {
            //有parent
        } else {
            //无parent 的child list
            val childList = childListMap[key] ?: emptyList()
            topList.addAll(childList)
        }
    }

    return topList
}