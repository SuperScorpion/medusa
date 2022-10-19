package com.jy.medusa.gaze.commons;

import java.io.Serializable;
import java.util.List;

/**
 * 基础接口
 * before : BaseServiceMedusa - BaseServiceMedusaString - BaseServiceMedusaLambda
 * after : BaseServiceMedusa -  BaseServiceMedusaLambda
 * @param <T> 参数
 */
//@Service
public abstract class BaseServiceImplMedusa<T> extends BaseServiceImplMedusaLambda<T> implements BaseServiceMedusa<T> {

//	public List<T> selectAll() {
//		return mapper.selectAll();
//	}
//
//	public T selectOne(T entity) {
//		return mapper.selectOne(entity);
//	}
//
//	public List<T> selectByIds(List<Serializable> ids) {
//		return mapper.selectByPrimaryKeyBatch(ids);
//	}
//
//	public T selectById(Serializable id) {
//		return mapper.selectByPrimaryKey(id);
//	}
//
//	public List<T> selectListBy(T entity) {
//		return mapper.select(entity);
//	}

//	public int selectCount(Object... mixParams) {
//		return mapper.selectCount(mixParams);
//	}
//
//	public List<T> selectByGazeMagic(Object... mixParams) {
//		return mapper.medusaGazeMagic(mixParams);
//	}

	public int saveSelective(T entity) {//
		return mapper.insertSelective(entity);
	}

	public int save(T entity) {//
		return mapper.insert(entity);
	}

	public int saveBatch(List<T> obs) {
		return mapper.insertBatch(obs, null);
	}

	public int update(T entity) {
		return mapper.updateByPrimaryKey(entity);
	}

	public int updateSelective(T entity) {//
		return mapper.updateByPrimaryKeySelective(entity);
	}

	public int updateBatch(List<T> obs) {
		return mapper.updateByPrimaryKeyBatch(obs, null);
	}

	public int deleteById(Serializable id) {//
		return mapper.deleteByPrimaryKey(id);
	}

	public int deleteBatch(List<Serializable> ids) {//
		return mapper.deleteBatch(ids);
	}

	public int deleteBy(T entity) {//
		return mapper.delete(entity);
	}
}