package com.angcyo.spring.app

import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/11/04
 *
 * 自动配置
 * https://docs.spring.io/spring-boot/docs/current/reference/html/spring-boot-features.html#boot-features-developing-auto-configuration
 */

@Configuration
@ComponentScan(basePackages = ["com.angcyo.spring.app"])
@EnableJpaAuditing
class AppAutoConfiguration
