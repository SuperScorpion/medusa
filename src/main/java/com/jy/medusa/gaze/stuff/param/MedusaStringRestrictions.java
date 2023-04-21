package com.jy.medusa.gaze.stuff.param;

import com.jy.medusa.gaze.stuff.Pager;
import com.jy.medusa.gaze.stuff.param.gele.GreatEqualParam;
import com.jy.medusa.gaze.stuff.param.gele.GreatThanParam;
import com.jy.medusa.gaze.stuff.param.gele.LessEqualParam;
import com.jy.medusa.gaze.stuff.param.gele.LessThanParam;
import com.jy.medusa.gaze.stuff.param.mix.*;
import com.jy.medusa.gaze.stuff.param.sort.GroupByParam;
import com.jy.medusa.gaze.stuff.param.sort.OrderByParam;

import java.util.List;

/**
 * @deprecated
 * Created by SuperScorpion on 2016/10/14.
 * 一个构造参数对象的类 方便用户使用
 */
public class MedusaStringRestrictions<T> extends BaseRestrictions<T, String, MedusaStringRestrictions<T>> {

    public MedusaStringRestrictions eqParam(String c, Object v) {
        paramList.add(new SingleParam(c, v, false));
        return this;
    }

    public MedusaStringRestrictions notEqParam(String c, Object v) {
        paramList.add(new SingleParam(c, v, true));
        return this;
    }

    public MedusaStringRestrictions isNotNullParam(String c) {
        paramList.add(new NotNullParam(c, true));
        return this;
    }

    public MedusaStringRestrictions isNullParam(String c) {
        paramList.add(new NotNullParam(c, false));
        return this;
    }

    public MedusaStringRestrictions notInParam(String c, List v) {
        paramList.add(new NotInParam(c, v, true));
        return this;
    }

    public MedusaStringRestrictions inParam(String c, List v) {
        paramList.add(new NotInParam(c, v, false));
        return this;
    }

    public MedusaStringRestrictions likeParam(String c, String v) {
        paramList.add(new LikeParam(c, v));
        return this;
    }

    public MedusaStringRestrictions betweenParam(String c, Object start, Object end) {
        paramList.add(new BetweenParam(c, start, end));
        return this;
    }

    public MedusaStringRestrictions greatEqualParam(String c, Object v) {
        paramList.add(new GreatEqualParam(c, v));
        return this;
    }

    public MedusaStringRestrictions greatThanParam(String c, Object v) {
        paramList.add(new GreatThanParam(c, v));
        return this;
    }

    public MedusaStringRestrictions lessEqualParam(String c, Object v) {
        paramList.add(new LessEqualParam(c, v));
        return this;
    }

    public MedusaStringRestrictions lessThanParam(String c, Object v) {
        paramList.add(new LessThanParam(c, v));
        return this;
    }

    public MedusaStringRestrictions clear() {
        paramList.clear();
        return this;
    }

    public MedusaStringRestrictions remove(int index) {
        paramList.remove(index);
        return this;
    }

    public MedusaStringRestrictions removeLast() {
        paramList.remove(paramList.size() - 1);
        return this;
    }

    public static MedusaStringRestrictions getRestrictions() {
        return new MedusaStringRestrictions<>();
    }


    /////add by SuperScorpion on 20221013 for order by and group by/////
    public MedusaStringRestrictions orderByDescParam(String c) {
        paramList.add(new OrderByParam(c, Pager.SortTypeEnum.SORT_DESC));
        return this;
    }

    public MedusaStringRestrictions orderByAscParam(String c) {
        paramList.add(new OrderByParam(c, Pager.SortTypeEnum.SORT_ASC));
        return this;
    }

    public MedusaStringRestrictions groupByParam(String c) {
        paramList.add(new GroupByParam(c));
        return this;
    }
}
