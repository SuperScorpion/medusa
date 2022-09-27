package com.jy.medusa.gaze.commons;

import java.io.Serializable;
import java.util.List;

/**
 * 基础接口 BaseServiceMedusa - BaseServiceMedusaString - BaseServiceMedusaLambda
 * @param <T> 参数
 */
public interface BaseServiceMedusa<T> extends BaseServiceMedusaLambda<T> {

	List<T> selectAll();

	T selectOne(T entity);

	List<T> selectByIds(List<Serializable> ids);

	T selectById(Serializable id);

	List<T> selectListBy(T entity);

//	int selectCount(Object... mixParams);
//
//	List<T> selectByGazeMagic(Object... mixParams);

	int saveSelective(T entity);

	int save(T entity);

	int saveBatch(List<T> obs);

	int update(T entity);

	int updateSelective(T entity);

	int updateBatch(List<T> obs);

	int deleteById(Serializable id);

	int deleteBatch(List<Serializable> ids);

	int deleteBy(T entity);
}