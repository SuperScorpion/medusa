package com.jy.medusa.stuff.param;

import com.jy.medusa.stuff.Pager;
import com.jy.medusa.stuff.param.gele.GreatEqualParam;
import com.jy.medusa.stuff.param.gele.GreatThanParam;
import com.jy.medusa.stuff.param.gele.LessEqualParam;
import com.jy.medusa.stuff.param.gele.LessThanParam;

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
            default: throw new RuntimeException("不支持该类型的查询条件输入");
        }
    }*/

    public static LikeParam getLikeParam(String c) {
        return new LikeParam(c);
    }
    public static BetweenParam getBetweenParam(String c) {
        return new BetweenParam(c);
    }
    public static GreatEqualParam getGreatEqualParam(String c) {
        return new GreatEqualParam(c);
    }
    public static GreatThanParam getGreatThanParam(String c) {
        return new GreatThanParam(c);
    }
    public static LessEqualParam getLessEqualParam(String c) {
        return new LessEqualParam(c);
    }
    public static LessThanParam getLessThanParam(String c) {
        return new LessThanParam(c);
    }
    public static SingleParam getSingleParam(String c) {
        return new SingleParam(c);
    }

    public static Pager getPager() {
        return new Pager();
    }
}
