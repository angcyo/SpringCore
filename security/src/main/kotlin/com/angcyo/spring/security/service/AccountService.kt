package com.angcyo.spring.security.service

import com.angcyo.spring.base.beanOf
import com.angcyo.spring.base.extension.apiError
import com.angcyo.spring.mybatis.plus.auto.BaseAutoMybatisServiceImpl
import com.angcyo.spring.mybatis.plus.columnName
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
        if (account.isNullOrBlank()) {
            apiError("帐号不允许为空")
        }
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

    /**添加一个账号*/
    fun saveAccount(bean: SaveAccountReqBean): UserTable {
        if (isAccountExist(bean.registerReqBean?.account)) {
            apiError("帐号已存在")
        }
        val authService = beanOf(AuthService::class.java)
        val userTable = authService.saveAccount(bean)
        return userTable
    }

    /**获取用户对应的所有帐号信息*/
    fun getUserAccount(userId: Long): List<AccountTable> {
        return listQuery {
            eq(AccountTable::userId.columnName(), userId)
        }
    }
}