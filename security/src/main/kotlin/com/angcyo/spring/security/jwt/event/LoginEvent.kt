package com.angcyo.spring.security.jwt.event

import com.angcyo.spring.security.bean.UserDetail
import org.springframework.context.ApplicationEvent

/**
 * 登录接口, 登录成功的事件
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/06/11
 */
class LoginEvent(val userDetail: UserDetail) : ApplicationEvent(userDetail) {

    override fun getSource(): UserDetail {
        return super.getSource() as UserDetail
    }
}