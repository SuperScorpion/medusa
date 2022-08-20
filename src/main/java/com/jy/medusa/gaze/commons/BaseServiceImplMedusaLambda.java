package com.jy.medusa.gaze.commons;

import com.jy.medusa.gaze.stuff.param.lambda.HolyGetter;

import java.io.Serializable;
import java.util.List;

//@Service
public abstract class BaseServiceImplMedusaLambda<T> extends BaseServiceImplLambdaBasis<T> implements BaseServiceMedusaLambda<T> {

	public List<T> selectAll(HolyGetter<T>... paramFns) {
		return mapper.selectAll(transferStringColumnByLambda(paramFns));
	}

	public T selectOne(T entity, HolyGetter<T>... paramFns) {
		return mapper.selectOne(entity, transferStringColumnByLambda(paramFns));
	}

	public List<T> selectByIds(List<Serializable> ids, HolyGetter<T>... paramFns) {
		return mapper.selectByPrimaryKeyBatch(ids, transferStringColumnByLambda(paramFns));
	}

	public T selectById(Serializable id, HolyGetter<T>... paramFns) {
		return mapper.selectByPrimaryKey(id, transferStringColumnByLambda(paramFns));
	}

	public List<T> selectListBy(T entity, HolyGetter<T>... paramFns) {
		return mapper.select(entity, transferStringColumnByLambda(paramFns));
	}

//	public int selectCount(Object... mixParams) {
//		return mapper.selectCount(transferLambdaForGaze(mixParams));
//	}
//
//	public List<T> selectByGazeMagic(Object... mixParams) {
//		return mapper.showMedusaGaze(transferLambdaForGaze(mixParams));
//	}

//	public int saveSelective(T entity) {
//		return mapper.insertSelective(entity);
//	}

//	public int save(T entity) {
//		return mapper.insert(entity);
//	}

	public int saveBatch(List<T> obs, HolyGetter<T>... paramFns) {
		return mapper.insertBatch(obs, transferStringColumnByLambda(paramFns));
	}

	public int update(T entity, HolyGetter<T>... paramFns) {
		return mapper.updateByPrimaryKey(entity, transferStringColumnByLambda(paramFns));
	}

//	public int updateSelective(T entity) {
//		return mapper.updateByPrimaryKeySelective(entity);
//	}

	public int updateBatch(List<T> obs, HolyGetter<T>... paramFns) {
		return mapper.updateByPrimaryKeyBatch(obs, transferStringColumnByLambda(paramFns));
	}

//	public int deleteById(Serializable id) {
//		return mapper.deleteByPrimaryKey(id);
//	}

//	public int deleteBatch(List<Serializable> ids) {
//		return mapper.deleteBatch(ids);
//	}

//	public int deleteBy(T entity) {
//		return mapper.delete(entity);
//	}

}