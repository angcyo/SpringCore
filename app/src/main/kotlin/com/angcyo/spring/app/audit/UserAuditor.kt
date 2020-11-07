package com.angcyo.spring.app.audit

import org.springframework.data.domain.AuditorAware
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import java.util.*

/**
 * 2020-11-07
 * https://www.cnblogs.com/niceyoo/p/10908647.html
 */
@Component
class UserAuditor : AuditorAware<String> {
    /**
     * 获取当前创建或修改的用户
     */
    override fun getCurrentAuditor(): Optional<String> {
        val user: UserDetails
        return try {
            user = SecurityContextHolder.getContext().authentication.principal as UserDetails
            Optional.ofNullable(user.username)
        } catch (e: Exception) {
            Optional.empty()
        }
    }
}