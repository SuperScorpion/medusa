package com.jy.medusa.generator.ftl.gen;

/**
 * Created by SuperScorpion on 16/7/19.
 */

import com.jy.medusa.gaze.utils.MedusaCommonUtils;
import com.jy.medusa.gaze.utils.MedusaDateUtils;
import com.jy.medusa.gaze.utils.SystemConfigs;
import com.jy.medusa.generator.DataBaseTools;
import com.jy.medusa.generator.Home;
import com.jy.medusa.generator.MedusaGenUtils;
import com.jy.medusa.generator.ftl.vo.EntityColumnVo;
import freemarker.core.ParseException;
import freemarker.template.*;

import java.io.*;
import java.sql.*;
import java.util.Date;
import java.util.*;

public class GenEntityFtl {

    private String[] colSqlNames;//数据库列名数组
    private String[] colFieldNames; // 列名数组
    private String[] colTypes; // mybatis 列名类型数组
    private int[] colSizes; // 列名大小数组
    private boolean isMedusaDateUtils = false; // 是否需要导入包 MedusaDateUtils
    private boolean isDate = false; // 是否需要导入包java.util.Date
//    private boolean isSql = false; // 是否需要导入包java.sql.*
    private boolean isMoney = false; // 是否需要导入包java.math.BigDecimal

    private String entityPath;
    private String tableName;
//    private String tag;//标记 mark
//    private JSONArray colValidArray;//参数校验
    private List<String> associationColumn;//映射的关系字段
    private String pluralAssociation;//映射关系字段的后缀名 一般为s

    private List<String> markStrList;//用来存储标记的代码段落

    private Map<String, String> defaultMap = new HashMap<>();//字段名称 和 默认值关系
    private Map<String, String> commentMap = new HashMap<>();//字段名称 和 注注释对应关系

    private String primaryKey = SystemConfigs.PRIMARY_KEY;//默认主键字段名

    private String tableComment = "";//表备注信息


    public GenEntityFtl() {

    }

    public GenEntityFtl(String entityPath, String tableName, Object colValidArray) {
        this.entityPath = entityPath;
        this.tableName = tableName;
//        this.tag = tag;
//        this.colValidArray = colValidArray;
        this.associationColumn = Arrays.asList(Home.associationColumn.split(","));
        this.pluralAssociation = Home.pluralAssociation;
//        this.markStrList = MedusaGenUtils.genTagStrList(MedusaGenUtils.upcaseFirst(tableName) + ".java", entityPath, tag, "java");
    }

    public Boolean process() {

        DataBaseTools dataBaseTools = Home.staticDataBaseTools;

        Connection conn = dataBaseTools.openConnection(); // 得到数据库连接
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String strsql = "select * from " + tableName;
        String sqlComments = "show full columns from " + tableName;
        String tableInfoSql = "SHOW CREATE TABLE " + tableName;

        try {
            //modify by SuperScorpion on 2019.08.17
            //get current primary key from table
            DatabaseMetaData dbmd = conn.getMetaData();
            ResultSet resultSet = dbmd.getPrimaryKeys(null, null, tableName);
            while(resultSet.next()) {
                if(!resultSet.isLast()) {
                    System.out.println("注意: " + tableName + " 表拥有多个主键 - 已跳过生成!");
                    break;
                }
                primaryKey = resultSet.getObject(4).toString();
            }


            //1.先处理表备注信息 add by SuperScorpion on 20210120
            pstmt = conn.prepareStatement(tableInfoSql);
            rs = pstmt.executeQuery();
            if (rs != null) {
                while (rs.next()) {
                    String createDDL = rs.getString(2);
                    int index = createDDL.indexOf("COMMENT='");
                    if (index < 0) {
                        tableComment = "";
                    } else {
                        tableComment = createDDL.substring(index + 9);
                        tableComment = tableComment.substring(0, tableComment.length() - 1);
                    }
                }
            }

            //2.处理默认值和字段备注信息
            pstmt = conn.prepareStatement(sqlComments);
            rs = pstmt.executeQuery();
            if (rs != null) {
                while (rs.next()) {
                    defaultMap.put(MedusaGenUtils.getCamelStr(rs.getString("Field")), rs.getString("Default"));
                    commentMap.put(MedusaGenUtils.getCamelStr(rs.getString("Field")), rs.getString("Comment"));
                }
            }

            //3.处理各种数据库字段信息
            pstmt = conn.prepareStatement(strsql);
            ResultSetMetaData rsmd = pstmt.getMetaData();
            int size = rsmd.getColumnCount(); // 共有多少列
            colSqlNames = new String[size];
            colFieldNames = new String[size];
            colTypes = new String[size];
            colSizes = new int[size];

            for (int i = 0; i < rsmd.getColumnCount(); i++) {

                colSqlNames[i] = rsmd.getColumnName(i + 1);
                colFieldNames[i] = MedusaGenUtils.getCamelStr(rsmd.getColumnName(i + 1));//modify by SuperScorpion on 20210121
                colTypes[i] = rsmd.getColumnTypeName(i + 1);

                if (sqlType2JavaType(colTypes[i]).equals("Date")) {
                    if(MedusaCommonUtils.isNotBlank(defaultMap.get(colFieldNames[i]))) isMedusaDateUtils = true;
                    isDate = true;
                }
                if (sqlType2JavaType(colTypes[i]).equals("BigDecimal")) {
                    isMoney = true;
                }
//                if (colTypes[i].equalsIgnoreCase("image")) {
//                    isSql = true;
//                }

                colSizes[i] = rsmd.getColumnDisplaySize(i + 1);
            }

            /*try {
                String content = parse();
                String path = Home.proJavaPath + entityPath.replaceAll("\\.", "/");
                File file = new File(path);
                if(!file.exists()) {
                    file.mkdirs();
                }
                String resPath = path + "/" + MedusaGenUtils.upcaseFirst(tableName) + Home.entityNameSuffix + ".java";
                MedusaCommonUtils.writeString2File(new File(resPath), content, "UTF-8");
            } catch (IOException e) {
                e.printStackTrace();
            }*/

            Map<String, Object> map = parse();

            String path = Home.proJavaPath + entityPath.replaceAll("\\.", "/");
            File file = new File(path);
            if(!file.exists()) {
                file.mkdirs();
            }
            String resPath = path + "/" + MedusaGenUtils.upcaseFirst(tableName) + Home.entityNameSuffix + ".java";

            Configuration cfg = Home.cfg;

            if(!Home.checkIsFtlAvailable()) {

                cfg.setClassLoaderForTemplateLoading(this.getClass().getClassLoader(), "/template");
            } else {

                cfg.setDirectoryForTemplateLoading(new File(Home.ftlDirPath));
            }

            Template temp = cfg.getTemplate("entity.ftl");//TODO


            //如果目标文件已存在 则跳过 add by SuperScorpion on 20230221
            File resPathFile = new File(resPath);
            if(resPathFile.exists()) {
                System.out.println("Medusa: " + MedusaGenUtils.upcaseFirst(tableName) + Home.entityNameSuffix + ".java" + " 文件已存在 将跳过生成...");
                return false;
            }
            FileOutputStream fos = new FileOutputStream(resPathFile);


            Writer out = new BufferedWriter(new OutputStreamWriter(fos, "utf-8"), 10240);

            /*Map<String, Object> map = new HashMap<>();
            map.put("projectName", "lisi");
            map.put("packageName", "wangwu");
            map.put("wt", true);*/

            if(temp != null) temp.process(map, out);

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            dataBaseTools.closeConnection(conn, pstmt);
        }

        return true;
    }

    /**
     * 解析处理(生成实体类主体代码)
     */
    private Map<String, Object> parse() {

        boolean lazyLoadSwitch = false;
        boolean entitySerializableSwitch = false;
        boolean useValid = false;

        if(MedusaCommonUtils.isNotBlank(Home.lazyLoadSwitch)) lazyLoadSwitch = true;
        if(MedusaCommonUtils.isNotBlank(Home.entitySerializableSwitch)) entitySerializableSwitch = true;

        //参数校验
        /*if(colValidArray != null && !colValidArray.isEmpty()) {
            useValid = true;
        }*/


        Map<String, Object> map = new HashMap<>();

        map.put("tableName", tableName);
        map.put("tableComment", tableComment);
        map.put("entityPath", entityPath);
        map.put("author", Home.author);
        map.put("upcaseFirstTableName", MedusaGenUtils.upcaseFirst(tableName));
        map.put("entityNameSuffix", Home.entityNameSuffix);
        map.put("now_time", MedusaDateUtils.convertDateToStr(new Date(), null));

        map.put("isMedusaDateUtils", isMedusaDateUtils);
        map.put("isDate", isDate);
//        map.put("isSql", isSql);
        map.put("isMoney", isMoney);

        map.put("lazyLoadSwitch", lazyLoadSwitch);
        map.put("entitySerializableSwitch", entitySerializableSwitch);
        map.put("useValid", useValid);


        List<EntityColumnVo> columnDtos = new ArrayList<>();

        processAll(columnDtos);

        map.put("columnDtos", columnDtos);

        return map;
    }

    private void processAll(List<EntityColumnVo> entityDtos) {

        for (int i = 0; i < colFieldNames.length; i++) {

            EntityColumnVo cv = new EntityColumnVo();

            processAllColumn(cv, i);

            entityDtos.add(cv);
        }
    }


    /**
     * 解析输出属性
     * @return 返回值类型
     */
    private void processAllColumn(EntityColumnVo cv, int i) {

        cv.setColumn(colSqlNames[i]);
        cv.setJavaType(sqlType2JavaType(colTypes[i]));
        cv.setUpperName(MedusaGenUtils.upcaseFirst(colFieldNames[i]));
        cv.setLowwerName(colFieldNames[i]);

        //添加注释
        if(MedusaCommonUtils.isNotBlank(commentMap.get(colFieldNames[i]))) {
            cv.setComment(commentMap.get(colFieldNames[i]));
        }

        if(colSqlNames[i].trim().equalsIgnoreCase(primaryKey)) {
            cv.setPrimarykeyFlag(true);
        }

        //添加默认值
        String defaultStr = MedusaCommonUtils.isNotBlank(defaultMap.get(colFieldNames[i])) ? " = " + sqlType2JavaTypeForDefault(colTypes[i], defaultMap.get(colFieldNames[i])) : "";
        cv.setDefaultStr(defaultStr);

        //字段都生成完了 再生成映射属性
        //外间关联表关系
        if(MedusaCommonUtils.isNotBlank(colSqlNames[i]) && colSqlNames[i].endsWith("_id") && associationColumn.contains(colSqlNames[i])) {

            cv.setNotOnlyColumnFlag(true);


            String p = colSqlNames[i].trim().replace("_id", "").trim();

            if(MedusaCommonUtils.isNotBlank(pluralAssociation) && !p.endsWith(pluralAssociation)) {///modify by SuperScorpion on 2016.11.25
                p = p.concat(pluralAssociation);
            }

            cv.setAssociRemark("/*这是" + colSqlNames[i] + "的关联属性*/");

            cv.setAssociUpperName(MedusaGenUtils.upcaseFirst(p) + Home.entityNameSuffix);
            cv.setAssociLowwerName(MedusaGenUtils.getCamelStr(p));
        }
    }


    /**
     * 根据数据库类型和默认值生成实体类里的字段默认值
     * @param sqlType
     * @param def
     * @return
     */
    private String sqlType2JavaTypeForDefault(String sqlType, String def) {
        if (sqlType2JavaType(sqlType).equals("Boolean")) {
            if(def.equals("0")) {
                return "false";
            } else if(def.equals("1")) {
                return "true";
            } else {
                return def;
            }
        } else if (sqlType2JavaType(sqlType).equals("Long")) {
            return def + "l";
        } else if (sqlType2JavaType(sqlType).equals("Float")) {
            return def + "f";
        } else if (sqlType2JavaType(sqlType).equals("Double")) {
            return def + "d";
        } else if (sqlType2JavaType(sqlType).equals("BigDecimal")) {
            return "new BigDecimal(" + def + ")";
        } else if (sqlType2JavaType(sqlType).equals("String")) {
            return "\"" + def + "\"";
        } else if (sqlType2JavaType(sqlType).equals("Date")) {
            if(def.equals("CURRENT_TIMESTAMP")) {
                return "new Date()";
            } else {
                return "MedusaDateUtils.convertStrToDate(\"" + def + "\")";
            }
        }
        return def;
    }


    /**
     * mysql数据库类型 转 java类型 待优化
     * @param sqlType
     * @return
     */
    private String sqlType2JavaType(String sqlType) {
        sqlType = sqlType.toLowerCase().trim().replace(" unsigned", "");//add by SuperScorpion on 20210120
        if (sqlType.equalsIgnoreCase("bit")) {
            return "Boolean";
        } else if (sqlType.equalsIgnoreCase("tinyint") || sqlType.equalsIgnoreCase("smallint") || sqlType.equalsIgnoreCase("mediumint")
           || sqlType.equalsIgnoreCase("int") || sqlType.equalsIgnoreCase("integer")) {
            return "Integer";
        } else if (sqlType.equalsIgnoreCase("bigint")) {
            return "Long";
        } else if (sqlType.equalsIgnoreCase("float")) {
            return "Float";
        } else if (sqlType.equalsIgnoreCase("double")) {
            return "Double";
        } else if (sqlType.equalsIgnoreCase("decimal")
                || sqlType.equalsIgnoreCase("numeric")
                || sqlType.equalsIgnoreCase("real")
                || sqlType.equalsIgnoreCase("money")
                || sqlType.equalsIgnoreCase("smallmoney")) {
            return "BigDecimal";
        } else if (sqlType.equalsIgnoreCase("varchar")
                || sqlType.equalsIgnoreCase("char")
                || sqlType.equalsIgnoreCase("nvarchar")
                || sqlType.equalsIgnoreCase("nchar")) {
            return "String";
        } else if (sqlType.equalsIgnoreCase("datetime") || sqlType.equalsIgnoreCase("date") || sqlType.equalsIgnoreCase("timestamp")
                || sqlType.equalsIgnoreCase("time")) {
            return "Date";
        } else if (sqlType.equalsIgnoreCase("json") || sqlType.equalsIgnoreCase("set") || sqlType.equalsIgnoreCase("enum")
                || sqlType.equalsIgnoreCase("tinytext") || sqlType.equalsIgnoreCase("text") || sqlType.equalsIgnoreCase("mediumtext")
                || sqlType.equalsIgnoreCase("longtext")) {//add by SuperScorpion on 20210120
            return "String";
        } else if (sqlType.equalsIgnoreCase("tinyblob") || sqlType.equalsIgnoreCase("blob") || sqlType.equalsIgnoreCase("mediumblob")
                || sqlType.equalsIgnoreCase("longblob") || sqlType.equalsIgnoreCase("binary") || sqlType.equalsIgnoreCase("varbinary")) {
            return "Byte[]";
        }

        return null;
    }
}