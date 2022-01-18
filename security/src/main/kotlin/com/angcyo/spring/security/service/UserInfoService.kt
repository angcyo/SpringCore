package com.angcyo.spring.security.service

import com.angcyo.spring.mybatis.plus.auto.BaseAutoMybatisServiceImpl
import com.angcyo.spring.mybatis.plus.auto.annotation.AutoFillRef
import com.angcyo.spring.mybatis.plus.c
import com.angcyo.spring.security.mapper.IUserInfoMapper
import com.angcyo.spring.security.table.UserInfoTable
import org.springframework.stereotype.Service

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2022/01/18
 */

@Service
class UserInfoService : BaseAutoMybatisServiceImpl<IUserInfoMapper, UserInfoTable>() {

    /**根据[userId]查询用户信息*/
    @AutoFillRef("com.angcyo.spring.security.bean.AuthRepBean")
    fun queryUserInfo(userId: Long?): UserInfoTable? {
        return listQueryNewOne {
            eq(UserInfoTable::userId.c(), userId)
        }
    }

}