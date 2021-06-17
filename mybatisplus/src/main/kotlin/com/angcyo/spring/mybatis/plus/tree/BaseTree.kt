package com.angcyo.spring.mybatis.plus.tree

import com.gitee.sunchenbin.mybatis.actable.annotation.Column
import io.swagger.annotations.ApiModelProperty

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/06/16
 */
open class BaseTree : IBaseTree {

    @ApiModelProperty("父id, 顶级用[-1]")
    @Column(comment = "父id, 顶级用[-1]")
    override var parentId: Long? = null

    @ApiModelProperty("父ids, 用,号. 包含自身id")
    @Column(comment = "父ids, 用,号. 包含自身id")
    override var parentIds: String? = null

}