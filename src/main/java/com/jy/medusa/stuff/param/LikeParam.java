package com.jy.medusa.stuff.param;

public class LikeParam extends BaseComplexParam {

    LikeParam(String column, String value) {
        this.setColumn(column);
        this.setValue(value);
    }

    String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}