package com.angcyo.spring.mybatis.plus.auto.core

/**
 * 分组模板表达式
 *
 * (g1 & g2) | (g1 & g3)
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/12/23
 */
class GroupPatternExpression {

    companion object {
        //表达式
        const val OR = '|'
        const val AND = '&'
    }

    /**表达式
     * [OR]
     * [AND]
     * */
    var op: Char? = null

    /**这一组中的有效分组名*/
    val groupList = mutableListOf<String>()

    /**其他表达式*/
    var expressionList = mutableListOf<GroupPatternExpression>()
}