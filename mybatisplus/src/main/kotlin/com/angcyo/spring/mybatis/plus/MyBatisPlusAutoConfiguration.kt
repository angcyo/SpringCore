package com.angcyo.spring.mybatis.plus

import com.baomidou.mybatisplus.annotation.DbType
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor
import org.mybatis.spring.annotation.MapperScan
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration


/**
 * https://mp.baomidou.com/
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/11/04
 */
@Configuration
@MapperScan(basePackages = ["com.angcyo.spring.**.mapper", "com.gitee.sunchenbin.mybatis.actable.dao.*"])
@ComponentScan(basePackages = ["com.gitee.sunchenbin.mybatis.actable.manager.*"])
//@PropertySource("classpath:application-actable.properties")
class MyBatisPlusAutoConfiguration {

    /**分页插件
     * https://mp.baomidou.com/guide/page.html*/

    /** 多租户插件
     * https://mp.baomidou.com/guide/interceptor-tenant-line.html#%E5%A4%9A%E7%A7%9F%E6%88%B7
     * */
    @Bean
    fun mybatisPlusInterceptor(): MybatisPlusInterceptor? {
        val interceptor = MybatisPlusInterceptor()

        /*interceptor.addInnerInterceptor(TenantLineInnerInterceptor(object : TenantLineHandler {

            //获取租户 ID 值表达式，只支持单个 ID 值
            override fun getTenantId(): Expression {
                return LongValue(1)
            }

            //获取租户字段名
            override fun getTenantIdColumn(): String? {
                return "tenant_id"
            }

            // 这是 default 方法,默认返回 false 表示所有表都需要拼多租户条件
            override fun ignoreTable(tableName: String): Boolean {
                return !"user".equals(tableName, ignoreCase = true)
            }
        }))*/

        // 如果用了分页插件注意先 add TenantLineInnerInterceptor 再 add PaginationInnerInterceptor
        // 用了分页插件必须设置 MybatisConfiguration#useDeprecatedExecutor = false
        interceptor.addInnerInterceptor(PaginationInnerInterceptor(DbType.MYSQL).apply {
            //isOverflow
            //maxLimit
        })
        return interceptor
    }

    /*@Bean
    fun configurationCustomizer(): ConfigurationCustomizer {
        return ConfigurationCustomizer {
            //configuration?.useDeprecatedExecutor = false
        }
    }*/
}