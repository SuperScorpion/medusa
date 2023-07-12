package com.jy.medusa.gaze.commons;

import java.io.Serializable;
import java.util.List;

@Deprecated
public abstract class BaseServiceImplMedusaString<T> extends BaseServiceImplLambdaBasis<T> implements BaseServiceMedusaString<T> {

	/*public List<T> selectAll(String... paramColumns) {
		return mapper.selectAll(paramColumns);
	}

	public T selectOne(T entity, String... paramColumns) {
		return mapper.selectOne(entity, paramColumns);
	}

	public List<T> selectByIds(List<? extends Serializable> ids, String... paramColumns) {
		return mapper.selectByPrimaryKeyBatch(ids, paramColumns);
	}

	public T selectById(Serializable id, String... paramColumns) {
		return mapper.selectByPrimaryKey(id, paramColumns);
	}

//	public List<T> selectListBy(T entity, String... paramColumns) {
//		return mapper.select(entity, paramColumns);
//	}

	public int selectCount(Serializable... mixParams) {
		return mapper.selectCount(mixParams);
	}

	public List<T> selectByGazeMagic(Serializable... mixParams) {
		return mapper.medusaGazeMagic(mixParams);
	}


	public int saveBatch(List<T> obs, String... paramColumns) {
		return mapper.insertBatch(obs, null, paramColumns);
	}

	public int saveBatchInclude(List<T> obs, String... paramColumns) {
		return mapper.insertBatch(obs, false, paramColumns);
	}

	public int saveBatchExclude(List<T> obs, String... paramColumns) {
		return mapper.insertBatch(obs, true, paramColumns);
	}

	public int updateBatchInclude(List<T> obs, String... paramColumns) {
		return mapper.updateByPrimaryKeyBatch(obs, false, paramColumns);
	}

	public int updateBatchExclude(List<T> obs, String... paramColumns) {
		return mapper.updateByPrimaryKeyBatch(obs, true, paramColumns);
	}*/
}