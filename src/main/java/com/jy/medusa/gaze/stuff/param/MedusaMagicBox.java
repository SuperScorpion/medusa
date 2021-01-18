package com.jy.medusa.gaze.stuff.param;

import com.jy.medusa.gaze.stuff.Pager;
import com.jy.medusa.gaze.stuff.param.lambda.HolyGetter;

import java.util.ArrayList;
import java.util.List;

public class MedusaMagicBox {

    public static Pager getPager() {
        return Pager.getPager();
    }

    public static MedusaStringRestrictions getStringRestrictions() {
        return MedusaStringRestrictions.getRestrictions();
    }

    public static MedusaLambdaRestrictions getLambdaRestrictions() {
        return MedusaLambdaRestrictions.getRestrictions();
    }

    public static List getLambdaColums() {
        return new ArrayList();
    }
}
