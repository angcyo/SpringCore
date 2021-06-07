package com.angcyo.spring.security.service

import com.angcyo.spring.base.beanOf
import com.angcyo.spring.base.extension.apiError
import com.angcyo.spring.mybatis.plus.auto.BaseAutoMybatisServiceImpl
import com.angcyo.spring.security.bean.AccountQueryParam
import com.angcyo.spring.security.bean.RegisterReqBean
import com.angcyo.spring.security.bean.SaveAccountReqBean
import com.angcyo.spring.security.mapper.IAccountMapper
import com.angcyo.spring.security.table.AccountTable
import com.angcyo.spring.security.table.UserTable
import org.springframework.stereotype.Service

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/05/28
 */

@Service
class AccountService : BaseAutoMybatisServiceImpl<IAccountMapper, AccountTable>() {

    /**判断帐号是否存在*/
    fun isAccountExist(account: String?): Boolean {
        return autoCount(AccountQueryParam().apply {
            name = account
        }) > 0
    }

    /**添加一个账号
     * [password] 未加密的密码*/
    fun addAccount(account: String, password: String, roleIdList: List<Long>? = null): UserTable {
        if (isAccountExist(account)) {
            apiError("帐号已存在")
        }
        val authService = beanOf(AuthService::class.java)
        val userTable = authService.saveAccount(SaveAccountReqBean().apply {
            registerReqBean = RegisterReqBean().apply {
                this.account = account
                this.password = password
            }
            this.roleIdList = roleIdList
        })
        return userTable
    }
}