package com.jy.medusa.gaze.stuff.param.base;

/**
 * 多条件查询参数基类
 * @author SuperScorpion
 */
public abstract class BaseParam {

    String column;

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }
}