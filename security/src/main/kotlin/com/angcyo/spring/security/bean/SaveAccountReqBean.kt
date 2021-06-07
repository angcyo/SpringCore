package com.angcyo.spring.security.bean

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/05/29
 */
class SaveAccountReqBean {

    /**注册账号需要的一些基础数据*/
    var registerReqBean: RegisterReqBean? = null

    /**需要分配的角色id*/
    var roleIdList: List<Long>? = null

}