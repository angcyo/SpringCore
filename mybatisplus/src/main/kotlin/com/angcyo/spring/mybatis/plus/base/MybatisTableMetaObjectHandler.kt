package com.angcyo.spring.mybatis.plus.base

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler
import org.apache.ibatis.reflection.MetaObject
import org.springframework.stereotype.Component
import java.time.LocalDateTime

/**
 * [com.angcyo.spring.mybatis.plus.table.BaseAuditTable]
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/02/25
 */

@Component
class MybatisTableMetaObjectHandler : MetaObjectHandler {

    override fun insertFill(metaObject: MetaObject?) {
        strictInsertFill(metaObject, "createdAt", LocalDateTime::class.java, LocalDateTime.now())
        strictInsertFill(metaObject, "updatedAt", LocalDateTime::class.java, LocalDateTime.now())
        //strictInsertFill(metaObject, "deleteFlag", Int::class.java, 0)

        //审计
        //strictInsertFill(metaObject, "createdBy", String::class.java, "xxx")
        //strictInsertFill(metaObject, "updatedBy", String::class.java, "xxx")

        //strictInsertFill(metaObject, BaseAuditTable::createdBy.name, String::class.java, "$userId")
        //strictInsertFill(metaObject, BaseAuditTable::updatedBy.name, String::class.java, "$userId")
    }

    override fun updateFill(metaObject: MetaObject?) {
        setFieldValByName("updatedAt", LocalDateTime.now(), metaObject)

        //审计
        //setFieldValByName("updatedBy", "xxx", metaObject)

        //setFieldValByName(BaseAuditTable::updatedBy.name, "$userId", metaObject)
    }
}