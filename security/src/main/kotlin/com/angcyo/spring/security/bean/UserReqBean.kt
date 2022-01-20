package com.angcyo.spring.security.bean

import com.gitee.sunchenbin.mybatis.actable.annotation.IsKey
import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2022/01/20
 */

@ApiModel("更新用户的数据结构")
open class UserReqBean : SaveAccountReqBean() {
    @ApiModelProperty("数据Id, 更新或删除接口时使用.")
    @IsKey
    var id: Long? = null
}