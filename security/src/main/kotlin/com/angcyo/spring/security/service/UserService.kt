package com.angcyo.spring.security.service

import com.angcyo.spring.mybatis.plus.auto.BaseAutoMybatisServiceImpl
import com.angcyo.spring.security.mapper.IUserMapper
import com.angcyo.spring.security.table.UserTable
import org.springframework.stereotype.Service

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/05/28
 */

@Service
class UserService : BaseAutoMybatisServiceImpl<IUserMapper, UserTable>()