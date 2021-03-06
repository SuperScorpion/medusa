package com.jy.medusa.gaze.commons;

import java.io.Serializable;
import java.util.List;

/**
 * 基础接口 BaseServiceImplMedusa - BaseServiceImplMedusaString - BaseServiceImplMedusaLambda
 * @param <T>
 */
//@Service
public abstract class BaseServiceImplMedusa<T> extends BaseServiceImplMedusaString<T> implements BaseServiceMedusa<T> {

	public List<T> selectAll() {
		return mapper.selectAll();
	}

	public T selectOne(T entity) {
		return mapper.selectOne(entity);
	}

	public List<T> selectByIds(List<Serializable> ids) {
		return mapper.selectByPrimaryKeyBatch(ids);
	}

	public T selectById(Serializable id) {
		return mapper.selectByPrimaryKey(id);
	}

	public List<T> selectListBy(T entity) {
		return mapper.select(entity);
	}

//	public int selectCount(Serializable... mixParams) {
//		return mapper.selectCount(mixParams);
//	}

//	public List<T> selectByGaze(Serializable... mixParams) {
//		return mapper.showMedusaGaze(mixParams);
//	}

	public int saveSelective(T entity) {
		return mapper.insertSelective(entity);
	}

	public int save(T entity) {
		return mapper.insert(entity);
	}

	public int saveBatch(List<T> obs) {
		return mapper.insertBatch(obs);
	}

	public int update(T entity) {
		return mapper.updateByPrimaryKey(entity);
	}

	public int updateSelective(T entity) {
		return mapper.updateByPrimaryKeySelective(entity);
	}

	public int updateBatch(List<T> obs) {
		return mapper.updateByPrimaryKeyBatch(obs);
	}

	public int deleteById(Serializable id) {
		return mapper.deleteByPrimaryKey(id);
	}

	public int deleteBatch(List<Serializable> ids) {
		return mapper.deleteBatch(ids);
	}

	public int deleteBy(T entity) {
		return mapper.delete(entity);
	}

	/*public JSONSerializable resultSuccess(Serializable result, String msg, JSONSerializable json) {
		json = json == null ? new JSONSerializable() : json;
		json.put("data", result);
		json.put("result",0);
		json.put("msg", msg);
		return json;
	}

	public JSONSerializable resultError(Serializable result, String msg, JSONSerializable json) {
		json = json == null ? new JSONSerializable() : json;
		json.put("data", result);
		json.put("result",1);
		json.put("msg", msg);
		return json;
	}*/
}