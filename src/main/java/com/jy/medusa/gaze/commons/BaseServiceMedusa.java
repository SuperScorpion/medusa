package com.jy.medusa.gaze.commons;

import java.io.Serializable;
import java.util.List;

/**
 * 基础接口
 * before : BaseServiceMedusa - BaseServiceMedusaString - BaseServiceMedusaLambda
 * after : BaseServiceMedusa -  BaseServiceMedusaLambda
 * @param <T> 参数
 * @author SuperScorpion
 */
public interface BaseServiceMedusa<T> extends BaseServiceMedusaLambda<T> {

//	List<T> selectAll();

//	T selectOne(T entity);

//	List<T> selectByIds(List<Serializable> ids);

//	T selectById(Serializable id);

//	List<T> selectListBy(T entity);

//	int selectCount(Object... mixParams);

//	List<T> selectByGazeMagic(Object... mixParams);

	int insertSelective(T entity);

	int insert(T entity);

	int insertBatch(List<T> obs);

	int update(T entity);

	int updateSelective(T entity);

	int updateBatch(List<T> obs);

	int deleteById(Serializable id);

	int deleteBatchByIds(List<Serializable> ids);

	int delete(T entity);
}