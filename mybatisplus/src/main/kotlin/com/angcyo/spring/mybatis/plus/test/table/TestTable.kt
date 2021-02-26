package com.angcyo.spring.mybatis.plus.test.table

import com.angcyo.spring.mybatis.plus.table.BaseAuditTable
import com.baomidou.mybatisplus.annotation.TableName
import com.gitee.sunchenbin.mybatis.actable.annotation.Column

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/02/25
 */
@TableName("test_mybatis_bean")
class TestTable : BaseAuditTable() {
    @Column
    var data: String? = null

    @Column
    var message: String? = null

    @Column
    var num: Int = 0
}