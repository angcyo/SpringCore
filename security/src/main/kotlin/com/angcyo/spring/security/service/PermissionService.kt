package com.angcyo.spring.security.service

import com.angcyo.spring.mybatis.plus.service.BaseMybatisServiceImpl
import com.angcyo.spring.security.mapper.IPermissionMapper
import com.angcyo.spring.security.table.PermissionTable
import org.springframework.stereotype.Service

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/05/28
 */

//@Service
class PermissionService : BaseMybatisServiceImpl<IPermissionMapper, PermissionTable>()