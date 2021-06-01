package com.angcyo.spring.security.service

import org.springframework.stereotype.Service

/**
 * 权限管理服务
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/06/01
 */

@Service
class PermissionManagerService {

    /**给指定的用户, 添加指定的权限
     * [userId] 用户的id
     * [permissionName] 权限的名字*/
    fun addUserPermission(userId: Long, permissionName: String) {

    }

}