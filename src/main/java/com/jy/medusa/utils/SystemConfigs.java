package com.jy.medusa.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by neo on 16/6/28.
 */
public class SystemConfigs {
//    public static final String REGZ_TIME_YYYYmmdd_COMPLEX = "(([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3})(((0[13578]|1[02])(0[1-9]|[12][0-9]|3[01]))|((0[469]|11)(0[1-9]|[12][0-9]|30))|(02(0[1-9]|[1][0-9]|2[0-8]))))|((([0-9]{2})(0[48]|[2468][048]|[13579][26])|((0[48]|[2468][048]|[3579][26])00))0229)";
    public static final String REGX_TIME_YYYY0mm0dd_SIMPLE_1 = "^[1-9][0-9]{3}(0[1-9]|1[0-2]|[1-9])([0-2][1-9]|3[0-1]|[1-9])$";
    public static final String REGX_TIME_YYYY0mm0dd_SIMPLE_2 = "^[1-9][0-9]{3}-(0[1-9]|1[0-2]|[1-9])-([0-2][1-9]|3[0-1]|[1-9])$";
    public static final String REGX_TIME_YYYY0mm0dd_SIMPLE_3 = "^[1-9][0-9]{3}\\.(0[1-9]|1[0-2]|[1-9])\\.([0-2][1-9]|3[0-1]|[1-9])$";
    public static final String REGX_TIME_YYYY0mm0dd_SIMPLE_4 = "^[1-9][0-9]{3}/(0[1-9]|1[0-2]|[1-9])/([0-2][1-9]|3[0-1]|[1-9])$";
    public static final String REGX_TIME_YYYY0mm0dd_SIMPLE_5 = "^((((1[6-9]|[2-9]\\d)\\d{2})-(0?[13578]|1[02])-(0?[1-9]|[12]\\d|3[01]))|(((1[6-9]|[2-9]\\d)\\d{2})-(0?[13456789]|1[012])-(0?[1-9]|[12]\\d|30))|(((1[6-9]|[2-9]\\d)\\d{2})-0?2-(0?[1-9]|1\\d|2[0-8]))|(((1[6-9]|[2-9]\\d)(0[48]|[2468][048]|[13579][26])|((16|[2468][048]|[3579][26])00))-0?2-29-)) (20|21|22|23|[0-1]?\\d):[0-5]?\\d:[0-5]?\\d$";
    public static final String REGX_TIME_YYYY0mm0dd_SIMPLE_6 = "^((((1[6-9]|[2-9]\\d)\\d{2})(0?[13578]|1[02])(0?[1-9]|[12]\\d|3[01]))|(((1[6-9]|[2-9]\\d)\\d{2})(0?[13456789]|1[012])(0?[1-9]|[12]\\d|30))|(((1[6-9]|[2-9]\\d)\\d{2})-0?2-(0?[1-9]|1\\d|2[0-8]))|(((1[6-9]|[2-9]\\d)(0[48]|[2468][048]|[13579][26])|((16|[2468][048]|[3579][26])00))-0?2-29-)) (20|21|22|23|[0-1]?\\d):[0-5]?\\d:[0-5]?\\d$";

    public static final String VALID_PATTERN_PATH = "com.jy.medusa.validator.MyPattern";
    public static final String VALID_VALIDATOR_PATH = "com.jy.medusa.validator.annotation.Validator";
    public static final String VALID_LENGTH_PATH = "com.jy.medusa.validator.annotation.Length";
//    public static final String VALID_NOTNULL_PATH = "com.jy.medusa.validator.annotation.NotNull";

    public static final String MEDUSA_PAGER_PATH = "com.jy.medusa.stuff.Pager";
    public static final String MEDUSA_MYRESTRICTION_PATH = "com.jy.medusa.stuff.param.MyRestrictions";

    public static final List<String> MY_ALL_METHOD_NANES_LIST = new ArrayList<>(20);

    static {
        MY_ALL_METHOD_NANES_LIST.add("showMedusaGaze");
        MY_ALL_METHOD_NANES_LIST.add("selectAll");
        MY_ALL_METHOD_NANES_LIST.add("selectByPrimaryKey");
        MY_ALL_METHOD_NANES_LIST.add("selectCount");
        MY_ALL_METHOD_NANES_LIST.add("select");
        MY_ALL_METHOD_NANES_LIST.add("selectOne");
        MY_ALL_METHOD_NANES_LIST.add("selectPage");
        MY_ALL_METHOD_NANES_LIST.add("insertSelective");
        MY_ALL_METHOD_NANES_LIST.add("insertBatch");
        MY_ALL_METHOD_NANES_LIST.add("insert");
        MY_ALL_METHOD_NANES_LIST.add("insertSelectiveUUID");
        MY_ALL_METHOD_NANES_LIST.add("updateByPrimaryKey");
        MY_ALL_METHOD_NANES_LIST.add("updateByPrimaryKeySelective");
        MY_ALL_METHOD_NANES_LIST.add("updateByPrimaryKeyBatch");
        MY_ALL_METHOD_NANES_LIST.add("deleteBatch");
        MY_ALL_METHOD_NANES_LIST.add("deleteByPrimaryKey");
        MY_ALL_METHOD_NANES_LIST.add("delete");
    }

    public static final String PRIMARY_KEY = "id";
}