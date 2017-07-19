package com.jy.medusa.utils;

import com.jy.medusa.stuff.exception.MedusaException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

/**
 * Created by neo on 16/7/27.
 */
public class MyDateUtils {

    /**定义常量**/
    public static final String DATE_FULL_STR = "yyyy-MM-dd HH:mm:ss";

    /**
     * @param date
     * @param regx
     * @return
     */
    public static String convertDateToStr(Date date, String regx){

        if(date == null) throw new MedusaException("Medusa: The date param is null");

        if(MyUtils.isBlank(regx)) regx = DATE_FULL_STR;

        SimpleDateFormat sdf = new SimpleDateFormat(regx);
        return sdf.format(date);
    }

    /**
     * 正则匹配日期时间并返回date对象格式化
     * @param dateStr
     * @return
     */
    public static Date convertStrToDate(String dateStr){

        if(MyUtils.isBlank(dateStr)) throw new MedusaException("Medusa: The date string param is null");
        dateStr = dateStr.split("\\.")[0];//处理末尾的.0
        SimpleDateFormat sdf;

        if(Pattern.compile(SystemConfigs.REGX_TIME_YYYY0mm0dd_SIMPLE_4).matcher(dateStr).matches()){
            sdf = new SimpleDateFormat("yyyy/MM/dd");
        } else if(Pattern.compile(SystemConfigs.REGX_TIME_YYYY0mm0dd_SIMPLE_3).matcher(dateStr).matches()){
            sdf = new SimpleDateFormat("yyyy.MM.dd");
        } else if(Pattern.compile(SystemConfigs.REGX_TIME_YYYY0mm0dd_SIMPLE_2).matcher(dateStr).matches()){
            sdf = new SimpleDateFormat("yyyy-MM-dd");
        } else if(Pattern.compile(SystemConfigs.REGX_TIME_YYYY0mm0dd_SIMPLE_1).matcher(dateStr).matches()){
            sdf = new SimpleDateFormat("yyyyMMdd");
        } else if(Pattern.compile(SystemConfigs.REGX_TIME_YYYY0mm0dd_SIMPLE_5).matcher(dateStr).matches()){
            sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        } else if(Pattern.compile(SystemConfigs.REGX_TIME_YYYY0mm0dd_SIMPLE_6).matcher(dateStr).matches()){
            sdf = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
        } else {
            throw new MedusaException("Medusa: The date string no one can match, please check it");
        }

        Date result = null;

        try {
            result = sdf.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return result;
    }

}
