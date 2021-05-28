package com.angcyo.spring.mybatis.plus.service

import com.baomidou.mybatisplus.core.mapper.BaseMapper
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/05/28
 */
abstract class BaseMybatisServiceImpl<M : BaseMapper<T>, T> : ServiceImpl<M, T>()