package com.angcyo.spring.mybatis.plus.auto.param

import com.angcyo.spring.mybatis.plus.auto.IBaseAutoMybatisService
import com.fasterxml.jackson.annotation.JsonInclude
import org.springframework.cache.annotation.Cacheable


/**
 * Json只返回非空的值
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/05/28
 */

@Cacheable
@JsonInclude(JsonInclude.Include.NON_NULL) //https://blog.csdn.net/weixin_44685511/article/details/118211458
interface IAutoParam

/**扩展的自动填充*/
fun <T : IAutoParam> T.autoFill(service: IBaseAutoMybatisService<*>): T {
    service.autoFill(this)
    return this
}