package com.jy.medusa.gaze.stuff.param.mix;

import java.util.Date;

public class BetweenParam extends BaseComplexParam {

    public BetweenParam(String column, Object start, Object end) {
        this.setColumn(column);
        this.setStart(start);

        if(end == null)
            this.setEnd(new Date());
        else
            this.setEnd(end);
    }

    Object start;

    Object end;

    public Object getStart() {
        return start;
    }

    public void setStart(Object start) {
        this.start = start;
    }

    public Object getEnd() {
        return end;
    }

    public void setEnd(Object end) {
        this.end = end;
    }
}