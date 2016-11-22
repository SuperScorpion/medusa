package com.jy.medusa.stuff.param;

import java.util.Date;

public class BetweenParam extends BaseComplexParam {

    BetweenParam(String column) {
        this.setColumn(column);
    }

    Date start;

    Date end = new Date();///默认设为现在系统的时间

    public Date getEnd() {
        return end;
    }

    public BetweenParam setEnd(Date end) {
        this.end = end;
        return this;
    }

    public Date getStart() {
        return start;
    }

    public BetweenParam setStart(Date start) {
        this.start = start;
        return this;
    }
}