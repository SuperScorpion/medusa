package com.jy.medusa.gaze.commons;

import com.jy.medusa.gaze.stuff.param.MedusaLambdaColumns;
import com.jy.medusa.gaze.stuff.param.lambda.HolyGetPropertyNameLambda;
import com.jy.medusa.gaze.stuff.param.lambda.HolyGetter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;

//@Service
public abstract class BaseServiceImplLambdaBasis<T> {

	//	@Autowired
	protected Mapper<T> mapper;

	//	@Autowired
	protected void initMapper (Mapper<T> mapper) {
		this.mapper = mapper;
	}

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
			}/* else if (param instanceof HolyGetter) {//Object is not a functionInterface
                lit.add(HolyGetPropertyNameLambda.convertToFieldName((HolyGetter<T>)param));
            } else {
			    //do nothing
            }*/
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

		Object[] result = paramList.toArray(new Object[]{});

		//help gc
		if(paramList != null) {
			paramList.clear();
			paramList = null;
		}

		return result;
	}

	protected String[] transferStringColumnByLambda(HolyGetter<T>[] paramFns) {

		List<Object> paramList = new ArrayList<>();

		for (HolyGetter<T> fns : paramFns) {
			paramList.add(HolyGetPropertyNameLambda.convertToFieldName(fns));
		}

		return paramList.toArray(new String[]{});
	}
}