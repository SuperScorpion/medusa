package com.jy.medusa.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;

/**
 * Created by neo on 16/7/27.
 */
public class MyDateUtils {

    /**定义常量**/
    public static final String DATE_FULL_STR = "yyyy-MM-dd HH:mm:ss";

    /**
     * 得到几天前的时间
     */
    public static Date getDateBefore(Date d, int day){
        Calendar now = Calendar.getInstance();
        now.setTime(d);
        now.set(Calendar.DATE,now.get(Calendar.DATE) - day);
        return now.getTime();
    }

    /**
     * 得到几天后的时间
     */
    public static Date getDateAfter(Date d, int day){
        Calendar now = Calendar.getInstance();
        now.setTime(d);
        now.set(Calendar.DATE,now.get(Calendar.DATE) + day);
        return now.getTime();
    }

    /**
     * @param date
     * @param regx
     * @return
     */
    public static String convertDateToStr(Date date, String regx){

        if(date == null) return "";

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

        if(MyUtils.isBlank(dateStr)) return null;
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
            return null;
        }

        Date result = null;

        try {
            result = sdf.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return result;
    }





//    public static void main(String[] args) {
        /*System.out.println(convertStrToDate("2016-07-25 17:22:43"));

        List d = new ArrayList();
        d.add('a');
        d.add('b');
        d.add('c');
        d.add('d');

        String  p  =  MyUtils.join(d, " AND ");

        System.out.println(p);

        HashMap m = new HashMap();
        m.put("l", "k");


        String s = (String) m.get("l");

        System.out.println(s);

        m.put("l", "p");

        System.out.println((String) m.get("l"));

        String mnn = "j, ,";
        String[] pmjk = MyUtils.split(mnn, ".");
        System.out.println(pmjk.length);

        StringBuilder sbbbbbbb = new StringBuilder();
        sbbbbbbb.append("k,k,l,p,o,j,p,");

        sbbbbbbb.deleteCharAt(sbbbbbbb.lastIndexOf(","));

        System.out.println(sbbbbbbb.toString());


        HashMap<String, Object> map = new HashMap<>();
        Pager ppppp = new Pager();
        ppppp.setPageSize(123);
        map.put("p", ppppp);

        Pager k = ((Pager)map.get("p"));
        k.setPageSize(34);

        System.out.println(((Pager)map.get("p")).getPageSize());


        String j = "@Length(max = 89,min = 1)&&@Validator(regExp = MyPattern.REGX_ALPHANUM)";
        System.out.println(j instanceof String);



        String[] b = j.split("&&");
        System.out.println(b.length);

        String[] c = null;
        for(String string : c) {
        }

        List<String> kjh = new ArrayList<>();
        kjh.add("o");
        kjh.add("o");
        kjh.add("o");
        kjh.add("p");
        kjh.add("o");
        kjh.add("o");
        kjh.add("o");
        kjh.add("o");
        kjh.add("o");
        kjh.add("o");
        kjh.add("o");
        kjh.add("p");
        kjh.add("o");

        for(String shb : kjh) {
            if(shb.equals("p")) {
                kjh.remove("p");
            }
        }

        System.out.println("///////" + kjh.toArray());*/

   /*      String[] v = {"t", "b", "c"};
       List<String> r = Arrays.asList(v.split("/"));
        System.out.println(Arrays.toString(v));
        System.out.println(String.join(",", v));*/

/*
        String k = "a,b ,c";
        String[] m = k.split(",");
        System.out.println(m[0]);

        Map l = new HashMap();
        l.put(" m", "bnnnmn");
        System.out.println(l.get(" m"));
*/

/*        BigDecimal resultMoney = new BigDecimal(176.5555).setScale(3, BigDecimal.ROUND_HALF_UP);//添加 四舍五入 保留两位小数点

        System.out.println(resultMoney.doubleValue());*/
/*
        String bnm = "SELECT COUNT(1)  FROM users WHERE name=#{pobj.param1.name} AND updated_at BETWEEN #{pobj.param2[1].start, jdbcType=TIMESTAMP} AND #{pobj.param2[1].end ,jdbcType=TIMESTAMP}";
        String handSql = bnm.replaceAll("\\#\\{[^\\#]+\\}", "?");
        System.out.println(handSql);

        String fnm = "1,id,3,p,9,7";
        String fdd = fnm.replaceAll("id,", "");
        System.out.println(fdd);*/

        /*String bnm = "SELECT a,j,m,h,l,f_fdsa FROM users WHERE name=#{pobj.param1.name} AND updated_at BETWEEN #{pobj.param2[1].start, jdbcType=TIMESTAMP} AND #{pobj.param2[1].end ,jdbcType=TIMESTAMP} order by id desc limit 0,1";
        String handSql = bnm.replaceAll("SELECT\\b.*\\bFROM", "SELECT COUNT(1) FROM").replaceAll("\\border by\\b.*", "");
        System.out.println(handSql);*/
//    }
}
