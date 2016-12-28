package com.jy.medusa.stuff.param;

import com.jy.medusa.stuff.Pager;
import com.jy.medusa.stuff.param.gele.GreatEqualParam;
import com.jy.medusa.stuff.param.gele.GreatThanParam;
import com.jy.medusa.stuff.param.gele.LessEqualParam;
import com.jy.medusa.stuff.param.gele.LessThanParam;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by neo on 2016/10/14.
 * 一个构造参数对象的类 方便用户使用
 */
public class MyRestrictions {

    /*public static BaseParam build(ParamEnums enums) {

        switch (enums) {
            case LIKE: return new LikeParam();
            case BETWEEN: return new BetweenParam();
            case GE: return new GreatEqualParam();
            case GT: return new GreatThanParam();
            case LE: return new LessEqualParam();
            case LT: return new LessThanParam();
            default: throw new RuntimeException("Query condition input for this type is not supported");
        }
    }*/

    MyRestrictions() {
        this.paramList = new ArrayList<>();
    }

    private List<BaseParam> paramList;

    public List<BaseParam> getParamList() {
        return paramList;
    }

    public MyRestrictions likeParam(String c, String v) {

        paramList.add(new LikeParam(c, v));
        return this;
    }

    public MyRestrictions betweenParam(String c, Object start, Object end) {

        paramList.add(new BetweenParam(c, start, end));
        return this;
    }

    public MyRestrictions greatEqualParam(String c, Object v) {

        paramList.add(new GreatEqualParam(c, v));
        return this;
    }

    public MyRestrictions greatThanParam(String c, Object v) {

        paramList.add(new GreatThanParam(c, v));
        return this;
    }

    public MyRestrictions lessEqualParam(String c, Object v) {

        paramList.add(new LessEqualParam(c, v));
        return this;
    }

    public MyRestrictions lessThanParam(String c, Object v) {

        paramList.add(new LessThanParam(c, v));
        return this;
    }

    public MyRestrictions singleParam(String c, Object v) {

        paramList.add(new SingleParam(c, v));
        return this;
    }


    public static Pager getPager() {
        return new Pager();
    }

    public static MyRestrictions getMyRestrctions() {
        return new MyRestrictions();
    }

    public MyRestrictions clear() {
        this.paramList.clear();
        return this;
    }
}
