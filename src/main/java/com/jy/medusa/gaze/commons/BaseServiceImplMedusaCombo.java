package com.jy.medusa.gaze.commons;

import com.jy.medusa.gaze.stuff.MedusaSqlHelper;
import com.jy.medusa.gaze.stuff.param.lambda.HolyGetter;
import com.jy.medusa.gaze.utils.MedusaReflectionUtils;

import java.io.Serializable;
import java.util.List;

public abstract class BaseServiceImplMedusaCombo<T> extends BaseServiceImplLambdaBasis<T> implements BaseServiceMedusaLambda<T> {

	public T selectById(Serializable id, HolyGetter<T>... paramFns) {
		return mapper.selectByPrimaryKey(id, paramFns);
	}

	public List<T> selectByIds(List<? extends Serializable> ids, HolyGetter<T>... paramFns) {
		return mapper.selectByPrimaryKeyBatch(ids, paramFns);
	}

	public List<T> selectAll(HolyGetter<T>... paramFns) {
		return mapper.selectAll(paramFns);
	}

	public T selectOneCombo(Serializable... mixParams) {
		return mapper.selectOneCombo(mixParams);
	}

	public int selectCountCombo(Serializable... mixParams) {
		return mapper.selectCountCombo(mixParams);
	}

	public List<T> selectMedusaCombo(Serializable... mixParams) {
		return mapper.selectMedusaCombo(mixParams);
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