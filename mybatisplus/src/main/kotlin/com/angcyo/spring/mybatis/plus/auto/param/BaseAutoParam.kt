package com.angcyo.spring.mybatis.plus.auto.param

import com.angcyo.spring.mybatis.plus.auto.annotation.AutoUpdateBy
import com.angcyo.spring.mybatis.plus.auto.annotation.AutoWhere
import com.gitee.sunchenbin.mybatis.actable.annotation.IsKey
import io.swagger.annotations.ApiModelProperty

/**
 * 根据主键查询/更新
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/05/29
 */
abstract class BaseAutoParam : IAutoParam {

    @AutoWhere
    @AutoUpdateBy
    @IsKey
    @ApiModelProperty("通过id查询/更新记录")
    var id: Long? = null
}