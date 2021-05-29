package com.angcyo.spring.security

import com.angcyo.spring.mybatis.plus.base.MybatisTableMetaObjectHandler
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
    }

    override fun updateFill(metaObject: MetaObject?) {
        super.updateFill(metaObject)
    }
}