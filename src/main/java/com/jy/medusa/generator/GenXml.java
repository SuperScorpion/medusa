package com.jy.medusa.generator;

import com.jy.medusa.utils.MyDateUtils;
import com.jy.medusa.utils.SystemConfigs;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by neo on 16/7/27.
 */
public class GenXml {

    private String[] colSqlNames;//数据库列名数组
    private String[] colnames; // 列名数组
    private String[] colTypes; // 列名类型数组
    private String[] colTypesSql;//mysql 对应的类型数组
    private int[] colSizes; // 列名大小数组

    private String packagePath;//mapper
    private String mapperPath;//xml
    private String tableName;
    private String propertyFilename;
    private String entityPath;//entity
    private String entityName;

    private List<String> markXmlList;
    private String tag;//标记 mark

    private List<String> ignorArrayAssociation;
    private String pluralAssociation;//映射关系字段的后缀名 一般为s



    public GenXml(String mapperPath, String packagePath, String entityPath, String tableName, String propertyFilename, String tag, String ignorAssociation, String pluralAssociation) {
        this.packagePath = packagePath;
        this.mapperPath = mapperPath;
        this.tableName = tableName;
        this.propertyFilename = propertyFilename;
        this.entityPath = entityPath;
        this.entityName = MyGenUtils.upcaseFirst(tableName);
        this.tag = tag;
        this.ignorArrayAssociation = Arrays.asList(ignorAssociation.split(","));
        this.pluralAssociation = pluralAssociation;
        this.markXmlList = MyGenUtils.genTagStrList(entityName + "Mapper.xml", packagePath, tag, "xml");
    }

    private void changeTypes(){//TODO
        for(int i=0; i < colTypesSql.length ;i++){
            if(StringUtils.isBlank(colTypesSql[i])) continue;

            switch (colTypesSql[i]) {
                case "INT" : colTypes[i] = "INTEGER"; break;
                case "DATETIME" : colTypes[i] = "TIMESTAMP"; break;
                default: colTypes[i] = colTypesSql[i]; break;
            }
        }
    }

    public void process() {

        GenEntity.DataBaseTools dataBaseTools = new GenEntity().new DataBaseTools(propertyFilename);

        Connection conn = dataBaseTools.openConnection(); // 得到数据库连接
        PreparedStatement pstmt = null;
        String strsql = "select * from " + tableName;
        try {
            pstmt = conn.prepareStatement(strsql);
            ResultSetMetaData rsmd = pstmt.getMetaData();
            int size = rsmd.getColumnCount(); // 共有多少列
            colSqlNames = new String[size];
            colnames = new String[size];
            colTypes = new String[size];
            colSizes = new int[size];
            colTypesSql = new String[size];

            for (int i = 0; i < rsmd.getColumnCount(); i++) {

                colSqlNames[i] = rsmd.getColumnName(i + 1);
                colnames[i] = MyGenUtils.getCamelStr(rsmd.getColumnName(i + 1));
                colTypesSql[i] = rsmd.getColumnTypeName(i + 1);
                colSizes[i] = rsmd.getColumnDisplaySize(i + 1);
            }

            changeTypes();//处理mybatis类型 和 sql类型不一致

            try {
                String content = parse();
                String path = System.getProperty("user.dir") + "/src/main/java/" + packagePath.replaceAll("\\.", "/");
                File file = new File(path);
                if(!file.exists()){
                    file.mkdirs();
                }
                String resPath = path + "/" + entityName + "Mapper.xml";

                FileUtils.writeStringToFile(new File(resPath), content, "UTF-8");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            dataBaseTools.closeConnection(conn, pstmt);
        }
    }

    /**
     * 解析处理(生成实体类主体代码)
     */
    private String parse() {
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n");
        sb.append("<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">\r\n");
        sb.append("<mapper namespace=\"" + mapperPath + "." + entityName + "Mapper\">\r\n");
        sb.append("\t<resultMap id=\"BaseResultMap\" type=\"" + entityPath + "." + entityName + Home.entityNameSuffix + "\">\r\n");


        for (int i = 0; i < colSqlNames.length; i++) {

            if (colnames[i].trim().equalsIgnoreCase(SystemConfigs.PRIMARY_KEY)) {
                sb.append("\t\t<id column=\"id\" jdbcType=\"INTEGER\" property=\"id\" />\r\n");
            } else {
                sb.append("\t\t<result column=\"" + colSqlNames[i] + "\" jdbcType=\"" + colTypes[i] + "\" property=\"" + colnames[i] + "\" />\r\n");
            }

            //外间关联
            //if(StringUtils.isNotBlank(colSqlNames[i]) && colSqlNames[i].contains("_id") && !ignorArrayAssociation.contains(colSqlNames[i])) {

                /*String p = colSqlNames[i].trim().replace("_id", "").trim().concat(pluralAssociation);
                String bigStr = MyGenUtils.upcaseFirst(p);
                String smallStr = MyGenUtils.getCamelStr(p);

                String param = "\t\t<association property=\"" + smallStr + "\" column=\"" + colSqlNames[i] + "\" select=\"find" + bigStr + "ById\" />\r\n";
                String paramStr11 = MyGenUtils.genMarkStr(markXmlList , param, "/>");*/

                //上次若有相同的association标签的话 并且内容相同则使用旧的 并且markxmllist里remove掉 不再遍历它
                //生成第一次时 直接生成 else条件 第二次如果保留了该次生成的 再次生成则使用if条件里的
                /*if(paramStr11 != null)
                    sb.append(paramStr11);
                else
                    sb.append(param);*/

//                sb.append("\t\t<association property=\"" + smallStr + "\" column=\"" + colSqlNames[i] + "\" select=\"find" + bigStr + "ById\" />\r\n");
            //}
        }

        //association 等到最后才生成
        for (int i = 0; i < colSqlNames.length; i++) {

            //外间关联
            if(StringUtils.isNotBlank(colSqlNames[i]) && colSqlNames[i].contains("_id") && !ignorArrayAssociation.contains(colSqlNames[i])) {

                String p = colSqlNames[i].trim().replace("_id", "").trim().concat(pluralAssociation);
                String bigStr = MyGenUtils.upcaseFirst(p);
                String smallStr = MyGenUtils.getCamelStr(p);

                String param = "\t\t<association property=\"" + smallStr + "\" column=\"" + colSqlNames[i] + "\" select=\"find" + bigStr + "ById\" " + Home.lazyLoad + "/>\r\n";
                String paramStr11 = MyGenUtils.genMarkStr(markXmlList , param, "/>");

                //上次若有相同的association标签的话 并且内容相同则使用旧的 并且markxmllist里remove掉 不再遍历它
                //生成第一次时 直接生成 else条件 第二次如果保留了该次生成的 再次生成则使用if条件里的
                if(paramStr11 != null)
                    sb.append(paramStr11);
                else
                    sb.append(param);
            }
        }

        MyGenUtils.processAllRemains(markXmlList, sb, tag, "xml1");//处理assciation上次的遗留

        sb.append("\t</resultMap>\r\n\r\n");

        //外间关联表
        for (int i = 0; i < colSqlNames.length; i++) {

            //外间关联sss
            if(StringUtils.isNotBlank(colSqlNames[i]) && colSqlNames[i].contains("_id") && !ignorArrayAssociation.contains(colSqlNames[i])) {

                String p = colSqlNames[i].trim().replace("_id", "").trim().concat(pluralAssociation);
                String bigStr = MyGenUtils.upcaseFirst(p);
                //String smallStr = MyGenUtils.getCamelStr(p);


                //String param = "\t<select id = \"find" + bigStr + "ById\" resultType=\"" + entityPath + "." + bigStr + "\">\r\n" + "\t\tSELECT * FROM " + p + " WHERE id = #{id} limit 0,1\r\n" + "\t</select>\r\n\r\n";
                //String paramStr11 = MyGenUtils.genMarkStr(markXmlList , param, "resultType=");

                //上次有相同的association标签内容段 则不再生产
                //生成第一次时 直接生成 else条件 第二次如果保留了该次生成的 再次生成则使用if条件里的
                /*if(paramStr11 != null)
                    sb.append(paramStr11);
                else
                    sb.append(param);*/

                //select 标签sql模块代码完全保留 不会跟association一样替代 不重复生产
                sb.append("\t<select id = \"find" + bigStr + "ById\" resultType=\"" + entityPath + "." + bigStr + Home.entityNameSuffix + "\">\r\n");
                sb.append("\t\tSELECT * FROM " + p + " WHERE id = #{id} limit 0,1\r\n");
                sb.append("\t</select>\r\n\r\n");
            }
        }

        //添加基础字段
        sb.append("\t<!--<sql id=\"Base_Column_List\" >\r\n");
        sb.append("\t\t" + String.join(",", colSqlNames) + "\r\n");
        sb.append("\t</sql>-->\r\n\r\n");
        //添加作者
        sb.append("\t<!-- Created by " + Home.author + " on " + MyDateUtils.convertDateToStr(new Date(), null) + " -->\r\n\r\n");


        MyGenUtils.processAllRemains(markXmlList, sb, tag, "xml2");//处理所有的上次遗留标签 在resultmap mapper 之间的

        sb.append("</mapper>\r\n");

        return sb.toString();
    }

}
