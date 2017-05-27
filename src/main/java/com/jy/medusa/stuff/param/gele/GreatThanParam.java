package com.jy.medusa.stuff.param.gele;

public class GreatThanParam extends BaseGeLeParam {

    public GreatThanParam(String column, Object value) {
        this.setColumn(column);
        this.setValue(value);
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
