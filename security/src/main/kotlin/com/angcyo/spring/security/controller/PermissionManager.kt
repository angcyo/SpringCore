package com.angcyo.spring.security.controller

import com.angcyo.spring.security.jwt.currentUserOrNull
import com.angcyo.spring.security.table.PermissionTable
import com.angcyo.spring.util.L
import com.angcyo.spring.util.have
import org.springframework.stereotype.Service

/**
 * 权限管理服务
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/06/01
 */

@Service
class PermissionManager {

    /**是否有通配符 ? * +*/
    fun String?.haveWildcard() = this?.run {
        contains("?") || contains("*") || contains("+")
    } == true

    /**判断指定的用户, 是否具有指定uri的访问权限
     * [userId] 用户的id
     * [uri] 权限的uri地址*/
    fun havePermission(userId: Long, uri: String): Boolean {
        val list: List<PermissionTable> = currentUserOrNull()?.userPermissionList ?: emptyList()

        //是否有权限
        var have = false

        for (p in list) {
            val permit = p.permit
            val deny = p.deny
            val forceDeny = p.forceDeny

            if (!forceDeny.isNullOrEmpty()) {
                //一票拒绝的权限配置
                if (uri.have(forceDeny, true)) {
                    //禁止访问
                    have = false
                    break
                }
            }

            if (permit.isNullOrEmpty() && deny.isNullOrEmpty()) {
                //允许和禁用 都没有配置
                have = false
            }

            //通过权限声明是否有通配符, 有通配符声明的权限匹配优先级低
            var permitWildcard = false
            var denyWildcard = false

            if (permit == null) {
                have = false
            } else if (permit.isEmpty() || permit == "*") {
                have = true
            } else if (permit.isNotEmpty()) {
                //放行的uri
                have = uri.have(permit, true)
                permitWildcard = permit.haveWildcard()
            }

            if (!deny.isNullOrEmpty()) {
                //禁止的uri
                if (uri.have(deny, true)) {
                    denyWildcard = deny.haveWildcard()

                    if (p.strict == true) {
                        have = false
                    } else {
                        if (denyWildcard && !permitWildcard) {
                            //如果禁止有通配符, 但是允许没有通配符, 则权限使用允许的条件判断
                        } else {
                            have = false
                        }
                    }
                }
            }

            if (have) {
                L.i("PermissionManager:$uri 通过权限验证: $p")
                break
            }
        }

        return have
    }
}