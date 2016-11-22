package com.jy.medusa.stuff.param;

public class SingleParam extends BaseComplexParam {

    SingleParam(String column) {
        this.setColumn(column);
    }

    Object value;

    public Object getValue() {
        return value;
    }

    public SingleParam setValue(Object value) {
        this.value = value;
        return this;
    }
}