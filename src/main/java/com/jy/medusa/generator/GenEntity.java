package com.jy.medusa.generator;

/**
 * Created by neo on 16/7/19.
 */

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jy.medusa.utils.MyDateUtils;
import com.jy.medusa.utils.SystemConfigs;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.Date;

public class GenEntity {

    private String[] colSqlNames;//数据库列名数组
    private String[] colnames; // 列名数组
    private String[] colTypes; // mybatis 列名类型数组
    private int[] colSizes; // 列名大小数组
    private boolean f_util = false; // 是否需要导入包java.util.*
    private boolean f_sql = false; // 是否需要导入包java.sql.*
    private boolean f_money = false; // 是否需要导入包java.math.*

    private String packagePath;
    private String tableName;
    private String propertyFilename;
    private String tag;//标记 mark
    private JSONArray colValidArray;//参数校验
    private List<String> ignorAssociation;//忽略处理的映射关系字段
    private String pluralAssociation;//映射关系字段的后缀名 一般为s

    private List<String> markStrList;//用来存储标记的代码段落

    private Map<String, String> commentMap = new HashMap<>();//字段名称 和 注注释对应关系
//    private Map<String, String> foreignMap = new HashMap<>();//字段名称 和 主外间的对应关系

    public GenEntity(String packagePath, String tableName, String propertyFilename, String tag, JSONArray colValidArray, String ignorAssociation, String pluralAssociation) {
        this.packagePath = packagePath;
        this.tableName = tableName;
        this.propertyFilename = propertyFilename;
        this.tag = tag;
        this.colValidArray = colValidArray;
        this.ignorAssociation = Arrays.asList(ignorAssociation.split(","));
        this.pluralAssociation = pluralAssociation;
        this.markStrList = MyGenUtils.genTagStrList(MyGenUtils.upcaseFirst(tableName) + ".java", packagePath, tag, "java");
    }

    public GenEntity() {

    }

    public void process() {

        DataBaseTools dataBaseTools = new DataBaseTools(propertyFilename);

        Connection conn = dataBaseTools.openConnection(); // 得到数据库连接
        PreparedStatement pstmt = null;
        String strsql = "select * from " + tableName;
        String sqlComments = "show full columns from " + tableName;

        try {
            pstmt = conn.prepareStatement(sqlComments);
            ResultSet rs = pstmt.executeQuery();
            if (rs != null) {
                while (rs.next()) {
                    commentMap.put(MyGenUtils.getCamelStr(rs.getString("Field")), rs.getString("Comment"));
//                    foreignMap.put(MyGenUtils.getCamelStr(rs.getString("Field")), rs.getString("Key"));
                }
            }

            pstmt = conn.prepareStatement(strsql);
            ResultSetMetaData rsmd = pstmt.getMetaData();
            int size = rsmd.getColumnCount(); // 共有多少列
            colSqlNames = new String[size];
            colnames = new String[size];
            colTypes = new String[size];
            colSizes = new int[size];

            for (int i = 0; i < rsmd.getColumnCount(); i++) {

                colSqlNames[i] = rsmd.getColumnName(i + 1);
                colnames[i] = MyGenUtils.getCamelStr(rsmd.getColumnName(i + 1));
                colTypes[i] = rsmd.getColumnTypeName(i + 1);
                if (colTypes[i].equalsIgnoreCase("datetime")) {
                    f_util = true;
                }
                if (colTypes[i].equalsIgnoreCase("image") || colTypes[i].equalsIgnoreCase("text")) {
                    f_sql = true;
                }
                if (colTypes[i].equalsIgnoreCase("decimal")) {
                    f_money = true;
                }
                colSizes[i] = rsmd.getColumnDisplaySize(i + 1);
            }

            try {
                String content = parse(colnames, colTypes, colSizes, packagePath, tableName);
                String path = System.getProperty("user.dir") + "/src/main/java/" + packagePath.replaceAll("\\.", "/");
                File file = new File(path);
                if(!file.exists()){
                    file.mkdirs();
                }
                String resPath = path + "/" + MyGenUtils.upcaseFirst(tableName) + Home.entityNameSuffix + ".java";
//                System.out.println("resPath=" + resPath);
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
    private String parse(String[] colNames, String[] colTypes, int[] colSizes, String packagePath, String tableName) {
        StringBuilder sb = new StringBuilder();
        sb.append("package " + packagePath + ";\r\n\r\n");

//        sb.append("import " + basePoPath + ";\r\n");//TODO
        sb.append("import javax.persistence.Column;\r\n");//TODO
        sb.append("import javax.persistence.Table;\r\n");//TODO
        sb.append("import javax.persistence.Id;\r\n\r\n");//TODO

        //参数校验
        if(colValidArray != null && !colValidArray.isEmpty()) {
            sb.append("import "+ SystemConfigs.VALID_PATTERN_PATH +";\r\n");
            sb.append("import "+ SystemConfigs.VALID_LENGTH_PATH +";\r\n");
            sb.append("import "+ SystemConfigs.VALID_VALIDATOR_PATH  +";\r\n\r\n");
        }

        //外间关联表关系

        //for (int i = 0; i < colnames.length; i++) {
           // if(StringUtils.isNotBlank(colSqlNames[i]) && colSqlNames[i].endsWith("_id") && !ignorAssociation.contains(colSqlNames[i])) {
//                String p = colSqlNames[i].trim().replace("_id", "").trim().concat(pluralAssociation);;
//                sb.append("import " + packagePath + "." + MyGenUtils.upcaseFirst(p) + ";\r\n");
         //       sb.append("import java.util.Set;\r\n\r\n");
           // }
        //}


        if (f_util) {
            sb.append("import java.util.Date;\r\n\r\n");
        }
        if (f_sql) {
            sb.append("import java.sql.*;\r\n\r\n");
        }
        if (f_money) {
            sb.append("import java.math.BigDecimal;\r\n\r\n");
        }

        //添加作者
        sb.append("/**\r\n");
        sb.append(" * Created by " + Home.author + " on " + MyDateUtils.convertDateToStr(new Date(), null) + "\r\n");
        sb.append(" */\r\n");

        sb.append("@Table(name = \""+ tableName +"\")\r\n");

        sb.append("public class " + MyGenUtils.upcaseFirst(tableName) + Home.entityNameSuffix + " {\r\n\r\n");

        processAllAttrs(sb);
        sb.append("\r\n");

        processAllMethod(sb);

        MyGenUtils.processAllRemains(markStrList, sb, tag, "java");

        sb.append("}\r\n");
//        System.out.println(sb.toString());
        return sb.toString();

    }


    /**
     * 生成所有的方法
     * @param sb
     */
    private void processAllMethod(StringBuilder sb) {
        for (int i = 0; i < colnames.length; i++) {

            //if(colnames[i].trim().equalsIgnoreCase(SystemConfigs.PRIMARY_KEY)) continue;//去除生成Id get set 方法

            String p1 = "\tpublic void set" + MyGenUtils.upcaseFirst(colnames[i]) + "(" + sqlType2JavaType(colTypes[i]) + " " + colnames[i] + ") {\r\n";
            String paramStr1 = MyGenUtils.genMarkStr(markStrList, p1, "{");
            if(paramStr1 != null) {
                sb.append("\t//以下为上次注释需要保存下来的代码" + "\r\n");
                sb.append(paramStr1);
                sb.append("\r\n");
            } else {
                sb.append(p1);
                sb.append("\t\tthis." + colnames[i] + "=" + colnames[i] + ";\r\n");
                sb.append("\t}\r\n\r\n");
            }


            String p2 = "\tpublic " + sqlType2JavaType(colTypes[i]) + " get" + MyGenUtils.upcaseFirst(colnames[i]) + "() {\r\n";
            String paramStr2 = MyGenUtils.genMarkStr(markStrList, p2, "{");
            if(paramStr2 != null) {
                sb.append("\t//以下为上次注释需要保存下来的代码" + "\r\n");
                sb.append(paramStr2);
                sb.append("\r\n");
            } else {
                sb.append(p2);
                sb.append("\t\treturn " + colnames[i] + ";\r\n");
                sb.append("\t}\r\n\r\n");
            }
        }



        //字段都生成完了 再生成映射属性
        for (int i = 0; i < colnames.length; i++) {
            //处理外间关联的表字段名称
            if(StringUtils.isNotBlank(colSqlNames[i]) && colSqlNames[i].endsWith("_id") && !ignorAssociation.contains(colSqlNames[i])) {
                String p = colSqlNames[i].trim().replace("_id", "").trim().concat(pluralAssociation);
                String bigStr = MyGenUtils.upcaseFirst(p) + Home.entityNameSuffix;
                String smallStr = MyGenUtils.getCamelStr(p);

//                String f11 = "\tpublic void set" + bigStr + "Set(Set<" + bigStr + "> " + smallStr + "Set) {\r\n";
                String f11 = "\tpublic void set" + bigStr + "(" + bigStr + " " + smallStr + ") {\r\n";
                String paramStr11 = MyGenUtils.genMarkStr(markStrList , f11, "{");
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
                String paramStr22 = MyGenUtils.genMarkStr(markStrList, f22, "{");
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
     * @return
     */
    private void processAllAttrs(StringBuilder sb) {
        for (int i = 0; i < colnames.length; i++) {

            //if(colnames[i].trim().equalsIgnoreCase(SystemConfigs.PRIMARY_KEY)) continue;//去除生成ID属性

            //添加注释
            if(StringUtils.isNotBlank(commentMap.get(colnames[i]))) {
                sb.append("\t/*");
                sb.append(commentMap.get(colnames[i]));
                sb.append("*/\r\n");
            }

            //参数校验
            if(colValidArray != null && !colValidArray.isEmpty()) {
                String[] validStrArray = null;
                for (Object n : colValidArray) {
                    if (StringUtils.isNotBlank(((JSONObject) n).getString(colnames[i])))
                        validStrArray = ((JSONObject) n).getString(colnames[i]).split("&");
                }

                if (validStrArray != null && validStrArray.length != 0) {
                    for (String k : validStrArray) {
                        sb.append("\t");
                        sb.append(k);
                        sb.append("\r\n");
                    }
                }
            }

            String primaryAnnotationTxt = colnames[i].trim().equalsIgnoreCase(SystemConfigs.PRIMARY_KEY) ? "\t@Id\r\n" : "";

            String wellStr = primaryAnnotationTxt + "\t@Column(name = \"" + colSqlNames[i] + "\")\r\n\tprivate " + sqlType2JavaType(colTypes[i]) + " " + colnames[i] + ";\r\n\r\n";

            String paramStr = MyGenUtils.genMarkStr(markStrList, wellStr, ";");

            if(paramStr != null) {
                sb.append("\t//以下为上次注释需要保存下来的代码" + "\r\n");
                sb.append(paramStr);
                sb.append("\r\n");
            } else {
                sb.append(wellStr);
            }

        }

        //字段都生成完了 再生成映射属性
        for (int i = 0; i < colnames.length; i++) {
            //外间关联表关系
            if(StringUtils.isNotBlank(colSqlNames[i]) && colSqlNames[i].endsWith("_id") && !ignorAssociation.contains(colSqlNames[i])) {

                String p = colSqlNames[i].trim().replace("_id", "").trim().concat(pluralAssociation);

                //sb.append("\tprivate Set<" + MyGenUtils.upcaseFirst(p) + "> " + MyGenUtils.getCamelStr(p) + "Set;\r\n\r\n");

                String wellStr = "\t/*这是" + colSqlNames[i] + "的关联属性*/\r\n" + "\tprivate " + MyGenUtils.upcaseFirst(p) + Home.entityNameSuffix + " " + MyGenUtils.getCamelStr(p) + ";\r\n\r\n";

                String paramStr = MyGenUtils.genMarkStr(markStrList, wellStr, ";");

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



/**
 * 获取properties 属性值 并初始化
 */
public class DataBaseTools{

    private String driver;

    private String url;

    private String user;

    private String password;

    private Connection conn;

    public DataBaseTools(String fileName) {
        loadProperties(fileName);
    }

    private void loadProperties(String fileName) {

        String resPaths = System.getProperty("user.dir") + Home.getProperPath() + fileName;

        Properties props = new Properties();
        try {
            props.load(new FileInputStream(resPaths));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.driver = props.getProperty("jdbc.driver");
        this.url = props.getProperty("jdbc.url");
        this.user = props.getProperty("jdbc.username");
        this.password = props.getProperty("jdbc.password");
    }


    public Connection openConnection(){
        try {
            if (conn != null && !conn.isClosed()) {
                return this.conn;
            } else {
                try {
                    Class.forName(driver);
                    this.conn = DriverManager.getConnection(url, user, password);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return this.conn;
    }

    public void closeConnection(Connection conn, Statement st){
        try {
            if (st != null) {
                st.close();
            }
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
}