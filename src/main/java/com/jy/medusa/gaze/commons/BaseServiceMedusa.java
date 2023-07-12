package com.jy.medusa.gaze.commons;

import java.io.Serializable;
import java.util.List;

/**
 * 基础接口
 * before : BaseServiceMedusa - BaseServiceMedusaString - BaseServiceMedusaLambda
 * after : BaseServiceMedusa -  BaseServiceMedusaLambda
 * @param <T> 参数
 * @author SuperScorpion
 */
public interface BaseServiceMedusa<T> extends BaseServiceMedusaLambda<T> {

	/**
	 * 单个新增数据(会过滤空列)
	 * @param entity 实体类参数
	 * @return 影响的行数
	 */
	int insertSelective(T entity);

	/**
	 * 单个新增数据(不会过滤空列)
	 * @param entity 实体类参数
	 * @return 影响的行数
	 */
	int insert(T entity);

	/**
	 * 批量新增数据(所有列)
	 * @param obs 实体类集合参数
	 * @return 影响的行数
	 */
	int insertBatch(List<T> obs);

	/**
	 * 根据主键单个更新数据(不会过滤空列)
	 * @param entity 实体类参数
	 * @return 影响的行数
	 */
	int update(T entity);

	/**
	 * 根据主键单个更新数据(会过滤空列)
	 * @param entity 实体类参数
	 * @return 影响的行数
	 */
	int updateSelective(T entity);

	/**
	 * 根据主键批量更新数据(所有列)
	 * @param obs 实体类集合参数
	 * @return 影响的行数
	 */
	int updateBatch(List<T> obs);

	/**
	 * 根据主键单个删除数据
	 * @param id 主键参数
	 * @return 影响的行数
	 */
	int deleteById(Serializable id);

	/**
	 * 根据主键的list集合批量删除数据
	 * @param ids 主键的list集合参数
	 * @return 影响的行数
	 */
	int deleteBatchByIds(List<? extends Serializable> ids);

	/**
	 * 根据实体类对应条件删除数据
	 * @param entity 实体类参数
	 * @return 影响的行数
	 */
	int delete(T entity);
}