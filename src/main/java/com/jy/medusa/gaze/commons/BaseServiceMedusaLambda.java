package com.jy.medusa.gaze.commons;

import com.jy.medusa.gaze.stuff.param.lambda.HolyGetter;

import java.util.List;

public interface BaseServiceMedusaLambda<T> {

	List<T> selectAllLambda(HolyGetter<T>... paramFns);

	T selectOneLambda(T entity, HolyGetter<T>... paramFns);

	List<T> selectByIdsLambda(List<Object> ids, HolyGetter<T>... paramFns);

    T selectByIdLambda(Object id, HolyGetter<T>... paramFns);

	List<T> selectListByLambda(T entity, HolyGetter<T>... paramFns);

	int selectCountLambda(Object... mixParams);

	List<T> selectByGazeMagic(Object... mixParams);

//	int saveSelective(T entity);

//	int save(T entity);

	int saveBatchLambda(List<T> obs, HolyGetter<T>... paramFns);

	int updateLambda(T entity, HolyGetter<T>... paramFns);

//	int updateSelective(T entity);

	int updateBatchLambda(List<T> obs, HolyGetter<T>... paramFns);

//	int deleteById(Object id);

//	int deleteBatch(List<Object> ids);

//	int deleteBy(T entity);
}