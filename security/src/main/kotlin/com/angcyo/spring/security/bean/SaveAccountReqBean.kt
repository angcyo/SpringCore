package com.angcyo.spring.security.bean

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/05/29
 */

@ApiModel("新增帐号参数")
class SaveAccountReqBean {

    @ApiModelProperty("注册账号的数据")
    var registerReqBean: RegisterReqBean? = null

    @ApiModelProperty("需要分配的角色id")
    var roleIdList: List<Long>? = null
}