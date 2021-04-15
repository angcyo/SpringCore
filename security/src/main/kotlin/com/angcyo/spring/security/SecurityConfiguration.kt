package com.angcyo.spring.security

import com.angcyo.spring.security.jwt.*
import com.angcyo.spring.security.service.AuthService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import java.security.SecureRandom


/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/11/06
 *
 * https://blog.csdn.net/z1790424577/article/details/80738507
 * https://blog.csdn.net/z1790424577/article/details/80739123
 *
 * https://juejin.im/post/6844903872096387080
 */

@EnableWebSecurity
class SecurityConfiguration : WebSecurityConfigurerAdapter() {

    companion object {

        /**密码加密器*/
        val encoder = BCryptPasswordEncoder(BCryptPasswordEncoder.BCryptVersion.`$2B`, 10, SecureRandom())

        /**认证白名单, 不需要验证
         * 取消安全验证, 可以使用通配符`*` `**`
         * SecurityConfiguration.SECURITY_WHITE_LIST.add("\**")
         */
        val SECURITY_WHITE_LIST = mutableListOf(
            "/test/**",
            "/http/**",
            "/auth/**",
            "/swagger**",
            "/swagger-ui/**",
            "/swagger-resources/**",
            "/static/**",
            "/js/*",
            "/css/**",
            "/webjars/**",
            "/v2/api-docs/**",
            "/v3/api-docs/**",
            "/doc.html",
        )

        fun configSecurityWhiteList(config: MutableList<String>.() -> Unit) {
            SECURITY_WHITE_LIST.apply(config)
        }
    }

    @Autowired
    lateinit var userDetailsServiceImpl: UserDetailsServiceImpl

    /**1. 通过重载，配置user-detail服务*/
    override fun configure(auth: AuthenticationManagerBuilder) {
        //super.configure(auth)
        /*auth.inMemoryAuthentication()
                .withUser("user")
                .password(passwordEncoder().encode("password"))
                .authorities("ROLE_USER")*/

        // 设置自定义的userDetailsService以及密码编码器, 用于登录接口验证判断
        auth.authenticationProvider(authenticationProvider())
            .userDetailsService(userDetailsServiceImpl)
            .passwordEncoder(passwordEncoder())//.password(passwordEncoder())

    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return encoder
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", CorsConfiguration().applyPermitDefaultValues())
        return source
    }

    /**
     * 认证 AuthenticationProvider
     */
    @Bean
    fun authenticationProvider(): AuthenticationProvider {
        return JwtAuthenticationProvider()
    }

    /**2. */
    override fun userDetailsService(): UserDetailsService {
        return super.userDetailsService()
    }

    override fun userDetailsServiceBean(): UserDetailsService {
        return super.userDetailsServiceBean()
    }

    @Autowired
    lateinit var authService: AuthService

    /**3. 通过重载，配置如何通过拦截器保护请求*/
    override fun configure(http: HttpSecurity) {
        //super.configure(http)
        http.cors()
            .and()
            .csrf().disable()
            .authorizeRequests()
            .antMatchers(*SECURITY_WHITE_LIST.toTypedArray()).permitAll()
            .anyRequest().authenticated()
            //.and().formLogin().loginPage().failureUrl()
            .and()
            .addFilter(JwtLoginFilter(authenticationManager(), authService))
            .addFilter(JwtAuthorizationFilter(authenticationManager(), userDetailsServiceImpl, authService))
            //.addFilter(JwtLogoutFilter(SecurityLogoutSuccessHandler(), SecurityLogoutHandler()))
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .logout()
            /*.logout().defaultLogoutSuccessHandlerFor(
                    SecurityLogoutSuccessHandler(),
                    AntPathRequestMatcher(SecurityConstants.AUTH_LOGIN_URL, RequestMethod.POST.toString()))*/
            .logoutUrl(SecurityConstants.AUTH_LOGOUT_URL)
            .logoutSuccessUrl(SecurityConstants.AUTH_LOGOUT_SUCCESS_URL)
            .logoutSuccessHandler(JwtLogoutSuccessHandler())
            .addLogoutHandler(JwtLogoutHandler())
            .and()
            // 授权异常处理
            .exceptionHandling().authenticationEntryPoint(JwtAuthenticationEntryPoint())
            .accessDeniedHandler(JwtAccessDeniedHandler())

        // 防止H2 web 页面的Frame 被拦截
        http.headers().frameOptions().disable()
    }

    /**4. 通过重载，配置Spring Security的Filter链*/
    override fun configure(web: WebSecurity) {
        web.ignoring()
            .antMatchers(
                "**.js",
                "**.css",
                "/images/**",
                "/webjars/**",
                "/**/favicon.ico"
            )
    }
}