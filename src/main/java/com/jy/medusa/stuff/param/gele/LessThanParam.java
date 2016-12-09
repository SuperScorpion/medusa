package com.jy.medusa.stuff.param.gele;

public class LessThanParam extends BaseGeLeParam {

    public LessThanParam(String column, Object value) {
        this.setColumn(column);
        this.setValue(value);
    }

    /*public LessThanParam setValue(Object value) {
        this.value = value;
        return this;
    }*/

    public void setValue(Object value) {
        this.value = value;
    }
}