package com.jy.medusa.validator;

/**
 * Created by neo on 16/6/28.
 */
public class MyPattern {

    /**
     *  20160905验证的比较复杂
     */
    public static final String REGX_TIME_yyyyMMdd_COMPLEX = "(([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3})(((0[13578]|1[02])(0[1-9]|[12][0-9]|3[01]))|((0[469]|11)(0[1-9]|[12][0-9]|30))|(02(0[1-9]|[1][0-9]|2[0-8]))))|((([0-9]{2})(0[48]|[2468][048]|[13579][26])|((0[48]|[2468][048]|[3579][26])00))0229)";

    /**
     * 20160905简单验证通过的
     */
    public static final String REGX_TIME_yyyy0MM0dd_SIMPLE_1 = "[0-9]{4}[0-9]{2}[0-9]{2}";

    /**
     * 2016-09-25
     */
    public static final String REGX_TIME_yyyy0MM0dd_SIMPLE_2 = "^[1-9][0-9]{3}-(0[1-9]|1[0-2]|[1-9])-([0-2][1-9]|3[0-1]|[1-9])$";

    /**
     * 2016.09.01
     */
    public static final String REGX_TIME_yyyy0MM0dd_SIMPLE_3 = "^[1-9][0-9]{3}\\.(0[1-9]|1[0-2]|[1-9])\\.([0-2][1-9]|3[0-1]|[1-9])$";

    /**
     * 2015-06-09
     */
    public static final String REGX_TIME_yyyy0MM0dd_SIMPLE_4 = "^[1-9][0-9]{3}/(0[1-9]|1[0-2]|[1-9])/([0-2][1-9]|3[0-1]|[1-9])$";

    /**
     * 2016-09-05 12:31:47
     */
    public static final String REGX_TIME_yyyy0MM0dd_SIMPLE_5 = "^((((1[6-9]|[2-9]\\d)\\d{2})-(0?[13578]|1[02])-(0?[1-9]|[12]\\d|3[01]))|(((1[6-9]|[2-9]\\d)\\d{2})-(0?[13456789]|1[012])-(0?[1-9]|[12]\\d|30))|(((1[6-9]|[2-9]\\d)\\d{2})-0?2-(0?[1-9]|1\\d|2[0-8]))|(((1[6-9]|[2-9]\\d)(0[48]|[2468][048]|[13579][26])|((16|[2468][048]|[3579][26])00))-0?2-29-)) (20|21|22|23|[0-1]?\\d):[0-5]?\\d:[0-5]?\\d$";

    /**
     * 20160908 13:45:01
     */
    public static final String REGX_TIME_yyyy0MM0dd_SIMPLE_6 = "^((((1[6-9]|[2-9]\\d)\\d{2})(0?[13578]|1[02])(0?[1-9]|[12]\\d|3[01]))|(((1[6-9]|[2-9]\\d)\\d{2})(0?[13456789]|1[012])(0?[1-9]|[12]\\d|30))|(((1[6-9]|[2-9]\\d)\\d{2})-0?2-(0?[1-9]|1\\d|2[0-8]))|(((1[6-9]|[2-9]\\d)(0[48]|[2468][048]|[13579][26])|((16|[2468][048]|[3579][26])00))-0?2-29-)) (20|21|22|23|[0-1]?\\d):[0-5]?\\d:[0-5]?\\d$";


    /**
     *
     *
     */
    public static final String REGX_ALPHANUM = "^[0-9a-zA-Z]*$";

    /**
     *
     *
     */
    public static final String REGX_NUM = "^[0-9]*$";

    /**
     * email
     */
    public static final String REGX_EMAIL = "\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*";

    /**
     * url
     */
    public static final String REGX_URL = "[a-zA-z]+://[^\\s]*";

    /**
     * mobile num
     */
    public static final String REGX_MOBILE = "^1[3|4|5|7|8][0-9]{9}$";

    /**
     * id card
     */
    public static final String REGX_ID_CARD1 = "^[1-9]\\d{7}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}$";

    /**
     * id card
     */
    public static final String REGX_ID_CARD2 = "^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}([0-9]|X)$";

/*    public static void main(String[] args) {
        String u = "420822198909236150";
        Boolean k = u.matches(REGX_ID_CARD2);
        System.out.println(k);
    }*/

}