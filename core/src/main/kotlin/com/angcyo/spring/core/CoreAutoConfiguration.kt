package com.angcyo.spring.core

import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.annotation.EnableScheduling

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/11/04
 *
 * 自动配置
 * https://docs.spring.io/spring-boot/docs/current/reference/html/spring-boot-features.html#boot-features-developing-auto-configuration
 */

//@EnableTransactionManagement
@Configuration
@EnableScheduling //激活定时任务
@EnableAsync //激活异步
@ComponentScan(basePackages = ["com.angcyo.spring.core"])
class CoreAutoConfiguration
