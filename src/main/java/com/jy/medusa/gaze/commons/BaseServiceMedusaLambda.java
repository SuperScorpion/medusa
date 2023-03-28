package com.jy.medusa.gaze.commons;

import com.jy.medusa.gaze.stuff.param.lambda.HolyGetter;

import java.io.Serializable;
import java.util.List;

public interface BaseServiceMedusaLambda<T> {

	/**
	 * 查询全表的数据
	 * @param paramFns 可选结果集需要的列名(双冒号形式)
	 * @return 查询出来的list结果集
	 */
	List<T> selectAll(HolyGetter<T>... paramFns);

	/**
	 * 根据实体类参数查询第一个结果
	 * @param entity 实体类参数
	 * @param paramFns 可选结果集需要的列名(双冒号形式)
	 * @return 单个实体类结果
	 */
	T selectOne(T entity, HolyGetter<T>... paramFns);

	/**
	 * 根据主键集合批量查询
	 * @param ids 主键集合参数
	 * @param paramFns 可选结果集需要的列名(双冒号形式)
	 * @return 查询出来的list结果集
	 */
	List<T> selectByIds(List<Serializable> ids, HolyGetter<T>... paramFns);

	/**
	 * 根据主键单个查询
	 * @param id 主键参数
	 * @param paramFns 可选结果集需要的列名(双冒号形式)
	 * @return 单个实体类结果
	 */
    T selectById(Serializable id, HolyGetter<T>... paramFns);

	/**
	 * 这是个大招 万能查询方法 根据多条件查询数据条数
	 * @param mixParams 请参考{@link com.jy.medusa.gaze.base.select.SelectMedusaGazeMapper}
	 * @return 查询出的总条数
	 */
	int selectCount(Object... mixParams);

	/**
	 * 这是个大招 万能查询方法 根据多条件查询数据结果集
	 * @param mixParams 请参考{@link com.jy.medusa.gaze.base.select.SelectMedusaGazeMapper}
	 * @return 查询出来的list结果集
	 */
	List<T> selectByGazeMagic(Object... mixParams);

	/**
	 * 批量新增数据(可选包含的列名)
	 * @param obs 实体类集合参数
	 * @param paramFns 可选包含的列名(双冒号形式)
	 * @return 影响的行数
	 */
	int insertBatchInclude(List<T> obs, HolyGetter<T>... paramFns);

	/**
	 * 批量新增数据(可选排除的列名)
	 * @param obs 实体类集合参数
	 * @param paramFns 可选排除的列名(双冒号形式)
	 * @return 影响的行数
	 */
	int insertBatchExclude(List<T> obs, HolyGetter<T>... paramFns);

	/**
	 * 批量更新数据(可选包含的列名)
	 * @param obs 实体类集合参数
	 * @param paramFns 可选包含的列名(双冒号形式)
	 * @return 影响的行数
	 */
	int updateBatchInclude(List<T> obs, HolyGetter<T>... paramFns);

	/**
	 * 批量更新数据(可选排除的列名)
	 * @param obs 实体类集合参数
	 * @param paramFns 可选排除的列名(双冒号形式)
	 * @return 影响的行数
	 */
	int updateBatchExclude(List<T> obs, HolyGetter<T>... paramFns);

	/**
	 * 新增或者更新
	 * 判断主键是否在表里存在, 如果存在且不为空执行update语句, 如果主键不存在或为空, 执行insert语句, 空列都会被过滤
	 * @param entity 实体类参数
	 * @return 影响的行数
	 */
	int saveOrUpdate(T entity);
}