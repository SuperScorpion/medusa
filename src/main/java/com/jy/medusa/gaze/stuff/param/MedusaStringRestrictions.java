package com.jy.medusa.gaze.stuff.param;

import com.jy.medusa.gaze.stuff.param.gele.GreatEqualParam;
import com.jy.medusa.gaze.stuff.param.gele.GreatThanParam;
import com.jy.medusa.gaze.stuff.param.gele.LessEqualParam;
import com.jy.medusa.gaze.stuff.param.gele.LessThanParam;
import com.jy.medusa.gaze.stuff.param.lambda.HolyGetter;
import com.jy.medusa.gaze.stuff.param.mix.*;

import java.util.List;

/**
 * Created by neo on 2016/10/14.
 * 一个构造参数对象的类 方便用户使用
 */
public class MedusaStringRestrictions<T> extends BaseRestrictions<T, String, MedusaStringRestrictions<T>> {

    public MedusaStringRestrictions notInParam(String c, List v, Boolean p) {

        paramList.add(new NotInParam(c, v, p));
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

    public MedusaStringRestrictions singleParam(String c, Object v) {

        paramList.add(new SingleParam(c, v));
        return this;
    }

    public MedusaStringRestrictions notNullParam(String c, Boolean v) {

        paramList.add(new NotNullParam(c, v));
        return this;
    }

    public MedusaStringRestrictions clear() {

        this.paramList.clear();
        return this;
    }

    public static MedusaStringRestrictions getRestrictions() {
        return new MedusaStringRestrictions<>();
    }
}
