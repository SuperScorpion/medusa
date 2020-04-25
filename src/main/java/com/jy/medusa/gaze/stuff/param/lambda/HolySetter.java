package com.jy.medusa.gaze.stuff.param.lambda;

import java.io.Serializable;

/**
 * Created by neo on 2020/4/18.
 */

@FunctionalInterface
public interface HolySetter<T, U> extends Serializable {
    void apply(T t, U u);
}