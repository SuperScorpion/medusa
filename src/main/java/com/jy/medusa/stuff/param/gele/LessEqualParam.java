package com.jy.medusa.stuff.param.gele;

public class LessEqualParam extends BaseGeLeParam {

    public LessEqualParam(String column, Object value) {
        this.setColumn(column);
        this.setValue(value);
    }

    /*public LessEqualParam setValue(Object value) {
        this.value = value;
        return this;
    }*/

    public void setValue(Object value) {
        this.value = value;
    }
}