package com.angcyo.spring.mybatis.plus.test.table

import com.angcyo.spring.mybatis.plus.table.BaseAuditBean
import com.baomidou.mybatisplus.annotation.TableName
import com.gitee.sunchenbin.mybatis.actable.annotation.Column

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/02/25
 */
@TableName("test_mybatis_bean")
class TestBean : BaseAuditBean() {
    @Column
    var data: String? = null

    @Column
    var message: String? = null

    @Column
    var count: Int = 0
}