package com.jy.medusa.gaze.stuff.param.gele;

public class LessEqualParam extends BaseGeLeParam {

    public LessEqualParam(String column, Object value) {
        this.setColumn(column);
        this.setValue(value);
    }

    public void setValue(Object value) {
        this.value = value;
    }
}