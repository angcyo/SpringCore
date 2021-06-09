package com.angcyo.spring.security.jwt.event

import com.angcyo.spring.security.bean.UserDetail
import org.springframework.context.ApplicationEvent

/**
 * 退出登录的事件
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/06/09
 */
class LogoutEvent(val userDetail: UserDetail) : ApplicationEvent(userDetail) {
    override fun getSource(): UserDetail {
        return super.getSource() as UserDetail
    }
}