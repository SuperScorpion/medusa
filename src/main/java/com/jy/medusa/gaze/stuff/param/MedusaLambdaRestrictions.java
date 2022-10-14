package com.jy.medusa.gaze.stuff.param;

import com.jy.medusa.gaze.stuff.Pager;
import com.jy.medusa.gaze.stuff.param.gele.GreatEqualParam;
import com.jy.medusa.gaze.stuff.param.gele.GreatThanParam;
import com.jy.medusa.gaze.stuff.param.gele.LessEqualParam;
import com.jy.medusa.gaze.stuff.param.gele.LessThanParam;
import com.jy.medusa.gaze.stuff.param.lambda.HolyGetPropertyNameLambda;
import com.jy.medusa.gaze.stuff.param.lambda.HolyGetter;
import com.jy.medusa.gaze.stuff.param.mix.*;
import com.jy.medusa.gaze.stuff.param.sort.GroupByParam;
import com.jy.medusa.gaze.stuff.param.sort.OrderByParam;

import java.util.List;

/**
 * Created by neo on 2020/04/23.
 * 一个构造参数对象的类 方便用户使用 使用lambda获取属性名
 */
public class MedusaLambdaRestrictions<T> extends BaseRestrictions<T, HolyGetter<T>, MedusaLambdaRestrictions<T>> {

    public MedusaLambdaRestrictions<T> eqParam(HolyGetter<T> fn, Object v) {

        paramList.add(new SingleParam(HolyGetPropertyNameLambda.convertToFieldName(fn), v, false));
        return this;
    }

    public MedusaLambdaRestrictions<T> notEqParam(HolyGetter<T> fn, Object v) {

        paramList.add(new SingleParam(HolyGetPropertyNameLambda.convertToFieldName(fn), v, true));
        return this;
    }

    public MedusaLambdaRestrictions<T> isNotNullParam(HolyGetter<T> fn) {

        paramList.add(new NotNullParam(HolyGetPropertyNameLambda.convertToFieldName(fn), true));
        return this;
    }

    public MedusaLambdaRestrictions<T> isNullParam(HolyGetter<T> fn) {

        paramList.add(new NotNullParam(HolyGetPropertyNameLambda.convertToFieldName(fn), false));
        return this;
    }

    public MedusaLambdaRestrictions<T> notInParam(HolyGetter<T> fn, List v) {

        paramList.add(new NotInParam(HolyGetPropertyNameLambda.convertToFieldName(fn), v, true));
        return this;
    }

    public MedusaLambdaRestrictions<T> inParam(HolyGetter<T> fn, List v) {

        paramList.add(new NotInParam(HolyGetPropertyNameLambda.convertToFieldName(fn), v, false));
        return this;
    }

    public MedusaLambdaRestrictions<T> likeParam(HolyGetter<T> fn, String v) {

        paramList.add(new LikeParam(HolyGetPropertyNameLambda.convertToFieldName(fn), v));
        return this;
    }

    public MedusaLambdaRestrictions<T> betweenParam(HolyGetter<T> fn, Object start, Object end) {

        paramList.add(new BetweenParam(HolyGetPropertyNameLambda.convertToFieldName(fn), start, end));
        return this;
    }

    public MedusaLambdaRestrictions<T> greatEqualParam(HolyGetter<T> fn, Object v) {

        paramList.add(new GreatEqualParam(HolyGetPropertyNameLambda.convertToFieldName(fn), v));
        return this;
    }

    public MedusaLambdaRestrictions<T> greatThanParam(HolyGetter<T> fn, Object v) {

        paramList.add(new GreatThanParam(HolyGetPropertyNameLambda.convertToFieldName(fn), v));
        return this;
    }

    public MedusaLambdaRestrictions<T> lessEqualParam(HolyGetter<T> fn, Object v) {

        paramList.add(new LessEqualParam(HolyGetPropertyNameLambda.convertToFieldName(fn), v));
        return this;
    }

    public MedusaLambdaRestrictions<T> lessThanParam(HolyGetter<T> fn, Object v) {

        paramList.add(new LessThanParam(HolyGetPropertyNameLambda.convertToFieldName(fn), v));
        return this;
    }

    public MedusaLambdaRestrictions<T> clear() {

        this.paramList.clear();
        return this;
    }

    public MedusaLambdaRestrictions<T> remove(int index) {

        this.paramList.remove(index);
        return this;
    }

    public MedusaLambdaRestrictions<T> removeLast() {

        this.paramList.remove(paramList.size() - 1);
        return this;
    }

    public static MedusaLambdaRestrictions getRestrictions() {
        return new MedusaLambdaRestrictions<>();
    }


    /////add by neo on 20221013 for order by and group by/////
    public MedusaLambdaRestrictions<T> orderByDescParam(HolyGetter<T> fn) {

        paramList.add(new OrderByParam(HolyGetPropertyNameLambda.convertToFieldName(fn), Pager.SortTypeEnum.SORT_DESC));
        return this;
    }

    public MedusaLambdaRestrictions<T> orderByAscParam(HolyGetter<T> fn) {

        paramList.add(new OrderByParam(HolyGetPropertyNameLambda.convertToFieldName(fn), Pager.SortTypeEnum.SORT_ASC));
        return this;
    }

    public MedusaLambdaRestrictions<T> groupByParam(HolyGetter<T> fn) {

        paramList.add(new GroupByParam(HolyGetPropertyNameLambda.convertToFieldName(fn)));
        return this;
    }
}
