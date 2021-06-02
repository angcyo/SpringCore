package com.angcyo.spring.security.service

import com.angcyo.spring.base.aspect.LogMethodTime
import com.angcyo.spring.mybatis.plus.auto.BaseAutoMybatisServiceImpl
import com.angcyo.spring.security.bean.UserRoleSaveBean
import com.angcyo.spring.security.mapper.IUserRoleMapper
import com.angcyo.spring.security.table.UserRoleReTable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/05/28
 */

@Service
class UserRoleService : BaseAutoMybatisServiceImpl<IUserRoleMapper, UserRoleReTable>() {

    /**充值用户对应的角色*/
    @LogMethodTime
    @Transactional
    fun resetUserRole(list: List<UserRoleSaveBean>) {
        autoReset(list)
    }
}