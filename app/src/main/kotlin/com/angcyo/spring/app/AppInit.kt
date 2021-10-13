package com.angcyo.spring.app

import com.angcyo.spring.log.core.ServletLog
import com.angcyo.spring.security.SecurityConfiguration
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component

/** 初始化操作
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/10/13
 */

@Component
class AppInit : ApplicationRunner {

    override fun run(args: ApplicationArguments?) {
        ServletLog.addLogIgnore(SecurityConfiguration.SECURITY_WHITE_LIST)
    }
}