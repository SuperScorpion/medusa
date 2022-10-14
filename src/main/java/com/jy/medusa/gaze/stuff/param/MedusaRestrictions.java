package com.jy.medusa.gaze.stuff.param;

import java.io.Serializable;
import java.util.List;

public interface MedusaRestrictions<T, P, R> extends Serializable {

    R eqParam(P fn, Object v);

    R notEqParam(P fn, Object v);

    R isNotNullParam(P fn);

    R isNullParam(P fn);

    R notInParam(P fn, List v);

    R inParam(P fn, List v);

    R likeParam(P fn, String v);

    R betweenParam(P fn, Object start, Object end);

    R greatEqualParam(P fn, Object v);

    R greatThanParam(P fn, Object v);

    R lessEqualParam(P fn, Object v);

    R lessThanParam(P fn, Object v);

    R clear();

    R remove(int index);

    R removeLast();
}