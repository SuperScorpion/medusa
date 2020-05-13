package com.jy.medusa.gaze.commons;

import com.jy.medusa.gaze.stuff.param.lambda.HolyGetPropertyNameLambda;
import com.jy.medusa.gaze.stuff.param.lambda.HolyGetter;

import java.util.*;

//@Service
public abstract class BaseServiceImplBasis<T> {

	//	@Autowired
	protected Mapper<T> mapper;

	//	@Autowired
	protected void initMapper (Mapper<T> mapper) {
		this.mapper = mapper;
	}

	protected Object[] transferLambdaForGaze(Object[] paramObjs) {

		if(paramObjs == null || paramObjs.length == 0) return new Object[]{};

//		List<Object> paramList = Arrays.asList(paramObjs);//https://blog.csdn.net/x541211190/article/details/79597236

		List< Object> paramList = new ArrayList<>(paramObjs.length);
		Collections.addAll(paramList, paramObjs);

		ListIterator<Object> lit = paramList.listIterator();
		while(lit.hasNext()) {
			Object param = lit.next();
			if(param instanceof Collection) {
				for (Object fns : (Collection) param) {
					if (fns instanceof HolyGetter) {
						lit.add(HolyGetPropertyNameLambda.convertToFieldName((HolyGetter<T>)fns));
					}
				}
			}
		}

//		paramList.forEach(param -> {
//			if(param instanceof Collection) {
//				((Collection) param).forEach(fns -> {
//					if (fns instanceof HolyGetter) {
//						paramList.add(HolyGetPropertyNameLambda.convertToFieldName((HolyGetter<T>)fns));
//					}
//				});
//			}
//		});

		return paramList.toArray(new Object[]{});
	}

	protected String[] transferStringColumnByLambda(HolyGetter<T>[] paramFns) {

		List<Object> paramList = new ArrayList<>();

		for(HolyGetter<T> fns : paramFns) {
			paramList.add(HolyGetPropertyNameLambda.convertToFieldName(fns));
		}

		return paramList.toArray(new String[]{});
	}
}