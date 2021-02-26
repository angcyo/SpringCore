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

    companion object {
        const val DEFAULT_USER = "unknown"
    }

    /**
     * 获取当前创建或修改的用户
     */
    override fun getCurrentAuditor(): Optional<String> {
        val authentication = SecurityContextHolder.getContext().authentication ?: return Optional.ofNullable("empty")
        val user: UserDetails
        return try {
            user = authentication.principal as UserDetails
            Optional.ofNullable(user.username)
        } catch (e: Exception) {
            if (authentication.principal is String) {
                Optional.ofNullable("${authentication.principal}")
            } else {
                Optional.ofNullable(DEFAULT_USER)
            }
        }
    }
}