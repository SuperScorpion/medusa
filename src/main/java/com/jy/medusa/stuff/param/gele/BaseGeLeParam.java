package com.jy.medusa.stuff.param.gele;

import com.jy.medusa.stuff.param.BaseParam;

/**
 * 大于 小于 大于等于 小于等于 的操作基类
 */
public abstract class BaseGeLeParam extends BaseParam {

    Object value;

    public Object getValue() {
        return value;
    }
}