package com.jy.medusa.gaze.stuff.param.mix;

public class SingleParam extends BaseComplexParam {

    public SingleParam(String column, Object value, Boolean p) {
        this.setColumn(column);
        this.setValue(value);
        this.neq = p;
    }

    Boolean neq;

    Object value;


    public Boolean getNeq() {
        return neq;
    }

    public void setNeq(Boolean neq) {
        this.neq = neq;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}