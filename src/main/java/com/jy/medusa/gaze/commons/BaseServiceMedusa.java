package com.jy.medusa.gaze.commons;

import java.util.List;

public interface BaseServiceMedusa<T> extends BaseServiceMedusaLambda<T> {

	List<T> selectAll(String... paramColumn);

	T selectOne(T entity, String... paramColumns);

	List<T> selectByIds(List<Object> ids, String... paramColumns);

	T selectById(Object id, String... paramColumns);

	List<T> selectListBy(T entity, String... paramColumns);

	int selectCount(Object... mixParams);

	List<T> selectByGaze(Object... mixParams);

//	int saveOrUpdate(T entity);

	int saveSelective(T entity);

	int save(T entity);

	int saveBatch(List<T> obs, String... paramColumns);

	int update(T entity, String... paramColumns);

	int updateSelective(T entity);

	int updateBatch(List<T> obs, String... paramColumns);

	int deleteById(Object id);

	int deleteBatch(List<Object> ids);

	int deleteBy(T entity);

	/*JSONObject resultSuccess(Object result, String msg, JSONObject json);

	JSONObject resultError(Object result, String msg, JSONObject json);*/
}