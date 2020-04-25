package com.jy.medusa.generator;

import com.jy.medusa.gaze.utils.MedusaCommonUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by neo on 16/8/4.
 */
public class MedusaGenUtils {

    /**
     * 驼峰命名---改为下划线 如  updatedAT ---updated_at
     * @param param 参数
     * @return 返回值类型
     */
    public static String camelToUnderline(String param) {

        if (MedusaCommonUtils.isBlank(param)) return "";

        int i = 0, len = param.length();

        StringBuilder sb = new StringBuilder(len+2);

        for (; i < len; i++) {

            char c = param.charAt(i);

            if (Character.isUpperCase(c)) {
                sb.append("_");
                sb.append(Character.toLowerCase(c));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * 把输入字符串的首字母改成小写
     * @param str 参数
     * @return 返回值类型
     */
    public static String lowcaseFirst(String str) {
        return getCamelStr(str.substring(0, 1).toLowerCase() + str.substring(1));
    }

    /**colSqlNames[i]
     * 把输入字符串的首字母改成大写 并且变成驼峰
     * @param str 参数
     * @return 返回值类型
     */
    public static String upcaseFirst(String str) {
        char[] ch = str.toCharArray();
        if (ch[0] >= 'a' && ch[0] <= 'z') {
            ch[0] = (char) (ch[0]-32);
        }
        return getCamelStr(new String(ch));
    }


    /**
     * 例：user_name --- userName
     * @param s 参数
     * @return 返回值类型
     */
    public static String getCamelStr(String s) {
        while(s.indexOf("_") > 0) {
            int index = s.indexOf("_");
            s = s.substring(0, index) + s.substring(index+1, index+2).toUpperCase() + s.substring(index+2);
        }
        return s;
    }


    /**
     * 添加剩余的标记段落 (方法名被修改了匹配不到  但是新文件继续保留下来)
     * 如果第一行位\n 并且只有这么一行 则不保留下来到新文件
     * @param markStrList 参数
     * @param sbb 参数
     * @param tag 参数
     * @param flag 参数
     */
    public static void processAllRemains(List<String> markStrList, StringBuilder sbb, String tag, String flag) {

        if(markStrList == null || markStrList.isEmpty() || (markStrList.size() == 1 && markStrList.get(0).toString().equals("\n")) || (markStrList.size() == 1 && markStrList.get(0).toString().equals(""))) return;

        if(flag.equals("xml1") && (markStrList.get(0).toString().contains("<association") || markStrList.get(0).toString().contains("<collection"))) {//xml 类型的文件形式 association
            sbb.append("\r\n");
            sbb.append("\t\t<!--以下为上次注释需要保存下来的代码-->" + "\r\n");
            for(String k : markStrList) {
                if(k.contains("<association") || k.contains("<collection")) {//只输出这一个上次遗留下来的标签内容然后删除
                    sbb.append(k);
                }
            }
            sbb.append("\r\n");
        } else if(flag.equals("xml2")) {//xml 类型的文件格式 <select>..
            sbb.append("\r\n");
            sbb.append("\t<!--以下为上次注释需要保存下来的代码-->" + "\r\n");
            for(String k : markStrList) {//baoliu 上次所以留下来的代码不包含association 的
                if(!k.contains("<association") && !k.contains("<collection")) {//只输出这一个上次遗留下来的标签内容然后删除
                    sbb.append(k);
                }
            }
            sbb.append("\r\n");
        } else if(!flag.equals("xml2") && !flag.equals("xml1")) {//java 类型的文件形式
            sbb.append("\t//以下为上次注释需要保存下来的代码" + "\r\n");
            sbb.append("\t//" + tag + "\r\n");//modify by neo on 2016.10.24
            for(String k : markStrList) {
                sbb.append(k);
            }
            sbb.append("\t//" + tag + "\r\n");
        }
    }

    /**
     * 判断出老的行 和 新生成的行 有相同的时 使用老的行内容替代了新的行内容
     * 注意老的行记录中list 元素不能重复 也就是说 不能有老的重复的行
     * @param markStrList    参数
     * @param freshLineStr     参数
     * @param endChar 参数
     * @return 返回值类型
     */
    public static String genMarkStr(List<String> markStrList, String freshLineStr, String endChar) {

        String result = null;

        if(markStrList == null || markStrList.isEmpty()) return result;

        String p = freshLineStr.replaceAll("\r", "").trim();//\t|\r|\n readline读取的上一次的代码 中 会保留\t 让有换行的时候会手动添加\n
        p = p.substring(0, p.indexOf(endChar));

        for(String markStr: markStrList) {
            if(MedusaCommonUtils.isNotBlank(markStr)) {

                int x = markStr.trim().indexOf(endChar);

                if(x != -1 && markStr.trim().substring(0, x).equals(p)) {
                    result = markStr;
                    markStrList.remove(markStr);//匹配一个删除一个元素   方法名称相同则替换方法体所有内容
                    break;
                }
            }
        }
        return result;
    }

    public static List<String> genTagStrList(String fileName, String packagePath, String tag, String flag) {

        String path;
        if(Home.xmlSuffix.matches("^classpath.*:.*")) {//modify by neo on 2020.04.25
            path = Home.proResourcePath + Home.xmlSuffix.replaceFirst("^classpath.*:", "");
        } else {
            path = Home.proJavaPath + packagePath.replaceAll("\\.", "/");
        }

        File dirsFile = new File(path);
        if(!dirsFile.exists()) dirsFile.mkdirs();
        String resPath = path + "/" + fileName;

        List<String> resultList = new ArrayList<>();

        if (MedusaCommonUtils.isBlank(tag) || MedusaCommonUtils.isBlank(resPath)) return null;


        String startTag;
        String endTag;

        if(flag.equals("xml")) {

            startTag = "</resultMap>";
            endTag = "</mapper>";
        } else {

            startTag = "//" + tag;//modify by neo on 2016.10.24
            endTag = "//" + tag;
        }

        FileInputStream fis = null;
        BufferedReader br = null; //用于包装InputStreamReader,提高处理性能。因为BufferedReader有缓冲的，而InputStreamReader没有

        try {
            //如果没有java文件则创建新的文件
            File oriFile = new File(resPath);
//            if(!oriFile.exists()) oriFile.createNewFile();
            if(!oriFile.exists()) return null;

            fis = new FileInputStream(oriFile);//TODO

            // 从文件系统中的某个文件中获取字节
            br = new BufferedReader(new InputStreamReader(fis));// 从字符输入流中读取文件中的内容,封装了一个new InputStreamReader的对象

            String str1 = "";
            while ((str1 = br.readLine()) != null) {

                if(str1.trim().contains("association")) {//xml

                    resultList.add(str1  + "\n");
                } else {
                    String str2, str3 = "";
                    if (str1.trim().contains(startTag)) {

                        while ((str2 = br.readLine()) != null) {
                            if (str2.trim().contains(endTag)) {
                                resultList.add(str3);
                                break;
                            } else {
                                str3 += str2 + "\n";// 当读取的一行不为空时,把读到的str的值赋给str1
                            }
                        }
                    }
                }

            }

            return resultList;

        } catch (FileNotFoundException e) {
            System.out.println("找不到指定文件");
        } catch (IOException e) {
            System.out.println("读取文件失败");
        } finally {
            try {
                if(br != null) br.close();
                if(fis != null) fis.close();
                // 关闭的时候最好按照先后顺序关闭最后开的先关闭所以先关s,再关n,最后关m
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return resultList;
    }
}
