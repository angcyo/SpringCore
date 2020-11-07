package com.angcyo.spring.mysql

import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/11/04
 */

@Configuration
@ComponentScan(basePackages = ["com.angcyo.spring.mysql"])
@EnableJpaRepositories(basePackages = ["com.angcyo.spring"])
@EntityScan(basePackages = ["com.angcyo.spring"])
class MysqlAutoConfiguration {

}