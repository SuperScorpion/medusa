package com.jy.medusa.gaze.commons;

import com.jy.medusa.gaze.stuff.param.lambda.HolyGetter;

import java.util.List;

//@Service
public abstract class BaseServiceImplMedusaLambda<T> extends BaseServiceImplBasis<T> implements BaseServiceMedusaLambda<T> {

	public List<T> selectAllLambda(HolyGetter<T>... paramFns) {
		return mapper.selectAll(transferStringColumnByLambda(paramFns));
	}

	public T selectOneLambda(T entity, HolyGetter<T>... paramFns) {
		return mapper.selectOne(entity, transferStringColumnByLambda(paramFns));
	}

	public List<T> selectByIdsLambda(List<Object> ids, HolyGetter<T>... paramFns) {
		return mapper.selectByPrimaryKeyBatch(ids, transferStringColumnByLambda(paramFns));
	}


	public T selectByIdLambda(Object id, HolyGetter<T>... paramFns) {
		return mapper.selectByPrimaryKey(id, transferStringColumnByLambda(paramFns));
	}

	public List<T> selectListByLambda(T entity, HolyGetter<T>... paramFns) {
		return mapper.select(entity, transferStringColumnByLambda(paramFns));
	}

	public int selectCountLambda(Object... mixParams) {
		return mapper.selectCount(transferLambdaForGaze(mixParams));
	}

	public List<T> selectByGazeLambda(Object... mixParams) {
		return mapper.showMedusaGaze(transferLambdaForGaze(mixParams));
	}

//	public int saveSelective(T entity) {
//		return mapper.insertSelective(entity);
//	}

//	public int save(T entity) {
//		return mapper.insert(entity);
//	}

	public int saveBatchLambda(List<T> obs, HolyGetter<T>... paramFns) {
		return mapper.insertBatch(obs, transferStringColumnByLambda(paramFns));
	}

	public int updateLambda(T entity, HolyGetter<T>... paramFns) {
		return mapper.updateByPrimaryKey(entity, transferStringColumnByLambda(paramFns));
	}

//	public int updateSelective(T entity) {
//		return mapper.updateByPrimaryKeySelective(entity);
//	}

	public int updateBatchLambda(List<T> obs, HolyGetter<T>... paramFns) {
		return mapper.updateByPrimaryKeyBatch(obs, transferStringColumnByLambda(paramFns));
	}

//	public int deleteById(Object id) {
//		return mapper.deleteByPrimaryKey(id);
//	}

//	public int deleteBatch(List<Object> ids) {
//		return mapper.deleteBatch(ids);
//	}

//	public int deleteBy(T entity) {
//		return mapper.delete(entity);
//	}

}