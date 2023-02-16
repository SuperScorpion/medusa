package com.jy.medusa.gaze.commons;


import com.jy.medusa.gaze.base.BaseDeleteMapper;
import com.jy.medusa.gaze.base.BaseInsertMapper;
import com.jy.medusa.gaze.base.BaseSelectMapper;
import com.jy.medusa.gaze.base.BaseUpdateMapper;

/**
 * 通用Mapper接口
 * mapper的基础类 其他接口继承该接口即可
 * @param <T> 实体类泛型
 * @author SuperScorpion
 */
public interface BaseMapper<T> extends
        BaseSelectMapper<T>,
        BaseInsertMapper<T>,
        BaseUpdateMapper<T>,
        BaseDeleteMapper<T> {
}