package com.jy.medusa.gaze.commons;

import javax.inject.Inject;

public abstract class BaseServiceImplLambdaBasis<T> {

	/**
	 * modify by neo on 20220723
	 * 不依赖于spring包 所以只能使用@Resource或@Inject
	 * 此处子类依赖注入如果使用@Resource 会报NoUniqueBeanDefinitionException
	 * 只能使用依赖类型注入 @Autowired和@Inject 等效
     * 思考: @Resource 注入的优先级为 Match by 1.Name2.Type3.Qualifier byType是否不支持泛型
	 */
	@Inject
	protected Mapper<T> mapper;
}