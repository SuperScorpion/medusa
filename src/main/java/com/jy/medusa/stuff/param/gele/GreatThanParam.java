package com.jy.medusa.stuff.param.gele;

public class GreatThanParam extends BaseGeLeParam {

    public GreatThanParam(String column) {
        this.setColumn(column);
    }

    public GreatThanParam setValue(Object value) {
        this.value = value;
        return this;
    }
}
