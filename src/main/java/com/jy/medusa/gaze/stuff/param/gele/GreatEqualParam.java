package com.jy.medusa.gaze.stuff.param.gele;

public class GreatEqualParam extends BaseGeLeParam {

    public GreatEqualParam(String column, Object value) {
        this.setColumn(column);
        this.setValue(value);
    }

    public void setValue(Object value) {
        this.value = value;
    }
}