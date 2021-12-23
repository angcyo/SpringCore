package com.angcyo.spring.mybatis.plus.auto.core

import java.util.*

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/12/23
 */
class GroupPatternParse {

    private lateinit var result: GroupPatternExpression

    fun parse(group: String): GroupPatternExpression {
        result = GroupPatternExpression()

        stack.push(result)

        for (char in group) {
            parseChar(char)
        }

        //结束后的检查
        if (groupNameBuilder.isNotEmpty()) {
            stack.lastOrNull()?.apply {
                groupList.add(groupNameBuilder.toString())
            }
            groupNameBuilder.clear()
        }

        return result
    }

    //操作堆
    private var stack: Stack<GroupPatternExpression> = Stack()

    //分组名构建
    private val groupNameBuilder = StringBuilder()

    private fun parseChar(char: Char) {
        when (char) {
            ' ' -> Unit
            GroupPatternExpression.OR -> {
                stack.lastOrNull()?.apply {
                    op = GroupPatternExpression.OR
                    if (groupNameBuilder.isNotEmpty()) {
                        groupList.add(groupNameBuilder.toString())
                    }
                }
                groupNameBuilder.clear()
            }
            GroupPatternExpression.AND -> {
                stack.lastOrNull()?.apply {
                    op = GroupPatternExpression.AND
                    if (groupNameBuilder.isNotEmpty()) {
                        groupList.add(groupNameBuilder.toString())
                    }
                }
                groupNameBuilder.clear()
            }
            '(' -> {
                val expression = GroupPatternExpression()
                stack.lastOrNull()?.apply {
                    expressionList.add(expression)
                }
                stack.push(expression)
                groupNameBuilder.clear()
            }
            ')' -> {
                stack.lastOrNull()?.apply {
                    if (groupNameBuilder.isNotEmpty()) {
                        groupList.add(groupNameBuilder.toString())
                    }
                }
                stack.pop()
                groupNameBuilder.clear()
            }
            else -> groupNameBuilder.append(char)
        }
    }
}