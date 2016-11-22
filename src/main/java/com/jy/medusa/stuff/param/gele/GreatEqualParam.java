package com.jy.medusa.stuff.param.gele;

public class GreatEqualParam extends BaseGeLeParam {

    public GreatEqualParam(String column) {
        this.setColumn(column);
    }

    public GreatEqualParam setValue(Object value) {
        this.value = value;
        return this;
    }
}