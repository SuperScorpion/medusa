package com.jy.medusa.stuff.param.gele;

public class GreatEqualParam extends BaseGeLeParam {

    public GreatEqualParam(String column, Object value) {
        this.setColumn(column);
        this.setValue(value);
    }

    /*public GreatEqualParam setValue(Object value) {
        this.value = value;
        return this;
    }*/

    public void setValue(Object value) {
        this.value = value;
    }
}