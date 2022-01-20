package com.angcyo.spring.security.bean

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/05/29
 */

@ApiModel("新增帐号参数")
open class SaveAccountReqBean : RegisterReqBean() {

    @ApiModelProperty("需要分配的角色id")
    var roleIdList: List<Long>? = null
}