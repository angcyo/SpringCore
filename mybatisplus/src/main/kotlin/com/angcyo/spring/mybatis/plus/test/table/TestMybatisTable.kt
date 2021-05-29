package com.angcyo.spring.mybatis.plus.test.table

import com.angcyo.spring.mybatis.plus.table.BaseAuditTable
import com.baomidou.mybatisplus.annotation.TableName
import com.gitee.sunchenbin.mybatis.actable.annotation.Column
import com.gitee.sunchenbin.mybatis.actable.annotation.TableComment

/**
 * 如果直接使用[com.gitee.sunchenbin.mybatis.actable.annotation.Table]注释, 那么mybatis无法识别表名
 * 所以需要使用[com.baomidou.mybatisplus.annotation.TableName]告诉mybatis表名
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/02/25
 */
@TableName("test_mybatis_table")
@TableComment("测试表")
class TestMybatisTable : BaseAuditTable() {
    @Column
    var data: String? = null

    @Column
    var message: String? = null

    @Column
    var num: Int = 0
}