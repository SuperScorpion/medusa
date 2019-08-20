package com.jy.medusa.generator;

/**
 * Created by neo on 16/7/19.
 */

import com.jy.medusa.gaze.utils.MyDateUtils;
import com.jy.medusa.gaze.utils.MyCommonUtils;
import com.jy.medusa.gaze.utils.SystemConfigs;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.Date;

public class GenEntity {

    private String[] colSqlNames;//数据库列名数组
    private String[] colNames; // 列名数组
    private String[] colTypes; // mybatis 列名类型数组
    private int[] colSizes; // 列名大小数组
    private boolean isMyDateUtils = false; // 是否需要导入包 myDateUtils
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

    private String primaryKey = SystemConfigs.PRIMARY_KEY;//主键字段名


    public GenEntity(String packagePath, String tableName, Object colValidArray) {
        this.packagePath = packagePath;
        this.tableName = tableName;
        this.tag = Home.tag;
//        this.colValidArray = colValidArray;
        this.associationColumn = Arrays.asList(Home.associationColumn.split(","));
        this.pluralAssociation = Home.pluralAssociation;
        this.markStrList = MyGenUtils.genTagStrList(MyGenUtils.upcaseFirst(tableName) + ".java", packagePath, tag, "java");
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
            pstmt = conn.prepareStatement(sqlComments);
            ResultSet rs = pstmt.executeQuery();
            if (rs != null) {
                while (rs.next()) {
                    defaultMap.put(MyGenUtils.getCamelStr(rs.getString("Field")), rs.getString("Default"));
                    commentMap.put(MyGenUtils.getCamelStr(rs.getString("Field")), rs.getString("Comment"));
                }
            }

            pstmt = conn.prepareStatement(strsql);
            ResultSetMetaData rsmd = pstmt.getMetaData();
            int size = rsmd.getColumnCount(); // 共有多少列
            colSqlNames = new String[size];
            colNames = new String[size];
            colTypes = new String[size];
            colSizes = new int[size];

            for (int i = 0; i < rsmd.getColumnCount(); i++) {

                colSqlNames[i] = rsmd.getColumnName(i + 1);
                colNames[i] = MyGenUtils.getCamelStr(rsmd.getColumnName(i + 1));
                colTypes[i] = rsmd.getColumnTypeName(i + 1);
                if (colTypes[i].equalsIgnoreCase("datetime") || colTypes[i].equalsIgnoreCase("date") || colTypes[i].equalsIgnoreCase("TIMESTAMP")) {
                    if(MyCommonUtils.isNotBlank(defaultMap.get(colNames[i]))) isMyDateUtils = true;
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

            try {
                String content = parse();
                String path = Home.proPath + packagePath.replaceAll("\\.", "/");
                File file = new File(path);
                if(!file.exists()) {
                    file.mkdirs();
                }
                String resPath = path + "/" + MyGenUtils.upcaseFirst(tableName) + Home.entityNameSuffix + ".java";
                MyCommonUtils.writeString2File(new File(resPath), content, "UTF-8");
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
        if(MyCommonUtils.isNotBlank(Home.lazyLoad)) sb.append("import com.fasterxml.jackson.annotation.JsonIgnoreProperties;\r\n");
        if(MyCommonUtils.isNotBlank(Home.entitySerializable)) sb.append("import java.io.Serializable;\r\n");

        sb.append("import com.jy.medusa.gaze.stuff.annotation.Column;\r\n");
        sb.append("import com.jy.medusa.gaze.stuff.annotation.Table;\r\n");
        sb.append("import com.jy.medusa.gaze.stuff.annotation.Id;\r\n\r\n");

        //参数校验
        /*if(colValidArray != null && !colValidArray.isEmpty()) {
            sb.append("import "+ SystemConfigs.VALID_PATTERN_PATH +";\r\n");
            sb.append("import "+ SystemConfigs.VALID_LENGTH_PATH +";\r\n");
            sb.append("import "+ SystemConfigs.VALID_VALIDATOR_PATH  +";\r\n\r\n");
        }*/

        if (isMyDateUtils) {
            sb.append("import com.jy.medusa.gaze.utils.MyDateUtils;\r\n\r\n");
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
        sb.append(" * Created by " + Home.author + " on " + MyDateUtils.convertDateToStr(new Date(), null) + "\r\n");
        sb.append(" */\r\n");

        sb.append("@Table(name = \""+ tableName +"\")\r\n");

        if(MyCommonUtils.isNotBlank(Home.lazyLoad)) sb.append("@JsonIgnoreProperties(value={\"handler\"})\r\n");

        sb.append("public class " + MyGenUtils.upcaseFirst(tableName) + Home.entityNameSuffix);
        if(MyCommonUtils.isNotBlank(Home.entitySerializable)) sb.append(" implements Serializable");
        sb.append(" {\r\n\r\n");

        processAllAttrs(sb);
        sb.append("\r\n");

        processAllMethod(sb);

        MyGenUtils.processAllRemains(markStrList, sb, tag, "java");

        sb.append("}\r\n");

        return sb.toString();
    }


    /**
     * 生成所有的方法
     * @param sb       参数
     */
    private void processAllMethod(StringBuilder sb) {

        for (int i = 0; i < colNames.length; i++) {

            String p1 = "\tpublic void set" + MyGenUtils.upcaseFirst(colNames[i]) + "(" + sqlType2JavaType(colTypes[i]) + " " + colNames[i] + ") {\r\n";
            String paramStr1 = MyGenUtils.genMarkStr(markStrList, p1, "{");
            if(paramStr1 != null) {
                sb.append("\t//以下为上次注释需要保存下来的代码" + "\r\n");
                sb.append(paramStr1);
                sb.append("\r\n");
            } else {
                sb.append(p1);
                sb.append("\t\tthis." + colNames[i] + "=" + colNames[i] + ";\r\n");
                sb.append("\t}\r\n\r\n");
            }


            String p2 = "\tpublic " + sqlType2JavaType(colTypes[i]) + " get" + MyGenUtils.upcaseFirst(colNames[i]) + "() {\r\n";
            String paramStr2 = MyGenUtils.genMarkStr(markStrList, p2, "{");
            if(paramStr2 != null) {
                sb.append("\t//以下为上次注释需要保存下来的代码" + "\r\n");
                sb.append(paramStr2);
                sb.append("\r\n");
            } else {
                sb.append(p2);
                sb.append("\t\treturn " + colNames[i] + ";\r\n");
                sb.append("\t}\r\n\r\n");
            }
        }


        //字段都生成完了 再生成映射属性
        for (int i = 0; i < colNames.length; i++) {
            //处理外间关联的表字段名称
            if(MyCommonUtils.isNotBlank(colSqlNames[i]) && colSqlNames[i].endsWith("_id") && associationColumn.contains(colSqlNames[i])) {

                String p = colSqlNames[i].trim().replace("_id", "").trim();
                if(MyCommonUtils.isNotBlank(pluralAssociation) && !p.endsWith(pluralAssociation)) {//modify by neo, on 2016.11.25
                    p = p.concat(pluralAssociation);
                }

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
     * @return 返回值类型
     */
    private void processAllAttrs(StringBuilder sb) {
        for (int i = 0; i < colNames.length; i++) {

            //添加注释
            if(MyCommonUtils.isNotBlank(commentMap.get(colNames[i]))) {
                sb.append("\t/*");
                sb.append(commentMap.get(colNames[i]));
                sb.append("*/\r\n");
            }

            //参数校验
            /*if(colValidArray != null && !colValidArray.isEmpty()) {
                String[] validStrArray = null;
                for (Object n : colValidArray) {
                    if (MyCommonUtils.isNotBlank(((JSONObject) n).getString(colNames[i])))
                        validStrArray = ((JSONObject) n).getString(colNames[i]).split("&");
                }

                if (validStrArray != null && validStrArray.length != 0) {
                    for (String k : validStrArray) {
                        sb.append("\t");
                        sb.append(k);
                        sb.append("\r\n");
                    }
                }
            }*/

            String primaryAnnotationTxt = colNames[i].trim().equalsIgnoreCase(primaryKey) ? "\t@Id\r\n" : "";

            //添加默认值
            String defaultStr = MyCommonUtils.isNotBlank(defaultMap.get(colNames[i])) ? " = " + sqlType2JavaTypeForDefault(colTypes[i], defaultMap.get(colNames[i])) : "";

            String wellStr = primaryAnnotationTxt + "\t@Column(name = \"" + colSqlNames[i] + "\")\r\n\tprivate " + sqlType2JavaType(colTypes[i]) + " " + colNames[i] + defaultStr + ";\r\n\r\n";

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
        for (int i = 0; i < colNames.length; i++) {
            //外间关联表关系
            if(MyCommonUtils.isNotBlank(colSqlNames[i]) && colSqlNames[i].endsWith("_id") && associationColumn.contains(colSqlNames[i])) {

                String p = colSqlNames[i].trim().replace("_id", "").trim();
                if(MyCommonUtils.isNotBlank(pluralAssociation) && !p.endsWith(pluralAssociation)) {///modify by neo on 2016.11.25
                    p = p.concat(pluralAssociation);
                }

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
                return "MyDateUtils.convertStrToDate(\"" + def + "\")";
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



    /**
     * 获取properties 属性值 并初始化
     */
    public class DataBaseTools {

        private String driver;

        private String url;

        private String user;

        private String password;

        private Connection conn;

        public DataBaseTools() {
            loadProperties();
    //        loadProperties(fileName);
        }

        private void loadProperties() {
    //    private void loadProperties(String fileName) {

    /*        String resPaths = System.getProperty("user.dir") + Home.getProperPath() + fileName;

            Properties props = new Properties();
            try {
                props.load(new FileInputStream(resPaths));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }*/
    /*        this.driver = props.getProperty("jdbc.driver");
            this.url = props.getProperty("jdbc.url");
            this.user = props.getProperty("jdbc.username");
            this.password = props.getProperty("jdbc.password");*/

            this.driver = Home.jdbcDriver;
            this.url = Home.jdbcUrl;
            this.user = Home.jdbcUsername;
            this.password = Home.jdbcPassword;
        }


        public Connection openConnection() {
            try {
                if (conn != null && !conn.isClosed()) {
                    return this.conn;
                } else {
                    try {
                        Class.forName(driver);///初始化 并注册 driver
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

        public void closeConnection(Connection conn, Statement st) {
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