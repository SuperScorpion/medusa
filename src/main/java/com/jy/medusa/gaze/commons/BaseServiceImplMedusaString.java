package com.jy.medusa.gaze.commons;

import java.io.Serializable;
import java.util.List;

//@Service
public abstract class BaseServiceImplMedusaString<T> extends BaseServiceImplMedusaLambda<T> implements BaseServiceMedusaString<T> {

	public List<T> selectAll(String... paramColumns) {
		return mapper.selectAll(paramColumns);
	}

	public T selectOne(T entity, String... paramColumns) {
		return mapper.selectOne(entity, paramColumns);
	}

	public List<T> selectByIds(List<Serializable> ids, String... paramColumns) {
		return mapper.selectByPrimaryKeyBatch(ids, paramColumns);
	}

	public T selectById(Serializable id, String... paramColumns) {
		return mapper.selectByPrimaryKey(id, paramColumns);
	}

	public List<T> selectListBy(T entity, String... paramColumns) {
		return mapper.select(entity, paramColumns);
	}

//	public int selectCount(Object... mixParams) {
//		return mapper.selectCount(mixParams);
//	}

//	public List<T> selectByGaze(Object... mixParams) {
//		return mapper.medusaGazeMagic(mixParams);
//	}

//	public int saveSelective(T entity) {
//		return mapper.insertSelective(entity);
//	}
//
//	public int save(T entity) {
//		return mapper.insert(entity);
//	}

	public int saveBatch(List<T> obs, String... paramColumns) {
		return mapper.insertBatch(obs, paramColumns);
	}

	public int update(T entity, String... paramColumns) {
		return mapper.updateByPrimaryKey(entity, paramColumns);
	}

//	public int updateSelective(T entity) {
//		return mapper.updateByPrimaryKeySelective(entity);
//	}

	public int updateBatch(List<T> obs, String... paramColumns) {
		return mapper.updateByPrimaryKeyBatch(obs, paramColumns);
	}

//	public int deleteById(Serializable id) {
//		return mapper.deleteByPrimaryKey(id);
//	}

//	public int deleteBatch(List<Serializable> ids) {
//		return mapper.deleteBatch(ids);
//	}
//
//	public int deleteBy(T entity) {
//		return mapper.delete(entity);
//	}
}