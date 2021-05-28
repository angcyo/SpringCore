package com.angcyo.spring.security.service

import com.angcyo.spring.mybatis.plus.table.BaseAuditTable
import com.angcyo.spring.security.mapper.IAccountMapper
import com.angcyo.spring.security.table.AccountTable
import com.baomidou.mybatisplus.core.mapper.BaseMapper
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import org.springframework.stereotype.Service

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/05/28
 */

abstract class BaseMybatisServiceImpl3<M : BaseMapper<T>, T : BaseAuditTable> : ServiceImpl<M, T>()

//class AccountService : BaseAutoMybatisServiceImpl<IAccountMapper, AccountTable>()
@Service
class AccountService : BaseMybatisServiceImpl3<IAccountMapper, AccountTable>()