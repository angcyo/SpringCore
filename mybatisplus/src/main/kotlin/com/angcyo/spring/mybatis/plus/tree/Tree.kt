package com.angcyo.spring.mybatis.plus.tree

import io.swagger.annotations.ApiModelProperty

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/06/16
 */
class Tree : BaseTree(), ITree<Tree> {
    @ApiModelProperty("子节点集合")
    override var childList: List<Tree>? = null
}