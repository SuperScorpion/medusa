package com.jy.medusa.gaze.stuff.param.mix;

import java.util.List;

public class NotInParam extends BaseComplexParam {

    public NotInParam(String column, List value, Boolean p) {
        this.setColumn(column);
        this.setValue(value);
        this.setNotIn(p);
    }

    Boolean notIn;

    List value;


    public Boolean getNotIn() {
        return notIn;
    }

    public void setNotIn(Boolean notIn) {
        this.notIn = notIn;
    }

    public List getValue() {
        return value;
    }

    public void setValue(List value) {
        this.value = value;
    }
}
