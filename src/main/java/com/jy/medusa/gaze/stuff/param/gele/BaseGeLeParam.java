package com.jy.medusa.gaze.stuff.param.gele;

import com.jy.medusa.gaze.stuff.param.base.BaseParam;

/**
 * 大于 小于 大于等于 小于等于 的操作基类
 */
public abstract class BaseGeLeParam extends BaseParam {

    Object value;

    public Object getValue() {
        return value;
    }
}