package com.jy.medusa.gaze.commons;

import java.util.List;

//@Service
public abstract class BaseServiceImplMedusa<T> extends BaseServiceImplMedusaLambda<T> implements BaseServiceMedusa<T> {

	public List<T> selectAll(String... paramColumns) {
		return mapper.selectAll(paramColumns);
	}

	public T selectOne(T entity, String... paramColumns) {
		return mapper.selectOne(entity, paramColumns);
	}

	public List<T> selectByIds(List<Object> ids, String... paramColumns) {
		return mapper.selectByPrimaryKeyBatch(ids, paramColumns);
	}

	public T selectById(Object id, String... paramColumns) {
		return mapper.selectByPrimaryKey(id, paramColumns);
	}

	public List<T> selectListBy(T entity, String... paramColumns) {
		return mapper.select(entity, paramColumns);
	}

	public int selectCount(Object... mixParams) {
		return mapper.selectCount(mixParams);
	}

	public List<T> selectByGaze(Object... mixParams) {
		return mapper.showMedusaGaze(mixParams);
	}

//	public int saveOrUpdate(T entity) {
//		return 0;//TODO
//	}

	public int saveSelective(T entity) {
		return mapper.insertSelective(entity);
	}

	public int save(T entity) {
		return mapper.insert(entity);
	}

	public int saveBatch(List<T> obs, String... paramColumns) {
		return mapper.insertBatch(obs, paramColumns);
	}

	public int update(T entity, String... paramColumns) {
		return mapper.updateByPrimaryKey(entity, paramColumns);
	}

	public int updateSelective(T entity) {
		return mapper.updateByPrimaryKeySelective(entity);
	}

	public int updateBatch(List<T> obs, String... paramColumns) {
		return mapper.updateByPrimaryKeyBatch(obs, paramColumns);
	}

	public int deleteById(Object id) {
		return mapper.deleteByPrimaryKey(id);
	}

	public int deleteBatch(List<Object> ids) {
		return mapper.deleteBatch(ids);
	}

	public int deleteBy(T entity) {
		return mapper.delete(entity);
	}

	/*public JSONObject resultSuccess(Object result, String msg, JSONObject json) {
		json = json == null ? new JSONObject() : json;
		json.put("data", result);
		json.put("result",0);
		json.put("msg", msg);
		return json;
	}

	public JSONObject resultError(Object result, String msg, JSONObject json) {
		json = json == null ? new JSONObject() : json;
		json.put("data", result);
		json.put("result",1);
		json.put("msg", msg);
		return json;
	}*/
}