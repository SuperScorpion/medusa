package com.jy.medusa.stuff.param.gele;

public class LessEqualParam extends BaseGeLeParam {

    public LessEqualParam(String column) {
        this.setColumn(column);
    }

    public LessEqualParam setValue(Object value) {
        this.value = value;
        return this;
    }
}