package com.angcyo.spring.security.service

import com.angcyo.spring.mybatis.plus.auto.BaseAutoMybatisServiceImpl
import com.angcyo.spring.security.mapper.IUserRoleMapper
import com.angcyo.spring.security.table.UserRoleReTable
import org.springframework.stereotype.Service

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/05/28
 */

@Service
class UserRoleService : BaseAutoMybatisServiceImpl<IUserRoleMapper, UserRoleReTable>()