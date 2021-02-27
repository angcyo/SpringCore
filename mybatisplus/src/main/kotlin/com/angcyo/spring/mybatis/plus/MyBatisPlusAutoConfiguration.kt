package com.angcyo.spring.mybatis.plus

import com.baomidou.mybatisplus.annotation.DbType
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor
import org.mybatis.spring.annotation.MapperScan
import org.springframework.context.annotation.Bean
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
class MyBatisPlusAutoConfiguration {

    /**分页插件
     * https://mp.baomidou.com/guide/page.html*/
    @Bean
    fun mybatisPlusInterceptor(): MybatisPlusInterceptor? {
        val interceptor = MybatisPlusInterceptor()
        interceptor.addInnerInterceptor(PaginationInnerInterceptor(DbType.MYSQL).apply {
            //isOverflow
            //maxLimit
        })
        return interceptor
    }

}