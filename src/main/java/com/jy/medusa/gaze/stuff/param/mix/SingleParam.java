package com.jy.medusa.gaze.stuff.param.mix;

public class SingleParam extends BaseComplexParam {

    public SingleParam(String column, Object value) {
        this.setColumn(column);
        this.setValue(value);
    }

    Object value;

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}