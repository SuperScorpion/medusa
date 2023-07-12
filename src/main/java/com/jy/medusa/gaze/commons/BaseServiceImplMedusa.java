package com.jy.medusa.gaze.commons;

import java.io.Serializable;
import java.util.List;

/**
 * 基础接口
 * fix before : BaseServiceMedusa - BaseServiceMedusaString - BaseServiceMedusaLambda
 * fix after : BaseServiceMedusa -  BaseServiceMedusaLambda
 * @param <T> 参数
 * @author SuperScorpion
 */
public abstract class BaseServiceImplMedusa<T> extends BaseServiceImplMedusaLambda<T> implements BaseServiceMedusa<T> {

	public int insertSelective(T entity) {//
		return mapper.insertSelective(entity);
	}

	public int insert(T entity) {//
		return mapper.insert(entity);
	}

	public int insertBatch(List<T> obs) {
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

	public int deleteBatchByIds(List<? extends Serializable> ids) {//
		return mapper.deleteBatch(ids);
	}

	public int delete(T entity) {//
		return mapper.delete(entity);
	}
}