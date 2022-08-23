package com.jy.medusa.gaze.commons;

import com.jy.medusa.gaze.stuff.param.MedusaLambdaColumns;
import com.jy.medusa.gaze.stuff.param.MedusaLambdaMap;
import com.jy.medusa.gaze.stuff.param.lambda.HolyGetPropertyNameLambda;
import com.jy.medusa.gaze.stuff.param.lambda.HolyGetter;

import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

//@Service
public abstract class BaseServiceImplLambdaBasis<T> {

	/**
	 * modify by neo on 20220723
	 * 不依赖于spring包 所以只能使用@Resource或@Inject
	 * 此处子类依赖注入如果使用@Resource 会报NoUniqueBeanDefinitionException
	 * 只能使用依赖类型注入 @Autowired和@Inject 等效
     * 思考: @Resource 注入的优先级为 Match by 1.Name2.Type3.Qualifier byType是否不支持泛型
	 */
//    @Resource
//	@Autowired
	@Inject
	protected Mapper<T> mapper;

//	@Resource
//	@Autowired
//	protected void initMapper (Mapper<T> mapper) {
//		this.mapper = mapper;
//	}

	protected Object[] transferLambdaForGaze(Object[] paramObjs) {

		if(paramObjs == null || paramObjs.length == 0) return new Object[]{};

//		List<Object> paramList = Arrays.asList(paramObjs);//https://blog.csdn.net/x541211190/article/details/79597236

        List<Object> paramList= Arrays.stream(paramObjs).collect(Collectors.toList());

//        List<Object> paramList = new ArrayList<>(paramObjs.length);
//		Collections.addAll(paramList, paramObjs);

		ListIterator<Object> lit = paramList.listIterator();
		while (lit.hasNext()) {
			Object param = lit.next();
			if (param instanceof MedusaLambdaColumns) {
				for (Object fns : ((MedusaLambdaColumns) param).getParamList()) {
					if (fns instanceof HolyGetter) {
						lit.add(HolyGetPropertyNameLambda.convertToFieldName((HolyGetter<T>)fns));
					}
				}
			} else if (param instanceof MedusaLambdaMap) {

				HashMap<String, Object> resultMap = new HashMap<>(((MedusaLambdaMap) param).size());

				Set<Map.Entry<HolyGetter<?>, Object>> entrySet = ((Map)param).entrySet();
				Iterator<Map.Entry<HolyGetter<?>, Object>> iter = entrySet.iterator();

				while(iter.hasNext()) {
					Map.Entry<HolyGetter<?>, Object> entry = iter.next();
					if (entry != null && entry.getKey() instanceof HolyGetter<?> && entry.getValue() != null) {//modify by neo on 2020.01.19
						if(entry.getKey() == null) continue;
						String fieldName = HolyGetPropertyNameLambda.convertToFieldName(entry.getKey());
						resultMap.put(fieldName, entry.getValue());
					}
				}
				lit.add(resultMap);
			}/*else if (param instanceof HolyGetter) {//Object is not a functionInterface
                lit.add(HolyGetPropertyNameLambda.convertToFieldName((HolyGetter<T>)param));
            } else {
			    //do nothing
            }*/
		}

		return paramList.toArray(new Object[]{});
	}

	protected String[] transferStringColumnByLambda(HolyGetter<T>[] paramFns) {

		List<Object> paramList = new ArrayList<>();

		for (HolyGetter<T> fns : paramFns) {
			paramList.add(HolyGetPropertyNameLambda.convertToFieldName(fns));
		}

		return paramList.toArray(new String[]{});
	}
}