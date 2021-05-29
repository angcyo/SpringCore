package com.angcyo.spring.base

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.EnableAspectJAutoProxy

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/11/04
 *
 * 自动配置
 * https://docs.spring.io/spring-boot/docs/current/reference/html/spring-boot-features.html#boot-features-developing-auto-configuration
 */

@Configuration
@ComponentScan(basePackages = ["com.angcyo.spring.base"])
@EnableConfigurationProperties
//https://blog.csdn.net/daofengsuoxiang/article/details/103027280
//https://blog.csdn.net/andy_zhang2007/article/details/83960798
@EnableAspectJAutoProxy(exposeProxy = true)
class BaseAutoConfiguration
