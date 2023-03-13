package com.jy.medusa.gaze.commons;

import com.jy.medusa.gaze.stuff.param.lambda.HolyGetter;

import java.io.Serializable;
import java.util.List;

public interface BaseServiceMedusaLambda<T> {

	List<T> selectAll(HolyGetter<T>... paramFns);

	T selectOne(T entity, HolyGetter<T>... paramFns);

	List<T> selectByIds(List<Serializable> ids, HolyGetter<T>... paramFns);

    T selectById(Serializable id, HolyGetter<T>... paramFns);

//	List<T> select(T entity, HolyGetter<T>... paramFns);

	int selectCount(Object... mixParams);

	List<T> selectByGazeMagic(Object... mixParams);

//	int saveSelective(T entity);

//	int save(T entity);

	int saveBatchInclude(List<T> obs, HolyGetter<T>... paramFns);

	int saveBatchExclude(List<T> obs, HolyGetter<T>... paramFns);

//	int update(T entity, HolyGetter<T>... paramFns);

//	int updateSelective(T entity);

	int updateBatchInclude(List<T> obs, HolyGetter<T>... paramFns);

	int updateBatchExclude(List<T> obs, HolyGetter<T>... paramFns);

//	int deleteById(Serializable id);

//	int deleteBatch(List<Serializable> ids);

//	int deleteBy(T entity);
}