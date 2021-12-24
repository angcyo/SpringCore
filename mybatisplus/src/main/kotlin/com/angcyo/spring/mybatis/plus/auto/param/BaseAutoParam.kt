package com.angcyo.spring.mybatis.plus.auto.param

import com.angcyo.spring.mybatis.plus.auto.AutoType
import com.angcyo.spring.mybatis.plus.auto.annotation.AutoQuery
import com.angcyo.spring.mybatis.plus.auto.annotation.AutoUpdateBy
import com.angcyo.spring.mybatis.plus.auto.annotation.Query
import com.gitee.sunchenbin.mybatis.actable.annotation.IsKey
import io.swagger.annotations.ApiModelProperty

/**
 * 根据主键查询/更新
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/05/29
 */
abstract class BaseAutoParam : IAutoParam {

    @AutoUpdateBy
    @IsKey
    @AutoQuery(
        [
            Query(type = AutoType.QUERY),
            Query(type = AutoType.DELETE, checkNull = true, nullError = "请指定数据id"),
            Query(type = AutoType.UPDATE, checkNull = true, nullError = "请指定数据id"),
            Query(type = AutoType.REMOVE, checkNull = true, nullError = "请指定数据id"),
        ]
    )
    @ApiModelProperty("[通用参数]通过id查询/更新/删除记录")
    var id: Long? = null

    @ApiModelProperty("[通用参数]逻辑删除(0:未删除 1:删除)", hidden = true)
    var deleteFlag: Int? = 0
}