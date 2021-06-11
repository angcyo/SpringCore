package com.angcyo.spring.mybatis.plus.auto.param

import com.angcyo.spring.mybatis.plus.auto.annotation.AutoDelete
import com.angcyo.spring.mybatis.plus.auto.annotation.AutoUpdateBy
import com.angcyo.spring.mybatis.plus.auto.annotation.AutoUpdate
import com.angcyo.spring.mybatis.plus.auto.annotation.AutoQuery
import com.gitee.sunchenbin.mybatis.actable.annotation.IsKey
import io.swagger.annotations.ApiModelProperty

/**
 * 根据主键查询/更新
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/05/29
 */
abstract class BaseAutoParam : IAutoParam {

    @AutoQuery
    @AutoUpdateBy
    @IsKey
    @ApiModelProperty("通过id查询/更新/删除记录")
    @AutoDelete
    @AutoUpdate
    var id: Long? = null

    @AutoQuery
    @ApiModelProperty("逻辑删除(0:未删除 1:删除)")
    var deleteFlag: Int? = 0
}