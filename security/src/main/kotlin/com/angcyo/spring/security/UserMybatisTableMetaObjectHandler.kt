package com.angcyo.spring.security

import com.angcyo.spring.mybatis.plus.base.MybatisTableMetaObjectHandler
import com.angcyo.spring.mybatis.plus.table.BaseAuditTable
import com.angcyo.spring.security.jwt.currentUserOrNull
import org.apache.ibatis.reflection.MetaObject
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Component

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/05/29
 */

@Component
@Primary
class UserMybatisTableMetaObjectHandler : MybatisTableMetaObjectHandler() {
    override fun insertFill(metaObject: MetaObject?) {
        super.insertFill(metaObject)

        val userId = currentUserOrNull()?.userTable?.id ?: -1

        //审计
        strictInsertFill(metaObject, BaseAuditTable::createdBy.name, String::class.java, "$userId")
        strictInsertFill(metaObject, BaseAuditTable::updatedBy.name, String::class.java, "$userId")
    }

    override fun updateFill(metaObject: MetaObject?) {
        super.updateFill(metaObject)

        val userId = currentUserOrNull()?.userTable?.id ?: -1
        setFieldValByName(BaseAuditTable::updatedBy.name, "$userId", metaObject)
    }
}