package com.jy.medusa.gaze.stuff.param.mix;

public class LikeParam extends BaseComplexParam {

    public LikeParam(String column, String value) {
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