package com.angcyo.spring;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * Email:angcyo@126.com
 *
 * @author angcyo
 * @date 2021/05/28
 */
public class BaseMybatisServiceImpl2<M extends BaseMapper<T>, T> extends ServiceImpl<M, T> implements IService<T> {
}
