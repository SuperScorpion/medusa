package com.jy.medusa.generator.gen;

import com.jy.medusa.gaze.utils.MedusaCommonUtils;
import com.jy.medusa.gaze.utils.MedusaDateUtils;
import com.jy.medusa.gaze.utils.SystemConfigs;
import com.jy.medusa.generator.DataBaseTools;
import com.jy.medusa.generator.Home;
import com.jy.medusa.generator.MedusaGenUtils;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.Date;

/**
 * Created by neo on 16/7/19.
 * @deprecated
 */

public class GenEntity {

    private String[] colSqlNames;//数据库列名数组
    private String[] colFieldNames; // 列名数组
    private String[] colTypes; // mybatis 列名类型数组
    private int[] colSizes; // 列名大小数组
    private boolean isMedusaDateUtils = false; // 是否需要导入包 MedusaDateUtils
    private boolean isDate = false; // 是否需要导入包java.util.Date
    private boolean isSql = false; // 是否需要导入包java.sql.*
    private boolean isMoney = false; // 是否需要导入包java.math.BigDecimal

    private String packagePath;
    private String tableName;
    private String tag;//标记 mark
//    private JSONArray colValidArray;//参数校验
    private List<String> associationColumn;//映射的关系字段
    private String pluralAssociation;//映射关系字段的后缀名 一般为s

    private List<String> markStrList;//用来存储标记的代码段落

    private Map<String, String> defaultMap = new HashMap<>();//字段名称 和 默认值关系
    private Map<String, String> commentMap = new HashMap<>();//字段名称 和 注注释对应关系

    private String primaryKey = SystemConfigs.PRIMARY_KEY;//默认主键字段名


    public GenEntity(String packagePath, String tableName, Object colValidArray) {
        this.packagePath = packagePath;
        this.tableName = tableName;
        this.tag = Home.tag;
//        this.colValidArray = colValidArray;
        this.associationColumn = Arrays.asList(Home.associationColumn.split(","));
        this.pluralAssociation = Home.pluralAssociation;
        this.markStrList = MedusaGenUtils.genTagStrList(MedusaGenUtils.upcaseFirst(tableName) + ".java", packagePath, tag, "java");
    }

    public GenEntity() {

    }

    public void process() {

        DataBaseTools dataBaseTools = new DataBaseTools();

        Connection conn = dataBaseTools.openConnection(); // 得到数据库连接
        PreparedStatement pstmt = null;
        String strsql = "select * from " + tableName;
        String sqlComments = "show full columns from " + tableName;

        try {
            //modify by neo on 2019.08.17
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


            pstmt = conn.prepareStatement(sqlComments);
            ResultSet rs = pstmt.executeQuery();
            if (rs != null) {
                while (rs.next()) {
                    defaultMap.put(MedusaGenUtils.getCamelStr(rs.getString("Field")), rs.getString("Default"));
                    commentMap.put(MedusaGenUtils.getCamelStr(rs.getString("Field")), rs.getString("Comment"));
                }
            }

            pstmt = conn.prepareStatement(strsql);
            ResultSetMetaData rsmd = pstmt.getMetaData();
            int size = rsmd.getColumnCount(); // 共有多少列
            colSqlNames = new String[size];
            colFieldNames = new String[size];
            colTypes = new String[size];
            colSizes = new int[size];

            for (int i = 0; i < rsmd.getColumnCount(); i++) {

                colSqlNames[i] = rsmd.getColumnName(i + 1);
                colFieldNames[i] = colSqlNames[i].equals(primaryKey) ? SystemConfigs.PRIMARY_KEY : MedusaGenUtils.getCamelStr(rsmd.getColumnName(i + 1));//modify by neo on 2020.02.14
                colTypes[i] = rsmd.getColumnTypeName(i + 1);
                if (colTypes[i].equalsIgnoreCase("datetime") || colTypes[i].equalsIgnoreCase("date") || colTypes[i].equalsIgnoreCase("TIMESTAMP")) {
                    if(MedusaCommonUtils.isNotBlank(defaultMap.get(colFieldNames[i]))) isMedusaDateUtils = true;
                    isDate = true;
                }
                if (colTypes[i].equalsIgnoreCase("image") || colTypes[i].equalsIgnoreCase("text")) {
                    isSql = true;
                }
                if (colTypes[i].equalsIgnoreCase("decimal")) {
                    isMoney = true;
                }
                colSizes[i] = rsmd.getColumnDisplaySize(i + 1);
            }

            try {
                String content = parse();
                String path = Home.proJavaPath + packagePath.replaceAll("\\.", "/");
                File file = new File(path);
                if(!file.exists()) {
                    file.mkdirs();
                }
                String resPath = path + "/" + MedusaGenUtils.upcaseFirst(tableName) + Home.entityNameSuffix + ".java";
                MedusaCommonUtils.writeString2File(new File(resPath), content, "UTF-8");
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
        sb.append("package " + packagePath + ";\r\n\r\n");

//        sb.append("import " + basePoPath + ";\r\n");//TODO
        if(MedusaCommonUtils.isNotBlank(Home.lazyLoad)) sb.append("import com.fasterxml.jackson.annotation.JsonIgnoreProperties;\r\n");
        if(MedusaCommonUtils.isNotBlank(Home.entitySerializable)) sb.append("import java.io.Serializable;\r\n");

        sb.append("import com.jy.medusa.gaze.stuff.annotation.Column;\r\n");
        sb.append("import com.jy.medusa.gaze.stuff.annotation.Table;\r\n");
        sb.append("import com.jy.medusa.gaze.stuff.annotation.Id;\r\n\r\n");

        //参数校验
        /*if(colValidArray != null && !colValidArray.isEmpty()) {
            sb.append("import "+ SystemConfigs.VALID_PATTERN_PATH +";\r\n");
            sb.append("import "+ SystemConfigs.VALID_LENGTH_PATH +";\r\n");
            sb.append("import "+ SystemConfigs.VALID_VALIDATOR_PATH  +";\r\n\r\n");
        }*/

        if (isMedusaDateUtils) {
            sb.append("import com.jy.medusa.gaze.utils.MedusaDateUtils;\r\n\r\n");
        }
        if (isDate) {
            sb.append("import java.util.Date;\r\n\r\n");
        }
        if (isSql) {
            sb.append("import java.sql.*;\r\n\r\n");
        }
        if (isMoney) {
            sb.append("import java.math.BigDecimal;\r\n\r\n");
        }

        //添加作者
        sb.append("/**\r\n");
        sb.append(" * Created by " + Home.author + " on " + MedusaDateUtils.convertDateToStr(new Date(), null) + "\r\n");
        sb.append(" */\r\n");

        sb.append("@Table(name = \""+ tableName +"\")\r\n");

        if(MedusaCommonUtils.isNotBlank(Home.lazyLoad)) sb.append("@JsonIgnoreProperties(value={\"handler\"})\r\n");

        sb.append("public class " + MedusaGenUtils.upcaseFirst(tableName) + Home.entityNameSuffix);
        if(MedusaCommonUtils.isNotBlank(Home.entitySerializable)) sb.append(" implements Serializable");
        sb.append(" {\r\n\r\n");

        processAllAttrs(sb);
        sb.append("\r\n");

        processAllMethod(sb);

        MedusaGenUtils.processAllRemains(markStrList, sb, tag, "java");

        sb.append("}\r\n");

        return sb.toString();
    }


    /**
     * 生成所有的方法
     * @param sb       参数
     */
    private void processAllMethod(StringBuilder sb) {

        for (int i = 0; i < colFieldNames.length; i++) {

            String p1 = "\tpublic void set" + MedusaGenUtils.upcaseFirst(colFieldNames[i]) + "(" + sqlType2JavaType(colTypes[i]) + " " + colFieldNames[i] + ") {\r\n";
            String paramStr1 = MedusaGenUtils.genMarkStr(markStrList, p1, "{");
            if(paramStr1 != null) {
                sb.append("\t//以下为上次注释需要保存下来的代码" + "\r\n");
                sb.append(paramStr1);
                sb.append("\r\n");
            } else {
                sb.append(p1);
                sb.append("\t\tthis." + colFieldNames[i] + "=" + colFieldNames[i] + ";\r\n");
                sb.append("\t}\r\n\r\n");
            }


            String p2 = "\tpublic " + sqlType2JavaType(colTypes[i]) + " get" + MedusaGenUtils.upcaseFirst(colFieldNames[i]) + "() {\r\n";
            String paramStr2 = MedusaGenUtils.genMarkStr(markStrList, p2, "{");
            if(paramStr2 != null) {
                sb.append("\t//以下为上次注释需要保存下来的代码" + "\r\n");
                sb.append(paramStr2);
                sb.append("\r\n");
            } else {
                sb.append(p2);
                sb.append("\t\treturn " + colFieldNames[i] + ";\r\n");
                sb.append("\t}\r\n\r\n");
            }
        }


        //字段都生成完了 再生成映射属性
        for (int i = 0; i < colFieldNames.length; i++) {
            //处理外间关联的表字段名称
            if(MedusaCommonUtils.isNotBlank(colSqlNames[i]) && colSqlNames[i].endsWith("_id") && associationColumn.contains(colSqlNames[i])) {

                String p = colSqlNames[i].trim().replace("_id", "").trim();
                if(MedusaCommonUtils.isNotBlank(pluralAssociation) && !p.endsWith(pluralAssociation)) {//modify by neo, on 2016.11.25
                    p = p.concat(pluralAssociation);
                }

                String bigStr = MedusaGenUtils.upcaseFirst(p) + Home.entityNameSuffix;
                String smallStr = MedusaGenUtils.getCamelStr(p);

//                String f11 = "\tpublic void set" + bigStr + "Set(Set<" + bigStr + "> " + smallStr + "Set) {\r\n";
                String f11 = "\tpublic void set" + bigStr + "(" + bigStr + " " + smallStr + ") {\r\n";
                String paramStr11 = MedusaGenUtils.genMarkStr(markStrList , f11, "{");
                if(paramStr11 != null) {
                    sb.append("\t//以下为上次注释需要保存下来的代码" + "\r\n");
                    sb.append(paramStr11);
                    sb.append("\r\n");
                } else {
                    sb.append(f11);
                    sb.append("\t\tthis." + smallStr + " = " + smallStr + ";\r\n");
                    sb.append("\t}\r\n\r\n");
                }


//                String f22 = "\tpublic Set<" + bigStr + "> get" + bigStr + "Set() {\r\n";
                String f22 = "\tpublic " + bigStr + " get" + bigStr + "() {\r\n";
                String paramStr22 = MedusaGenUtils.genMarkStr(markStrList, f22, "{");
                if(paramStr22 != null) {
                    sb.append("\t//以下为上次注释需要保存下来的代码" + "\r\n");
                    sb.append(paramStr22);
                    sb.append("\r\n");
                } else {
                    sb.append(f22);
                    sb.append("\t\treturn " + smallStr + ";\r\n");
                    sb.append("\t}\r\n\r\n");
                }
            }
        }
    }

    /**
     * 解析输出属性
     * @return 返回值类型
     */
    private void processAllAttrs(StringBuilder sb) {
        for (int i = 0; i < colFieldNames.length; i++) {

            //添加注释
            if(MedusaCommonUtils.isNotBlank(commentMap.get(colFieldNames[i]))) {
                sb.append("\t/*");
                sb.append(commentMap.get(colFieldNames[i]));
                sb.append("*/\r\n");
            }

            //参数校验
            /*if(colValidArray != null && !colValidArray.isEmpty()) {
                String[] validStrArray = null;
                for (Object n : colValidArray) {
                    if (MedusaCommonUtils.isNotBlank(((JSONObject) n).getString(colFieldNames[i])))
                        validStrArray = ((JSONObject) n).getString(colFieldNames[i]).split("&");
                }

                if (validStrArray != null && validStrArray.length != 0) {
                    for (String k : validStrArray) {
                        sb.append("\t");
                        sb.append(k);
                        sb.append("\r\n");
                    }
                }
            }*/

            String primaryAnnotationTxt = colSqlNames[i].trim().equalsIgnoreCase(primaryKey) ? "\t@Id\r\n" : "";

            //添加默认值
            String defaultStr = MedusaCommonUtils.isNotBlank(defaultMap.get(colFieldNames[i])) ? " = " + sqlType2JavaTypeForDefault(colTypes[i], defaultMap.get(colFieldNames[i])) : "";

            String wellStr = primaryAnnotationTxt + "\t@Column(name = \"" + colSqlNames[i] + "\")\r\n\tprivate " + sqlType2JavaType(colTypes[i]) + " " + colFieldNames[i] + defaultStr + ";\r\n\r\n";

            String paramStr = MedusaGenUtils.genMarkStr(markStrList, wellStr, ";");

            if(paramStr != null) {
                sb.append("\t//以下为上次注释需要保存下来的代码" + "\r\n");
                sb.append(paramStr);
                sb.append("\r\n");
            } else {
                sb.append(wellStr);
            }
        }

        //字段都生成完了 再生成映射属性
        for (int i = 0; i < colFieldNames.length; i++) {
            //外间关联表关系
            if(MedusaCommonUtils.isNotBlank(colSqlNames[i]) && colSqlNames[i].endsWith("_id") && associationColumn.contains(colSqlNames[i])) {

                String p = colSqlNames[i].trim().replace("_id", "").trim();
                if(MedusaCommonUtils.isNotBlank(pluralAssociation) && !p.endsWith(pluralAssociation)) {///modify by neo on 2016.11.25
                    p = p.concat(pluralAssociation);
                }

                //sb.append("\tprivate Set<" + MedusaGenUtils.upcaseFirst(p) + "> " + MedusaGenUtils.getCamelStr(p) + "Set;\r\n\r\n");

                String wellStr = "\t/*这是" + colSqlNames[i] + "的关联属性*/\r\n" + "\tprivate " + MedusaGenUtils.upcaseFirst(p) + Home.entityNameSuffix + " " + MedusaGenUtils.getCamelStr(p) + ";\r\n\r\n";

                String paramStr = MedusaGenUtils.genMarkStr(markStrList, wellStr, ";");

                if(paramStr != null) {
                    sb.append("\t//以下为上次注释需要保存下来的代码" + "\r\n");
                    sb.append(paramStr);
                    sb.append("\r\n");
                } else {
                    sb.append(wellStr);
                }
            }
        }
    }


    private String sqlType2JavaTypeForDefault(String sqlType, String def) {
        if (sqlType.equalsIgnoreCase("bit")) {
            if(def.equals("0")) {
                return "false";
            } else if(def.equals("1")) {
                return "true";
            } else {
                return def;
            }
        } else if (sqlType.equalsIgnoreCase("bigint")) {
            return def + "l";
        } else if (sqlType.equalsIgnoreCase("float")) {
            return def + "f";
        } else if (sqlType.equalsIgnoreCase("double")) {
            return def + "d";
        } else if (sqlType.equalsIgnoreCase("decimal")
                || sqlType.equalsIgnoreCase("numeric")
                || sqlType.equalsIgnoreCase("real")
                || sqlType.equalsIgnoreCase("money")
                || sqlType.equalsIgnoreCase("smallmoney")) {
            return "new BigDecimal(" + def + ")";
        } else if (sqlType.equalsIgnoreCase("varchar")
                || sqlType.equalsIgnoreCase("char")
                || sqlType.equalsIgnoreCase("nvarchar")
                || sqlType.equalsIgnoreCase("nchar")) {
            return "\"" + def + "\"";
        } else if (sqlType.equalsIgnoreCase("datetime") || sqlType.equalsIgnoreCase("date") || sqlType.equalsIgnoreCase("timestamp")) {
            if(def.equals("CURRENT_TIMESTAMP")) {
                return "new Date()";
            } else {
                return "MedusaDateUtils.convertStrToDate(\"" + def + "\")";
            }
        }

        return def;
    }

    private String sqlType2JavaType(String sqlType) {
        if (sqlType.equalsIgnoreCase("bit")) {
            return "Boolean";
        } else if (sqlType.equalsIgnoreCase("tinyint")) {
            return "Byte";
        } else if (sqlType.equalsIgnoreCase("smallint")) {
            return "Short";
        } else if (sqlType.equalsIgnoreCase("int") || sqlType.equalsIgnoreCase("integer") || sqlType.equalsIgnoreCase("int unsigned")) {
            return "Integer";
        } else if (sqlType.equalsIgnoreCase("bigint")) {
            return "Long";
        } else if (sqlType.equalsIgnoreCase("float")) {
            return "Float";
        } else if (sqlType.equalsIgnoreCase("double")) {
            return "Double";
        } else if (sqlType.equalsIgnoreCase("decimal")
                || sqlType.equalsIgnoreCase("numeric")
                || sqlType.equalsIgnoreCase("real")) {
            return "BigDecimal";
        } else if (sqlType.equalsIgnoreCase("money")
                || sqlType.equalsIgnoreCase("smallmoney")) {
            return "BigDecimal";
        } else if (sqlType.equalsIgnoreCase("varchar")
                || sqlType.equalsIgnoreCase("char")
                || sqlType.equalsIgnoreCase("nvarchar")
                || sqlType.equalsIgnoreCase("nchar")) {
            return "String";
        } else if (sqlType.equalsIgnoreCase("datetime") || sqlType.equalsIgnoreCase("date") || sqlType.equalsIgnoreCase("timestamp")) {
            return "Date";
        }
        else if (sqlType.equalsIgnoreCase("image")) {
            return "Blob";
        } else if (sqlType.equalsIgnoreCase("text")) {
            return "Clob";
        }
        return null;
    }
}