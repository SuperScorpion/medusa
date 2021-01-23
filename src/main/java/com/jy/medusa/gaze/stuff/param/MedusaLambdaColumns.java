package com.jy.medusa.gaze.stuff.param;

import com.jy.medusa.gaze.stuff.param.lambda.HolyGetter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MedusaLambdaColumns<T> implements Serializable {

    MedusaLambdaColumns() {
        this.paramList = new ArrayList<>();
    }

    private List<HolyGetter<T>> paramList;

    public List<HolyGetter<T>> getParamList() {
        return this.paramList;
    }

    public MedusaLambdaColumns<T> addColumn(HolyGetter<T> h) {

        paramList.add(h);
        return this;
    }

    public static MedusaLambdaColumns getLambdaColums() {
        return new MedusaLambdaColumns<>();
    }
}
