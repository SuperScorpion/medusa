package com.jy.medusa.gaze.stuff.param;

import com.jy.medusa.gaze.stuff.Pager;
import com.jy.medusa.gaze.stuff.param.gele.GreatEqualParam;
import com.jy.medusa.gaze.stuff.param.gele.GreatThanParam;
import com.jy.medusa.gaze.stuff.param.gele.LessEqualParam;
import com.jy.medusa.gaze.stuff.param.gele.LessThanParam;
import com.jy.medusa.gaze.stuff.param.lambda.HolyGetPropertyNameLambda;
import com.jy.medusa.gaze.stuff.param.lambda.HolyGetter;
import com.jy.medusa.gaze.stuff.param.mix.*;
import com.jy.medusa.gaze.stuff.param.orand.AndModelClass;
import com.jy.medusa.gaze.stuff.param.orand.BaseModelClass;
import com.jy.medusa.gaze.stuff.param.orand.OrModelClass;
import com.jy.medusa.gaze.stuff.param.sort.GroupByParam;
import com.jy.medusa.gaze.stuff.param.sort.OrderByParam;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SuperScorpion on 2020/04/23.
 * 一个构造参数对象的类 方便用户使用 使用lambda获取属性名
 */
public class MedusaLambdaRestrictions<T> extends MedusaLambdaRestrictionsSup<T> {


    public static MedusaLambdaRestrictions getRestrictions() {
        return new MedusaLambdaRestrictions<>();
    }

    /////add by SuperScorpion on 20221013 for order by and group by/////
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

    /////add by SuperScorpion on 20230113 for or and/////
    private List<MedusaLambdaRestrictionsSup> orModelList;

    private List<MedusaLambdaRestrictionsSup> andModelList;

    public List<MedusaLambdaRestrictionsSup> getOrModelList() {
        return orModelList;
    }

    public List<MedusaLambdaRestrictionsSup> getAndModelList() {
        return andModelList;
    }

    public MedusaLambdaRestrictions<T> or(MedusaLambdaRestrictionsSup omc) {
        if(orModelList == null) orModelList = new ArrayList<>();
        orModelList.add(omc);
        return this;
    }

    public MedusaLambdaRestrictions<T> and(MedusaLambdaRestrictionsSup omc) {
        if(andModelList == null) andModelList = new ArrayList<>();
        andModelList.add(omc);
        return this;
    }

    public BaseModelClass<T> orModel() {
        return new OrModelClass();
    }

    public BaseModelClass<T> andModel() {
        return new AndModelClass();
    }
}
