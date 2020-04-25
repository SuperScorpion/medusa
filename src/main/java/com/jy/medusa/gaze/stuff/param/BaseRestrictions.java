package com.jy.medusa.gaze.stuff.param;

import com.jy.medusa.gaze.stuff.param.mix.BaseParam;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseRestrictions<T, P, R extends BaseRestrictions<T, P, R>> implements MedusaRestrictions<T, P, R> {

    BaseRestrictions() {
        this.paramList = new ArrayList<>();
    }

    protected List<BaseParam> paramList;

    public List<BaseParam> getParamList() {
        return this.paramList;
    }
}
