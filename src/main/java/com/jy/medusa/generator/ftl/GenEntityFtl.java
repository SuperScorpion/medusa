package com.jy.medusa.generator.ftl;

/**
 * Created by neo on 16/7/19.
 */

import com.jy.medusa.generator.Home;
import com.jy.medusa.generator.MyGenUtils;
import com.jy.medusa.generator.ftl.vo.EntityColumnVo;
import com.jy.medusa.gaze.utils.MyDateUtils;
import com.jy.medusa.gaze.utils.MyCommonUtils;
import com.jy.medusa.gaze.utils.SystemConfigs;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.*;
import java.sql.*;
import java.util.*;
import java.util.Date;

public class GenEntityFtl {

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
//    private String tag;//标记 mark
//    private JSONArray colValidArray;//参数校验
    private List<String> associationColumn;//映射的关系字段
    private String pluralAssociation;//映射关系字段的后缀名 一般为s

    private List<String> markStrList;//用来存储标记的代码段落

    private Map<String, String> defaultMap = new HashMap<>();//字段名称 和 默认值关系
    private Map<String, String> commentMap = new HashMap<>();//字段名称 和 注注释对应关系

    public GenEntityFtl() {

    }

    public GenEntityFtl(String packagePath, String tableName, Object colValidArray) {
        this.packagePath = packagePath;
        this.tableName = tableName;
//        this.tag = tag;
//        this.colValidArray = colValidArray;
        this.associationColumn = Arrays.asList(Home.associationColumn.split(","));
        this.pluralAssociation = Home.pluralAssociation;
//        this.markStrList = MyGenUtils.genTagStrList(MyGenUtils.upcaseFirst(tableName) + ".java", packagePath, tag, "java");
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

            Map<String, Object> map = parse();

            /*try {
                String content = parse();
                String path = Home.proPath + packagePath.replaceAll("\\.", "/");
                File file = new File(path);
                if(!file.exists()){
                    file.mkdirs();
                }
                String resPath = path + "/" + MyGenUtils.upcaseFirst(tableName) + Home.entityNameSuffix + ".java";
                MyCommonUtils.writeString2File(new File(resPath), content, "UTF-8");
            } catch (IOException e) {
                e.printStackTrace();
            }*/

            String path = Home.proPath + packagePath.replaceAll("\\.", "/");
            File file = new File(path);
            if(!file.exists()){
                file.mkdirs();
            }
            String resPath = path + "/" + MyGenUtils.upcaseFirst(tableName) + Home.entityNameSuffix + ".java";



            Configuration cfg = new Configuration(Configuration.VERSION_2_3_22);

            try {
                if(!Home.checkIsFtlAvailable()) {

                    cfg.setClassLoaderForTemplateLoading(this.getClass().getClassLoader(), "/template");
                } else {

                    cfg.setDirectoryForTemplateLoading(new File(Home.ftlDirPath));
                }


                Template temp = cfg.getTemplate("entity.ftl");//TODO

                FileOutputStream fos = new FileOutputStream(new File(resPath));

                Writer out = new BufferedWriter(new OutputStreamWriter(fos, "utf-8"), 10240);

                /*Map<String, Object> map = new HashMap<>();
                map.put("projectName", "lisi");
                map.put("packageName", "wangwu");
                map.put("wt", true);*/

                if(temp != null) temp.process(map, out);

            } catch (IOException e) {
                e.printStackTrace();
            } catch (TemplateException e) {
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
    private Map<String, Object> parse() {

        boolean lazyLoad = false;
        boolean entitySerializable = false;
        boolean useValid = false;

        if(MyCommonUtils.isNotBlank(Home.lazyLoad)) lazyLoad = true;
        if(MyCommonUtils.isNotBlank(Home.entitySerializable)) entitySerializable = true;

        //参数校验
        /*if(colValidArray != null && !colValidArray.isEmpty()) {
            useValid = true;
        }*/


        Map<String, Object> map = new HashMap<>();

        map.put("tableName", tableName);
        map.put("entityPath", packagePath);
        map.put("author", Home.author);
        map.put("upcaseFirstTableName", MyGenUtils.upcaseFirst(tableName));
        map.put("entityNameSuffix", Home.entityNameSuffix);
        map.put("now_time", MyDateUtils.convertDateToStr(new Date(), null));

        map.put("isMyDateUtils", isMyDateUtils);
        map.put("isDate", isDate);
        map.put("isSql", isSql);
        map.put("isMoney", isMoney);

        map.put("lazyLoad", lazyLoad);
        map.put("entitySerializable", entitySerializable);
        map.put("useValid", useValid);


        List<EntityColumnVo> columnDtos = new ArrayList<>();

        processAll(columnDtos);

        map.put("columnDtos", columnDtos);

        return map;
    }

    private void processAll(List<EntityColumnVo> entityDtos) {

        for (int i = 0; i < colNames.length; i++) {

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
        cv.setUpperName(MyGenUtils.upcaseFirst(colNames[i]));
        cv.setLowwerName(colNames[i]);

        //添加注释
        if(MyCommonUtils.isNotBlank(commentMap.get(colNames[i]))) {
            cv.setComment(commentMap.get(colNames[i]));
        }

        if(colNames[i].trim().equalsIgnoreCase(SystemConfigs.PRIMARY_KEY)) {
            cv.setPrimarykeyFlag(true);
        }

        //添加默认值
        String defaultStr = MyCommonUtils.isNotBlank(defaultMap.get(colNames[i])) ? " = " + sqlType2JavaTypeForDefault(colTypes[i], defaultMap.get(colNames[i])) : "";
        cv.setDefaultStr(defaultStr);

        //字段都生成完了 再生成映射属性
        //外间关联表关系
        if(MyCommonUtils.isNotBlank(colSqlNames[i]) && colSqlNames[i].endsWith("_id") && associationColumn.contains(colSqlNames[i])) {

            cv.setNotOnlyColumnFlag(true);


            String p = colSqlNames[i].trim().replace("_id", "").trim();

            if(MyCommonUtils.isNotBlank(pluralAssociation) && !p.endsWith(pluralAssociation)) {///modify by neo on 2016.11.25
                p = p.concat(pluralAssociation);
            }

            cv.setAssociRemark("/*这是" + colSqlNames[i] + "的关联属性*/");

            cv.setAssociUpperName(MyGenUtils.upcaseFirst(p) + Home.entityNameSuffix);
            cv.setAssociLowwerName(MyGenUtils.getCamelStr(p));
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
public class DataBaseTools{

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