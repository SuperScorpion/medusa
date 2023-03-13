package com.jy.medusa.gaze.commons;

import java.io.Serializable;
import java.util.List;

@Deprecated
public interface BaseServiceMedusaString<T> {

	List<T> selectAll(String... paramColumn);

	T selectOne(T entity, String... paramColumns);

	List<T> selectByIds(List<Serializable> ids, String... paramColumns);

	T selectById(Serializable id, String... paramColumns);

//	List<T> selectListBy(T entity, String... paramColumns);

	int selectCount(Object... mixParams);

	List<T> selectByGazeMagic(Object... mixParams);

//	int saveSelective(T entity);

//	int save(T entity);

	int saveBatchInclude(List<T> obs, String... paramColumns);

	int saveBatchExclude(List<T> obs, String... paramColumns);

//	int update(T entity, String... paramColumns);

//	int updateSelective(T entity);

	int updateBatchInclude(List<T> obs, String... paramColumns);

	int updateBatchExclude(List<T> obs, String... paramColumns);

//	int deleteById(Serializable id);

//	int deleteBatch(List<Serializable> ids);

//	int deleteBy(T entity);
}