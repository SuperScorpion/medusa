package com.jy.medusa.generator;

import com.jy.medusa.gaze.utils.MyCommonUtils;
import com.jy.medusa.generator.ftl.GenControllerFtl;
import com.jy.medusa.generator.ftl.GenEntityFtl;
import com.jy.medusa.generator.ftl.GenServiceFtl;
import com.jy.medusa.generator.ftl.GenXmlFtl;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.Map;

/**
 * Created by neo on 16/8/4.
 */
public class Home {

    /*private static boolean devFlag = true;

    public static final String medusaPropPath = "/src/main/resources/";
    public static final String medusaPropPathDev = "/";*/

    public static final String mixMapper = "com.jy.medusa.gaze.commons.Mapper";

    public static String proPath;

    public static String author;
    public static String entityNameSuffix;//实体文件后缀名
    public static String lazyLoad;
    public static String entitySerializable;
    public static String ftlDirPath;

    public static String jdbcDriver;
    public static String jdbcUrl;
    public static String jdbcUsername;
    public static String jdbcPassword;


    public static String packagePath;
    public static String tableName;
//  public static String jdbcName;///////该字段已弃用了

    public static String tag;
//    public static String medusaProName;
    public static String entitySuffix;
    public static String serviceSuffix;
    public static String serviceImplSuffix;
    public static String mapperSuffix;
    public static String xmlSuffix;

    public static String controlJsonSuffix;
    public static String controlMortalSuffix;
//    public static String validJsonStr;//参数校验
    public static String associationColumn;//映射的普通数据库字段级联
    public static String pluralAssociation;//设定映射字段的后罪名
    public static String baseServiceSwitch;///是否生成基础的service类

    public Home() {
    }

    /*public Home(String medusaProName) {
        this.medusaProName = medusaProName;
    }*/

    /*public static String getProperPath() {
        return devFlag ? medusaPropPath : medusaPropPathDev;
    }*/


    public void process() {

        System.out.println("Sometimes your whole life boils down to one insane move. XBinYa");
        System.out.println("Loading...");

//        loadProperties(medusaProName);
        loadYml();//处理spring boot 里yml相关medusa的配置

//        if(!checkParams()) return;

        String entityPath = packagePath.concat(".").concat(entitySuffix);
        String servicePath = packagePath.concat(".").concat(serviceSuffix);
        String serviceImplPath = packagePath.concat(".").concat(serviceImplSuffix);
        String mapperPath = packagePath.concat(".").concat(mapperSuffix);
        String xmlPath = packagePath.concat(".").concat(xmlSuffix);
        String controlPathJson = packagePath.concat(".").concat(controlJsonSuffix);
        String controlPathMortal = packagePath.concat(".").concat(controlMortalSuffix);

        tableName = MyCommonUtils.isBlank(tableName) ? getAllTableName() : tableName;//如果不写表明则生成所有的表相关
        String[] tableNameArray = tableName.split(",");
        if(tableNameArray == null || tableNameArray.length == 0) return;

        //参数校验
        /*JSONObject job;
        JSONArray m = null;
        if(MyCommonUtils.isNotBlank(validJsonStr)) {
            job = parseValidJson(validJsonStr);
            m = (JSONArray) job.get("validator");
        }*/

        if(checkIsFtl()) {
            System.out.println("Medusa: " + "启动 ftl 生成模式...");
            if(checkIsFtlAvailable())
                System.out.println("Medusa: " + "启动 自定义模块 生成模式...");
            else
                System.out.println("Medusa: " + "启动 内置模版 生成模式...");
        } else {
            System.out.println("Medusa: " + "启动 default 生成模式...");
        }

        for(String tabName : tableNameArray) {
            if(MyCommonUtils.isNotBlank(tabName)) {

                tabName = tabName.trim();//祛除空格

                //TODO 自动生成参数校验
                /*JSONArray colValidArray = null;
                if(m != null && !m.isEmpty()) {
                    for (Object p : m) {
                        if ((((JSONObject) p).get(tabName)) != null) colValidArray = ((JSONArray) ((JSONObject) p).get(tabName));
                    }
                }*/


                if(MyCommonUtils.isNotBlank(entitySuffix)) {
                    long nanoSs = System.nanoTime();
                    if(checkIsFtl()) {
                        new GenEntityFtl(entityPath, tabName, null).process();
                    } else {
                        new GenEntity(entityPath, tabName, null).process();//生成 实体类
                    }
                    System.out.println("Medusa: 已完成 " + tabName + " - entity文件 总用时 & " + (System.nanoTime() - nanoSs) + " ns");
                }

                if(MyCommonUtils.isNotBlank(serviceImplSuffix) && MyCommonUtils.isNotBlank(serviceSuffix) && MyCommonUtils.isNotBlank(entitySuffix) && MyCommonUtils.isNotBlank(mapperSuffix)) {
                    long nanoSs = System.nanoTime();
                    if(checkIsFtl()) {
                        new GenServiceFtl(tabName, entityPath, servicePath, serviceImplPath, mapperPath).process();
                    } else {
                        new GenService(tabName, entityPath, servicePath, serviceImplPath, mapperPath).process();//执行生成service serviceimpl mapper
                    }
                    System.out.println("Medusa: 已完成 " + tabName + " - service文件 总用时 & " + (System.nanoTime() - nanoSs) + " ns");
                }

                if(MyCommonUtils.isNotBlank(xmlSuffix) && MyCommonUtils.isNotBlank(entitySuffix) && MyCommonUtils.isNotBlank(mapperSuffix)) {
                    long nanoSs = System.nanoTime();
                    if(checkIsFtl()) {
                        new GenXmlFtl(mapperPath, xmlPath, entityPath, tabName).process();
                    } else {
                        new GenXml(mapperPath, xmlPath, entityPath, tabName).process();//执行生成xml
                    }
                    System.out.println("Medusa: 已完成 " + tabName + " - xml文件 总用时 & " + (System.nanoTime() - nanoSs) + " ns");
                }


                if(checkIsFtl()) {
                    long nanoSs = System.nanoTime();
                    new GenControllerFtl(tabName, controlPathJson, entityPath, servicePath).process();
                    System.out.println("Medusa: 已完成 " + tabName + " - controller文件 总用时 & " + (System.nanoTime() - nanoSs) + " ns");
                } else {
                    if (MyCommonUtils.isNotBlank(entitySuffix) && MyCommonUtils.isNotBlank(serviceSuffix)) {
                        if (MyCommonUtils.isNotBlank(controlJsonSuffix)) {
                            long nanoSs = System.nanoTime();
                            new GenControllerJson(tabName, controlPathJson, entityPath, servicePath).process();//生成controller
                            System.out.println("Medusa: 已完成 " + tabName + " - controllerJson文件 总用时 & " + (System.nanoTime() - nanoSs) + " ns");
                        }
                        if (MyCommonUtils.isNotBlank(controlMortalSuffix)) {
                            long nanoSs = System.nanoTime();
                            new GenControllerMortal(tabName, controlPathMortal, entityPath, servicePath).process();//生成controller
                            System.out.println("Medusa: 已完成 " + tabName + " - controllerMortal文件 总用时 & " + (System.nanoTime() - nanoSs) + " ns");
                        }
                    }
                }
            }
        }

        ///yml 文件里面的属性值 会自动转换 比如 on->true yes->true no->false
        ///baseService和baseServiceImpl 只需要生成一次所以没做ftl的模版
        if(MyCommonUtils.isNotBlank(baseServiceSwitch) && (baseServiceSwitch.trim().equalsIgnoreCase("true"))) new GenBaseServiceAndImpl(servicePath, serviceImplPath).process();//处理生成基础的 service

        System.out.println("Medusa: The task has been completed...");
        System.out.println("Since 2016 in Jy - Hangzhou");
    }

    /*private JSONObject parseValidJson(String validJsonStr) {
        return MyCommonUtils.isNotBlank(validJsonStr) ? JSONObject.parseObject(validJsonStr) : null;
    }*/

    /*private boolean checkParams() {

        boolean result = true;

        if(MyCommonUtils.isBlank(packagePath)) {
            System.out.println("大兄弟你的packagePath没填写!");
            result = false;
        }
        return result;
    }*/

    private void loadYml() {

        String resPaths;
        URL ymlres = this.getClass().getClassLoader().getResource("application.yml");
        URL yamlres = this.getClass().getClassLoader().getResource("application.yaml");

        if(ymlres == null && yamlres != null) {
            resPaths = yamlres.getPath();
        } else if(ymlres != null && yamlres == null) {
            resPaths = ymlres.getPath();
        } else if(ymlres == null && yamlres == null) {
            System.out.println("Medusa: 大兄弟你的 application yml 或 yaml 配置文件 去火星了?");
            return;
        } else {
            System.out.println("Medusa: 大兄弟你的 application yml 或 yaml 配置文件 存在重复?");
            return;
        }

        //给proPath赋值 其它地方也要使用
        gainTheProPath(resPaths);

        FileInputStream fileInputStream = null;
        try {
            Yaml yaml = new Yaml();//实例化解析器
            File file = new File(resPaths);//配置文件地址
            fileInputStream = new FileInputStream(file);
            Map<String ,Object> map = yaml.loadAs(fileInputStream, Map.class);//装载的对象，这里使用Map, 当然也可使用自己写的对象

            Map<String ,Object> childMap = (Map) map.get("medusa");

            if(childMap == null) {
                System.out.println("Medusa: 大兄弟你的 application.yml 里的 medusa 相关配置 去火星了?");
                return;
            }

            if(childMap.get("jdbc") == null) {
                System.out.println("Medusa: 大兄弟你的 application.yml 里的 medusa 的 jdbc 相关配置 去火星了?");
                return;
            }

            Map<String ,String> jdbcMap = (Map<String, String>) childMap.get("jdbc");



            this.packagePath = childMap.get("packagePath") == null || MyCommonUtils.isBlank(childMap.get("packagePath").toString()) ? "com.medusa.xxx" : childMap.get("packagePath").toString().trim();
            this.tableName = childMap.get("tableName") == null || MyCommonUtils.isBlank(childMap.get("tableName").toString()) ?  "" : childMap.get("tableName").toString().trim();
            this.tag = childMap.get("tag") == null || MyCommonUtils.isBlank(childMap.get("tag").toString()) ?  "<" : childMap.get("tag").toString().trim();

            this.entitySuffix = childMap.get("entitySuffix") == null || MyCommonUtils.isBlank(childMap.get("entitySuffix").toString()) ?  "entity" : childMap.get("entitySuffix").toString().trim();
            this.serviceSuffix = childMap.get("serviceSuffix") == null || MyCommonUtils.isBlank(childMap.get("serviceSuffix").toString()) ?  "service" : childMap.get("serviceSuffix").toString().trim();
            this.serviceImplSuffix = childMap.get("serviceImplSuffix") == null || MyCommonUtils.isBlank(childMap.get("serviceImplSuffix").toString()) ?  "service.impl" : childMap.get("serviceImplSuffix").toString().trim();
            this.mapperSuffix = childMap.get("mapperSuffix") == null || MyCommonUtils.isBlank(childMap.get("mapperSuffix").toString()) ?  "persistence" : childMap.get("mapperSuffix").toString().trim();
            this.xmlSuffix = childMap.get("xmlSuffix") == null || MyCommonUtils.isBlank(childMap.get("xmlSuffix").toString()) ?  "persistence.xml" : childMap.get("xmlSuffix").toString().trim();
            this.controlJsonSuffix = childMap.get("controlJsonSuffix") == null || MyCommonUtils.isBlank(childMap.get("controlJsonSuffix").toString()) ?  "controller" : childMap.get("controlJsonSuffix").toString().trim();
            this.controlMortalSuffix = childMap.get("controlMortalSuffix") == null || MyCommonUtils.isBlank(childMap.get("controlMortalSuffix").toString()) ?  "" : childMap.get("controlMortalSuffix").toString().trim();

            this.associationColumn = childMap.get("associationColumn") == null || MyCommonUtils.isBlank(childMap.get("associationColumn").toString()) ?  "" : childMap.get("associationColumn").toString().trim();
            this.pluralAssociation = childMap.get("pluralAssociation") == null || MyCommonUtils.isBlank(childMap.get("pluralAssociation").toString()) ?  "s" : childMap.get("pluralAssociation").toString().trim();

            this.author = childMap.get("author") == null || MyCommonUtils.isBlank(childMap.get("author").toString()) ?  "administrator" : childMap.get("author").toString().trim();
            this.entityNameSuffix = childMap.get("entityNameSuffix") == null || MyCommonUtils.isBlank(childMap.get("entityNameSuffix").toString()) ?  "" : childMap.get("entityNameSuffix").toString().trim();
            this.lazyLoad = childMap.get("lazyLoad") == null || MyCommonUtils.isBlank(childMap.get("lazyLoad").toString()) ?  "" : "fetchType=\"lazy\"";
            this.entitySerializable = childMap.get("entitySerializable") == null || MyCommonUtils.isBlank(childMap.get("entitySerializable").toString()) ?  "" : childMap.get("entitySerializable").toString().trim();
            this.baseServiceSwitch = childMap.get("baseServiceSwitch") == null || MyCommonUtils.isBlank(childMap.get("baseServiceSwitch").toString()) ?  "" : childMap.get("baseServiceSwitch").toString();

            this.ftlDirPath = childMap.get("ftlDirPath") == null || MyCommonUtils.isBlank(childMap.get("ftlDirPath").toString()) ?  "" : childMap.get("ftlDirPath").toString().trim();

            this.jdbcDriver = jdbcMap.get("driver") == null || MyCommonUtils.isBlank(jdbcMap.get("driver").toString()) ?  "" : jdbcMap.get("driver").toString().trim();
            this.jdbcUrl = jdbcMap.get("url") == null || MyCommonUtils.isBlank(jdbcMap.get("url").toString()) ?  "" : jdbcMap.get("url").toString().trim();
            this.jdbcUsername = jdbcMap.get("username") == null || MyCommonUtils.isBlank(jdbcMap.get("username").toString()) ?  "" : jdbcMap.get("username").toString().trim();
            this.jdbcPassword = jdbcMap.get("password") == null || MyCommonUtils.isBlank(jdbcMap.get("password").toString()) ?  "" : jdbcMap.get("password").toString().trim();

        } catch(FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if(fileInputStream != null) fileInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 多模块的环境下 System.getProperty("user.dir") 获得的路径不够深入 所以用classLoader处理
     * /Users/neo/Desktop/my-work/arbitrage/webapi/target/classes/application.yml
     * /Users/neo/Desktop/my-work/arbitrage/webapi/src/main/java/
     * @param resPaths 参数
     */
    private void gainTheProPath(String resPaths) {
        String path = resPaths.replaceAll("/target/classes", "/src/main/java");
        this.proPath = path.replaceAll("application.yml|application.yaml", "");
    }

    /**
     * @deprecated
     * @param fileName
     */
    private void loadProperties(String fileName) {
/*
        String resPaths = System.getProperty("user.dir") + getProperPath() + fileName;

        Properties props = new Properties();
        try {
            props.load(new FileInputStream(resPaths));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.packagePath = MyCommonUtils.isBlank(props.get("medusa.packagePath")) ? "" : childMap.get("medusa.packagePath");
        this.tableName = MyCommonUtils.isBlank(childMap.get("medusa.tableName")) ? "" : childMap.get("medusa.tableName");
        this.tag = MyCommonUtils.isBlank(childMap.get("medusa.tag")) ? "" : childMap.get("medusa.tag");

        this.entitySuffix = MyCommonUtils.isBlank(childMap.get("medusa.entitySuffix")) ? "" : childMap.get("medusa.entitySuffix");
        this.serviceSuffix = MyCommonUtils.isBlank(childMap.get("medusa.serviceSuffix")) ? "" : childMap.get("medusa.serviceSuffix");
        this.serviceImplSuffix = MyCommonUtils.isBlank(childMap.get("medusa.serviceImplSuffix")) ? "" : childMap.get("medusa.serviceImplSuffix");
        this.mapperSuffix = MyCommonUtils.isBlank(childMap.get("medusa.mapperSuffix")) ? "" : childMap.get("medusa.mapperSuffix");
        this.xmlSuffix = MyCommonUtils.isBlank(childMap.get("medusa.xmlSuffix")) ? "" : childMap.get("medusa.xmlSuffix");
        this.controlJsonSuffix = MyCommonUtils.isBlank(childMap.get("medusa.controlJsonSuffix")) ? "" : childMap.get("medusa.controlJsonSuffix");
        this.controlMortalSuffix = MyCommonUtils.isBlank(childMap.get("medusa.controlMortalSuffix")) ? "" : childMap.get("medusa.controlMortalSuffix");

//        this.validJsonStr = MyCommonUtils.isBlank(childMap.get("medusa.validator")) ? "" : childMap.get("medusa.validator");
        this.associationColumn = MyCommonUtils.isBlank(childMap.get("medusa.associationColumn")) ? "" : childMap.get("medusa.associationColumn");
        this.pluralAssociation = MyCommonUtils.isBlank(childMap.get("medusa.pluralAssociation")) ? "" : childMap.get("medusa.pluralAssociation");

        this.author = MyCommonUtils.isBlank(childMap.get("medusa.author")) ? "administrator" : childMap.get("medusa.author");
        this.entityNameSuffix = MyCommonUtils.isBlank(childMap.get("medusa.entityNameSuffix")) ? "" : childMap.get("medusa.entityNameSuffix");
        this.lazyLoad = MyCommonUtils.isBlank(childMap.get("medusa.lazyLoad")) ? "" : "fetchType=\"lazy\"";
        this.entitySerializable = MyCommonUtils.isBlank(childMap.get("medusa.entitySerializable")) ? "" : childMap.get("medusa.entitySerializable");
        this.baseServiceSwitch = MyCommonUtils.isBlank(childMap.get("medusa.baseServiceSwitch")) ? "" : "gen";


        this.jdbcDriver = MyCommonUtils.isBlank(childMap.get("jdbc.driver")) ? "" : childMap.get("jdbc.driver");
        this.jdbcUrl = MyCommonUtils.isBlank(childMap.get("jdbc.url")) ? "" : childMap.get("jdbc.url");
        this.jdbcUsername = MyCommonUtils.isBlank(childMap.get("jdbc.username")) ? "" : childMap.get("jdbc.username");
        this.jdbcPassword = MyCommonUtils.isBlank(childMap.get("jdbc.password")) ? "" : childMap.get("jdbc.password");

        this.unitModel = MyCommonUtils.isBlank(childMap.get("medusa.unitModel")) ? "" : childMap.get("medusa.unitModel");
        this.ftlDirPath = MyCommonUtils.isBlank(childMap.get("medusa.ftlDirPath")) ? "" : childMap.get("medusa.ftlDirPath");*/
    }

    public static boolean checkIsFtl() {
        boolean result = false;
        if(MyCommonUtils.isNotBlank(ftlDirPath)) result = true;
        return result;
    }

    public static boolean checkIsFtlAvailable() {
        boolean result = false;

        if(checkIsFtl()) {
            File p = new File(ftlDirPath);
            if(p.exists()) return true;
        }
        return result;
    }


    /**
     * 获取数据库的表所有名称
     * @return 返回值类型
     */
    public String getAllTableName() {

        String result = "";

        GenEntity.DataBaseTools dataBaseTools = new GenEntity().new DataBaseTools();

        Connection conn = dataBaseTools.openConnection(); // 得到数据库连接
        PreparedStatement pstmt = null;
        try {
            DatabaseMetaData meta = conn.getMetaData();
            ResultSet rs = meta.getTables(null, null, null, new String[] { "TABLE" });
            while (rs.next()) {
                result = result + rs.getString(3) + ",";
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            dataBaseTools.closeConnection(conn, pstmt);
        }

        return result;
    }
}
