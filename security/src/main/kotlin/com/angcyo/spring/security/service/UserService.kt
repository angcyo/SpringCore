package com.angcyo.spring.security.service

import com.angcyo.spring.mybatis.plus.auto.BaseAutoMybatisServiceImpl
import com.angcyo.spring.security.jwt.currentUserId
import com.angcyo.spring.security.mapper.IUserMapper
import com.angcyo.spring.security.table.UserTable
import com.angcyo.spring.util.nowTime
import org.springframework.stereotype.Service

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/05/28
 */

@Service
class UserService : BaseAutoMybatisServiceImpl<IUserMapper, UserTable>() {

    /**获取当前登录的用户id*/
    fun getCurrentUserId() = currentUserId()
}