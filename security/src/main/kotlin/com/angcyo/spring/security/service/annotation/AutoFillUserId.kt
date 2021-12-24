package com.angcyo.spring.security.service.annotation

import com.angcyo.spring.mybatis.plus.auto.annotation.AutoFill

/**
 * 填充用户id
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/12/24
 */

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@AutoFill(
    service = com.angcyo.spring.security.service.UserService::class,
    serviceMethod = "getCurrentUserId",
    targetField = ""
)
//@AutoFill(spEL = "@userService.getCurrentUserId()")
annotation class AutoFillUserId
