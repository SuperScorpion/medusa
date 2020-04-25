package com.jy.medusa.gaze.stuff.param;

import com.jy.medusa.gaze.stuff.param.mix.BaseParam;

import java.util.List;

public interface MedusaRestrictions<T, P, R> {

    R notInParam(P fn, List v, Boolean p);

    R likeParam(P fn, String v);

    R betweenParam(P fn, Object start, Object end);

    R greatEqualParam(P fn, Object v);

    R greatThanParam(P fn, Object v);

    R lessEqualParam(P fn, Object v);

    R lessThanParam(P fn, Object v);

    R singleParam(P fn, Object v);

    R notNullParam(P fn, Boolean v);

    R clear();
}