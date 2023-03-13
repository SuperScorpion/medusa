package com.jy.medusa.gaze.stuff.param;

import com.jy.medusa.gaze.stuff.param.gele.GreatEqualParam;
import com.jy.medusa.gaze.stuff.param.gele.GreatThanParam;
import com.jy.medusa.gaze.stuff.param.gele.LessEqualParam;
import com.jy.medusa.gaze.stuff.param.gele.LessThanParam;
import com.jy.medusa.gaze.stuff.param.lambda.HolyGetPropertyNameLambda;
import com.jy.medusa.gaze.stuff.param.lambda.HolyGetter;
import com.jy.medusa.gaze.stuff.param.mix.*;

import java.util.List;

/**
 * Created by SuperScorpion on 2023/03/13.
 * MedusaLambdaRestrictions - MedusaLambdaRestrictionsSup - BaseRestrictions
 * BaseModelClass - MedusaLambdaRestrictionsSup - BaseRestrictions
 * 一个构造参数对象的类 方便用户使用 使用lambda获取属性名
 */
public abstract class MedusaLambdaRestrictionsSup<T> extends BaseRestrictions<T, HolyGetter<T>, MedusaLambdaRestrictionsSup<T>> {

    public MedusaLambdaRestrictionsSup<T> eqParam(HolyGetter<T> fn, Object v) {

        paramList.add(new SingleParam(HolyGetPropertyNameLambda.convertToFieldName(fn), v, false));
        return this;
    }

    public MedusaLambdaRestrictionsSup<T> notEqParam(HolyGetter<T> fn, Object v) {

        paramList.add(new SingleParam(HolyGetPropertyNameLambda.convertToFieldName(fn), v, true));
        return this;
    }

    public MedusaLambdaRestrictionsSup<T> isNotNullParam(HolyGetter<T> fn) {

        paramList.add(new NotNullParam(HolyGetPropertyNameLambda.convertToFieldName(fn), true));
        return this;
    }

    public MedusaLambdaRestrictionsSup<T> isNullParam(HolyGetter<T> fn) {

        paramList.add(new NotNullParam(HolyGetPropertyNameLambda.convertToFieldName(fn), false));
        return this;
    }

    public MedusaLambdaRestrictionsSup<T> notInParam(HolyGetter<T> fn, List v) {

        paramList.add(new NotInParam(HolyGetPropertyNameLambda.convertToFieldName(fn), v, true));
        return this;
    }

    public MedusaLambdaRestrictionsSup<T> inParam(HolyGetter<T> fn, List v) {

        paramList.add(new NotInParam(HolyGetPropertyNameLambda.convertToFieldName(fn), v, false));
        return this;
    }

    public MedusaLambdaRestrictionsSup<T> likeParam(HolyGetter<T> fn, String v) {

        paramList.add(new LikeParam(HolyGetPropertyNameLambda.convertToFieldName(fn), v));
        return this;
    }

    public MedusaLambdaRestrictionsSup<T> betweenParam(HolyGetter<T> fn, Object start, Object end) {

        paramList.add(new BetweenParam(HolyGetPropertyNameLambda.convertToFieldName(fn), start, end));
        return this;
    }

    public MedusaLambdaRestrictionsSup<T> greatEqualParam(HolyGetter<T> fn, Object v) {

        paramList.add(new GreatEqualParam(HolyGetPropertyNameLambda.convertToFieldName(fn), v));
        return this;
    }

    public MedusaLambdaRestrictionsSup<T> greatThanParam(HolyGetter<T> fn, Object v) {

        paramList.add(new GreatThanParam(HolyGetPropertyNameLambda.convertToFieldName(fn), v));
        return this;
    }

    public MedusaLambdaRestrictionsSup<T> lessEqualParam(HolyGetter<T> fn, Object v) {

        paramList.add(new LessEqualParam(HolyGetPropertyNameLambda.convertToFieldName(fn), v));
        return this;
    }

    public MedusaLambdaRestrictionsSup<T> lessThanParam(HolyGetter<T> fn, Object v) {

        paramList.add(new LessThanParam(HolyGetPropertyNameLambda.convertToFieldName(fn), v));
        return this;
    }

    public MedusaLambdaRestrictionsSup<T> clear() {

        this.paramList.clear();
        return this;
    }

    public MedusaLambdaRestrictionsSup<T> remove(int index) {

        this.paramList.remove(index);
        return this;
    }

    public MedusaLambdaRestrictionsSup<T> removeLast() {

        this.paramList.remove(paramList.size() - 1);
        return this;
    }
}
