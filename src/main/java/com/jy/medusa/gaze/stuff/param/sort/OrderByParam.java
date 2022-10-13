package com.jy.medusa.gaze.stuff.param.sort;

import com.jy.medusa.gaze.stuff.Pager;
import com.jy.medusa.gaze.stuff.param.base.BaseParam;

public class OrderByParam extends BaseSortParam {

    public OrderByParam(String column, Pager.SortTypeEnum value) {
        this.setColumn(column);
        this.setValue(value);
    }

    Pager.SortTypeEnum value;


    public Pager.SortTypeEnum getValue() {
        return value;
    }

    public void setValue(Pager.SortTypeEnum value) {
        this.value = value;
    }
}