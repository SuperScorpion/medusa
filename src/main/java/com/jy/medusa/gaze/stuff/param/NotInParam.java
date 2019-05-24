package com.jy.medusa.gaze.stuff.param;

import java.util.List;

public class NotInParam extends BaseComplexParam {

    NotInParam(String column, List value, Boolean p) {
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
