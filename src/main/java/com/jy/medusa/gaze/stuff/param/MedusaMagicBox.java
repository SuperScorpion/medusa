package com.jy.medusa.gaze.stuff.param;

import com.jy.medusa.gaze.stuff.Pager;
import com.jy.medusa.gaze.stuff.param.orand.AndModelClass;
import com.jy.medusa.gaze.stuff.param.orand.BaseModelClass;
import com.jy.medusa.gaze.stuff.param.orand.OrModelClass;

public class MedusaMagicBox {

    /**
     * 可获取内置分页对象
     * @return 分页对象
     */
    public static Pager getPager() {
        return Pager.getPager();
    }

    /**
     * 可通过此对象做字符串模式条件限制
     * @deprecated
     * @return 字符串模式条件限制对象
     */
    public static MedusaStringRestrictions getStringRestrictions() {
        return MedusaStringRestrictions.getRestrictions();
    }

    /**
     * 可通过此对象做lambda模式条件限制
     * @return lambda模式条件限制对象
     */
    public static MedusaLambdaRestrictions getLambdaRestrictions() {
        return MedusaLambdaRestrictions.getRestrictions();
    }

    /**
     * 可获取此对象设置需要查询的列
     * @return 查询的列对象
     */
    public static MedusaLambdaColumns getLambdaColums() {
        return MedusaLambdaColumns.getLambdaColums();
    }

    /**
     * 可获取此对象设置查询的map对象<br>
     * 此map的key为lambda形式
     * @return 查询的map对象
     */
    public static MedusaLambdaMap getLambdaMap() {
        return new MedusaLambdaMap();
    }

    /**
     * MedusaLambdaRestrictions 复杂语句条件拼接时使用 作为and(...)或or(...)里的参数
     * @return
     */
    public BaseModelClass orBuilder() {
        return new OrModelClass();
    }

    /**
     * MedusaLambdaRestrictions 复杂语句条件拼接时使用 作为and(...)或or(...)里的参数
     * @return
     */
    public BaseModelClass andBuilder() {
        return new AndModelClass();
    }
}
