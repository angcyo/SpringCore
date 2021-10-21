package com.angcyo.spring.mybatis.plus.base

import com.angcyo.spring.mybatis.plus.table.BaseAuditTable
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
        //strictInsertFill(metaObject, "createdAt", LocalDateTime::class.java, LocalDateTime.now())
        //strictInsertFill(metaObject, "updatedAt", LocalDateTime::class.java, LocalDateTime.now())
        //strictInsertFill(metaObject, "deleteFlag", Int::class.java, 0)

        strictInsertFillOnHas(
            metaObject,
            BaseAuditTable::createdAt.name,
            LocalDateTime::class.java,
            LocalDateTime.now()
        )
        strictInsertFillOnHas(
            metaObject,
            BaseAuditTable::updatedAt.name,
            LocalDateTime::class.java,
            LocalDateTime.now()
        )

        //审计
        //strictInsertFill(metaObject, "createdBy", String::class.java, "xxx")
        //strictInsertFill(metaObject, "updatedBy", String::class.java, "xxx")

        //strictInsertFill(metaObject, BaseAuditTable::createdBy.name, String::class.java, "$userId")
        //strictInsertFill(metaObject, BaseAuditTable::updatedBy.name, String::class.java, "$userId")
    }

    override fun updateFill(metaObject: MetaObject?) {
        //setFieldValByName("updatedAt", LocalDateTime.now(), metaObject)
        setFieldValByNameOnNoSet(BaseAuditTable::updatedAt.name, LocalDateTime.now(), metaObject)

        //审计
        //setFieldValByName("updatedBy", "xxx", metaObject)

        //setFieldValByName(BaseAuditTable::updatedBy.name, "$userId", metaObject)
    }

    /**如果有字段, 则填充值, 否则跳过*/
    fun <T, E : T?> strictInsertFillOnHas(
        metaObject: MetaObject?,
        fieldName: String?,
        fieldType: Class<T>?,
        fieldVal: E
    ): MetaObjectHandler? {
        if (metaObject?.hasSetter(fieldName) == true) {
            return strictInsertFill(metaObject, fieldName, fieldType, fieldVal)
        }
        return null
    }

    /**如果字段未设置值, 则设置, 否则使用默认的*/
    fun setFieldValByNameOnNoSet(fieldName: String?, fieldVal: Any?, metaObject: MetaObject?): MetaObjectHandler? {
        if (getFieldValByName(fieldName, metaObject) == null) {
            return setFieldValByName(BaseAuditTable::updatedAt.name, LocalDateTime.now(), metaObject)
        }
        return null
    }
}