package com.angcyo.spring.security.service

import com.angcyo.spring.mybatis.plus.service.BaseMybatisServiceImpl
import com.angcyo.spring.security.mapper.IRolePermissionMapper
import com.angcyo.spring.security.table.RolePermissionReTable
import org.springframework.stereotype.Service

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/05/28
 */

//@Service
class RolePermissionService : BaseMybatisServiceImpl<IRolePermissionMapper, RolePermissionReTable>()