package com.jy.medusa.gaze.commons;

import java.util.List;

public interface BaseServiceMedusa<T> {

	int selectCount(Object... ps);

	List<T> selectAll(Object... ps);

	T selectOne(T entity, Object... ps);

	List<T> selectByIds(List<Object> ids, Object... ps);

	T selectById(Object id, Object... ps);

	List<T> selectListBy(T entity, Object... ps);

	int saveOrUpdate(T entity);

	int saveSelective(T entity);

	int save(T entity);

	int saveBatch(List<T> obs, Object... ps);

	int update(T entity, Object... ps);

	int updateSelective(T entity);

	int updateBatch(List<T> obs, Object... ps);

	int deleteById(Object id);

	int deleteBatch(List<Object> ids);

	int deleteBy(T entity);

	List<T> selectByGaze(Object... ps);

	/*JSONObject resultSuccess(Object result, String msg, JSONObject json);

	JSONObject resultError(Object result, String msg, JSONObject json);*/
}