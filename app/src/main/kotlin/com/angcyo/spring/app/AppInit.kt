package com.angcyo.spring.app

import com.angcyo.spring.log.core.ServletLog
import com.angcyo.spring.security.SecurityConfiguration
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct

/** 初始化操作
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/10/13
 */

@Component
class AppInit : ApplicationRunner {

    //执行顺序1
    @PostConstruct
    fun init() {
        SecurityConfiguration.SECURITY_WHITE_LIST.add("/err/**")
    }

    //执行顺序2
    override fun run(args: ApplicationArguments?) {
        ServletLog.addLogIgnore(SecurityConfiguration.SECURITY_WHITE_LIST)
    }
}