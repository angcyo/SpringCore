package com.angcyo.spring.security.service

import com.angcyo.spring.util.L
import com.angcyo.spring.util.have
import org.springframework.beans.factory.annotation.Autowired
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

    @Autowired
    lateinit var permissionService: PermissionService

    /**判断指定的用户, 是否具有指定uri的访问权限
     * [userId] 用户的id
     * [uri] 权限的uri地址*/
    fun havePermission(userId: Long, uri: String): Boolean {
        val list = permissionService.getUserPermission(userId)
        var have = false

        for (p in list) {
            if (p.permit.isNullOrEmpty() && p.deny.isNullOrEmpty()) {
                //允许和禁用 都没有配置
                have = false
            }

            if (!p.permit.isNullOrEmpty()) {
                //放行的uri
                have = uri.have(p.permit, true)
            }

            if (!p.deny.isNullOrEmpty()) {
                //禁止的uri
                if (uri.have(p.deny, true)) {
                    have = false
                }
            }

            if (have) {
                L.i("$uri 通过权限验证: $p")
                break
            }
        }
        return have
    }
}