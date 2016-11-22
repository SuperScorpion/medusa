package com.jy.medusa.stuff.param.gele;

public class LessThanParam extends BaseGeLeParam {

    public LessThanParam(String column) {
        this.setColumn(column);
    }

    public LessThanParam setValue(Object value) {
        this.value = value;
        return this;
    }
}