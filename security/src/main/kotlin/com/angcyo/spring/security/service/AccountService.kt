package com.angcyo.spring.security.service

import com.angcyo.spring.mybatis.plus.auto.BaseAutoMybatisServiceImpl
import com.angcyo.spring.security.bean.AccountQueryParam
import com.angcyo.spring.security.mapper.IAccountMapper
import com.angcyo.spring.security.table.AccountTable
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
}