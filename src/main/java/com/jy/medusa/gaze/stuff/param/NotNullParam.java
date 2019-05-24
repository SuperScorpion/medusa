package com.jy.medusa.gaze.stuff.param;

public class NotNullParam extends BaseComplexParam {

    NotNullParam(String column, Boolean value) {
        this.setColumn(column);
        this.setValue(value);
    }

    Boolean value;

    public Boolean getValue() {
        return value;
    }

    public void setValue(Boolean value) {
        this.value = value;
    }
}