package com.angcyo.spring.security.service.annotation

/**
 * 标识方法是保存帐号, 用来实现AOP
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/05/29
 */

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class SaveAccount
