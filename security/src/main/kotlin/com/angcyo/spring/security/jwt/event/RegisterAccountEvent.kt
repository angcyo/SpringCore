package com.angcyo.spring.security.jwt.event

import com.angcyo.spring.security.bean.RegisterReqBean
import com.angcyo.spring.security.table.UserTable
import org.springframework.context.ApplicationEvent

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/06/03
 */
class RegisterAccountEvent(val bean: RegisterReqBean, val user: UserTable) : ApplicationEvent(bean) {

    override fun getSource(): RegisterReqBean {
        return super.getSource() as RegisterReqBean
    }
}