package com.jy.medusa.gaze.commons;

import com.jy.medusa.gaze.stuff.MedusaSqlHelper;
import com.jy.medusa.gaze.stuff.param.lambda.HolyGetter;
import com.jy.medusa.gaze.utils.MedusaReflectionUtils;

import java.io.Serializable;
import java.util.List;

public abstract class BaseServiceImplMedusaLambda<T> extends BaseServiceImplLambdaBasis<T> implements BaseServiceMedusaLambda<T> {

	public List<T> selectAll(HolyGetter<T>... paramFns) {
		return mapper.selectAll(paramFns);
	}

	public T selectOne(T entity, HolyGetter<T>... paramFns) {
		return mapper.selectOne(entity, paramFns);
	}

	public List<T> selectByIds(List<Serializable> ids, HolyGetter<T>... paramFns) {
		return mapper.selectByPrimaryKeyBatch(ids, paramFns);
	}

	public T selectById(Serializable id, HolyGetter<T>... paramFns) {
		return mapper.selectByPrimaryKey(id, paramFns);
	}

	public int selectCount(Object... mixParams) {
		return mapper.selectCount(mixParams);
	}

	public List<T> selectByGazeMagic(Object... mixParams) {
		return mapper.medusaGazeMagic(mixParams);
	}

	public int insertBatchInclude(List<T> obs, HolyGetter<T>... paramFns) {
		return mapper.insertBatch(obs, Boolean.FALSE, paramFns);
	}

	public int insertBatchExclude(List<T> obs, HolyGetter<T>... paramFns) {
		return mapper.insertBatch(obs, Boolean.TRUE, paramFns);
	}

	public int updateBatchInclude(List<T> obs, HolyGetter<T>... paramFns) {
		return mapper.updateByPrimaryKeyBatch(obs, Boolean.FALSE, paramFns);
	}

	public int updateBatchExclude(List<T> obs, HolyGetter<T>... paramFns) {
		return mapper.updateByPrimaryKeyBatch(obs, Boolean.TRUE, paramFns);
	}

	public int saveOrUpdate(T entity) {
		int result = 0;
		if(entity == null) return result;
		Object pkValue = MedusaReflectionUtils.obtainFieldValue(entity,
				MedusaSqlHelper.getSqlGeneratorByClass(entity.getClass()).getPkPropertyName());

		if(pkValue != null) {
			if(mapper.selectByPrimaryKey((Serializable) pkValue) != null) {//update
				result = mapper.updateByPrimaryKeySelective(entity);
			} else {//save
				result = mapper.insertSelective(entity);
			}
		} else {//save
			result = mapper.insertSelective(entity);
		}

		return result;
	}
}