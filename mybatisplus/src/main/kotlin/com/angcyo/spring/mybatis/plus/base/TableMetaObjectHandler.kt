package com.angcyo.spring.mybatis.plus.base

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler
import org.apache.ibatis.reflection.MetaObject
import org.springframework.stereotype.Component
import java.time.LocalDateTime

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/02/25
 */

@Component
class TableMetaObjectHandler : MetaObjectHandler {

    override fun insertFill(metaObject: MetaObject?) {
        this.strictInsertFill(metaObject, "createdAt", LocalDateTime::class.java, LocalDateTime.now())
        this.strictInsertFill(metaObject, "updatedAt", LocalDateTime::class.java, LocalDateTime.now())
        this.strictInsertFill(metaObject, "deleteFlag", Int::class.java, 0)
    }

    override fun updateFill(metaObject: MetaObject?) {
        this.strictUpdateFill(metaObject, "updatedAt", LocalDateTime::class.java, LocalDateTime.now())
    }
}