package com.jy.medusa.commons;


import com.jy.medusa.base.BaseDeleteMapper;
import com.jy.medusa.base.BaseInsertMapper;
import com.jy.medusa.base.BaseSelectMapper;
import com.jy.medusa.base.BaseUpdateMapper;

/**
 * 通用Mapper接口,其他接口继承该接口即可
 * <p/>
 * <p>这是一个例子，自己扩展时可以参考</p>
 * <p/>
 * @param <T> 不能为空
 * @author neo
 */
public interface BaseMapper<T> extends
        BaseSelectMapper<T>,
        BaseInsertMapper<T>,
        BaseUpdateMapper<T>,
        BaseDeleteMapper<T> {
}