package com.angcyo.spring.security

import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.core.userdetails.UserDetailsService

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/11/06
 *
 * https://blog.csdn.net/z1790424577/article/details/80738507
 * https://blog.csdn.net/z1790424577/article/details/80739123
 */

@EnableWebSecurity
class SecurityConfiguration : WebSecurityConfigurerAdapter() {

    /**1. 通过重载，配置user-detail服务*/
    override fun configure(auth: AuthenticationManagerBuilder?) {
        super.configure(auth)
    }

    /**2. */
    override fun userDetailsService(): UserDetailsService {
        return super.userDetailsService()
    }

    /**3. 通过重载，配置如何通过拦截器保护请求*/
    override fun configure(http: HttpSecurity?) {
        //super.configure(http)
        http?.authorizeRequests()?.apply {

            //security下的所有请求都需要认证
            antMatchers("/security/**").authenticated()

            //其他请求放行
            anyRequest().permitAll()

            and().formLogin()
            and().httpBasic() //get方法不会403, post方法403*/
        }
    }

    /**4. 通过重载，配置Spring Security的Filter链*/
    override fun configure(web: WebSecurity?) {
        super.configure(web)
    }
}