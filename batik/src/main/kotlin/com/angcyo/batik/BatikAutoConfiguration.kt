package com.angcyo.batik

import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/10/27
 *
 * 自动配置
 * https://docs.spring.io/spring-boot/docs/current/reference/html/spring-boot-features.html#boot-features-developing-auto-configuration
 */

@Configuration
@ComponentScan(basePackages = ["com.angcyo.batik"])
class BatikAutoConfiguration