package com.angcyo.spring.mybatis.plus

import org.mybatis.spring.annotation.MapperScan
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/11/04
 */
@Configuration
@MapperScan(basePackages = ["com.angcyo.spring", "com.gitee.sunchenbin.mybatis.actable.dao.*"])
@ComponentScan(basePackages = ["com.gitee.sunchenbin.mybatis.actable.manager.*"])
//@PropertySource("classpath:application-actable.properties")
class MyBatisPlusAutoConfiguration