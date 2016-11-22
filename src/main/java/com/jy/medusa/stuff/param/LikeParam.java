package com.jy.medusa.stuff.param;

public class LikeParam extends BaseComplexParam {

    LikeParam(String column) {
        this.setColumn(column);
    }

    String value;

    public String getValue() {
        return value;
    }

    public LikeParam setValue(String value) {
        this.value = value;
        return this;
    }
}