package com.jy.medusa.generator;

import com.jy.medusa.gaze.utils.MedusaCommonUtils;
import com.jy.medusa.generator.ftl.GenControllerFtl;
import com.jy.medusa.generator.ftl.GenEntityFtl;
import com.jy.medusa.generator.ftl.GenServiceFtl;
import com.jy.medusa.generator.ftl.GenXmlFtl;
import com.jy.medusa.generator.gen.*;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.net.URL;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

/**
 * Created by neo on 16/8/4.
 */
public class Home {

    public static final String mixMapper = "com.jy.medusa.gaze.commons.Mapper";


    private static boolean devFlag = true;
    public static final String medusaPropPath = "/src/main/resources/";
    public static final String medusaPropPathDev = "/";

    public static String medusaProFileName;

    public static String proJavaPath;
    public static String proResourcePath;

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
    public static String entitySuffix;
    public static String serviceSuffix;
    public static String serviceImplSuffix;
    public static String mapperSuffix;
    public static String xmlSuffix;
//    public static String classpathXml;//以classpath开头的xml生成的路径

    public static String controlJsonSuffix;
    public static String controlMortalSuffix;
//    public static String validJsonStr;//参数校验
    public static String associationColumn;//映射的普通数据库字段级联
    public static String pluralAssociation;//设定映射字段的后罪名
    public static String baseServiceSwitch;///是否生成基础的service类

    public static DataBaseTools staticDataBaseTools;//数据库连接

    public Home() {
    }

    public Home(String medusaProFileName) {
        this.medusaProFileName = medusaProFileName;
    }

    public static String getProperPath() {
        return devFlag ? medusaPropPath : medusaPropPathDev;
    }


    public void process() {
        process(medusaProFileName);
    }

    public void process(String medusaProFileName) {

        loadRandomDialogue();///加载随机旁白

        if(MedusaCommonUtils.isBlank(medusaProFileName)) {
            loadYml("");//处理spring boot 里yml相关medusa的配置 - 先找默认yml文件有无配置项 如果没有再找properties文件配置项
        } else {
            if(medusaProFileName.endsWith(".yml") || medusaProFileName.endsWith(".yaml")) {
                loadYml(medusaProFileName);
            } else if(medusaProFileName.endsWith(".properties")) {
                loadProperties(medusaProFileName);
            } else {
                System.out.println("Medusa: 识别不了该配置文件类型...");
                return;
            }
        }

        loadDataBaseTools();///加载数据库连接配置

//        if(!checkParams()) return;

        String entityPath = packagePath.concat(".").concat(entitySuffix);
        String servicePath = packagePath.concat(".").concat(serviceSuffix);
        String serviceImplPath = packagePath.concat(".").concat(serviceImplSuffix);
        String mapperPath = packagePath.concat(".").concat(mapperSuffix);
        String xmlPath = packagePath.concat(".").concat(xmlSuffix);
        String controlPathJson = packagePath.concat(".").concat(controlJsonSuffix);
        String controlPathMortal = packagePath.concat(".").concat(controlMortalSuffix);

        //参数校验
        /*JSONObject job;
        JSONArray m = null;
        if(MedusaCommonUtils.isNotBlank(validJsonStr)) {
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

        tableName = MedusaCommonUtils.isBlank(tableName) ? getAllTableNameSwitch() : tableName;//如果不写表明则生成所有的表相关
        if(MedusaCommonUtils.isBlank(tableName)) return;
        String[] tableNameArray = tableName.split(",");


        for(String tabName : tableNameArray) {
            if(MedusaCommonUtils.isNotBlank(tabName)) {

                tabName = tabName.trim();//祛除首末的空格

                if(!validateTableExist(tabName)) {
                    System.out.println("Medusa: 不存在该表 " + tabName + " 已跳过...");
                    continue;//add by neo on 20220907 检查表是否存在
                }

                //TODO 自动生成参数校验
                /*JSONArray colValidArray = null;
                if(m != null && !m.isEmpty()) {
                    for (Object p : m) {
                        if ((((JSONObject) p).get(tabName)) != null) colValidArray = ((JSONArray) ((JSONObject) p).get(tabName));
                    }
                }*/

                if(MedusaCommonUtils.isNotBlank(entitySuffix)) {
                    long nanoSs = System.nanoTime();
                    if(checkIsFtl()) {
                        new GenEntityFtl(entityPath, tabName, null).process();
                    } else {
                        new GenEntity(entityPath, tabName, null).process();//生成 实体类
                    }
                    System.out.println("Medusa: 已完成 " + tabName + " - " + entitySuffix + "文件 总用时 - " + (System.nanoTime() - nanoSs) / 1000000.00 + " ms");
                }

                if(MedusaCommonUtils.isNotBlank(serviceImplSuffix) && MedusaCommonUtils.isNotBlank(serviceSuffix) && MedusaCommonUtils.isNotBlank(entitySuffix) && MedusaCommonUtils.isNotBlank(mapperSuffix)) {
                    long nanoSs = System.nanoTime();
                    if(checkIsFtl()) {
                        new GenServiceFtl(tabName, entityPath, servicePath, serviceImplPath, mapperPath).process();
                    } else {
                        new GenService(tabName, entityPath, servicePath, serviceImplPath, mapperPath).process();//执行生成service serviceimpl mapper
                    }
                    System.out.println("Medusa: 已完成 " + tabName + " - " + serviceSuffix + "&impl" + "文件 总用时 - " + (System.nanoTime() - nanoSs) / 1000000.00 + " ms");
                }

                if(MedusaCommonUtils.isNotBlank(xmlSuffix) && MedusaCommonUtils.isNotBlank(entitySuffix) && MedusaCommonUtils.isNotBlank(mapperSuffix)) {
                    long nanoSs = System.nanoTime();
                    if(checkIsFtl()) {
                        new GenXmlFtl(mapperPath, xmlPath, entityPath, tabName).process();
                    } else {
                        new GenXml(mapperPath, xmlPath, entityPath, tabName).process();//执行生成xml
                    }
                    System.out.println("Medusa: 已完成 " + tabName + " - " + mapperSuffix + "&xml" + "文件 总用时 - " + (System.nanoTime() - nanoSs)  / 1000000.00 + " ms");
                }


                if(checkIsFtl()) {
                    long nanoSs = System.nanoTime();
                    new GenControllerFtl(tabName, controlPathJson, entityPath, servicePath).process();
                    System.out.println("Medusa: 已完成 " + tabName + " - controller文件 总用时 - " + (System.nanoTime() - nanoSs) / 1000000.00 + " ms");
                } else {
                    if (MedusaCommonUtils.isNotBlank(entitySuffix) && MedusaCommonUtils.isNotBlank(serviceSuffix)) {
                        if (MedusaCommonUtils.isNotBlank(controlJsonSuffix)) {
                            long nanoSs = System.nanoTime();
                            new GenControllerJson(tabName, controlPathJson, entityPath, servicePath).process();//生成controller
                            System.out.println("Medusa: 已完成 " + tabName + " - controllerJson文件 总用时 - " + (System.nanoTime() - nanoSs) / 1000000.00 + " ms");
                        }
                        if (MedusaCommonUtils.isNotBlank(controlMortalSuffix)) {
                            long nanoSs = System.nanoTime();
                            new GenControllerMortal(tabName, controlPathMortal, entityPath, servicePath).process();//生成controller
                            System.out.println("Medusa: 已完成 " + tabName + " - controllerMortal文件 总用时 - " + (System.nanoTime() - nanoSs) / 1000000.00 + " ms");
                        }
                    }
                }
            }
        }

        ///yml 文件里面的属性值 会自动转换 比如 on->true yes->true no->false
        ///baseService和baseServiceImpl 只需要生成一次所以没做ftl的模版
        if(checkBaseServiceSwitch()) {
            new GenBaseServiceAndImpl(servicePath, serviceImplPath).process();//处理生成基础的 service
            System.out.println("Medusa: 已完成 基础service和impl类文件...");
        }

        System.out.println("Medusa: The task has been completed...");
        System.out.println("Since 2016.09 in Compass - Hangzhou - For XBinYa.");
    }

    /*private JSONObject parseValidJson(String validJsonStr) {
        return MedusaCommonUtils.isNotBlank(validJsonStr) ? JSONObject.parseObject(validJsonStr) : null;
    }*/

    /*private boolean checkParams() {

        boolean result = true;

        if(MedusaCommonUtils.isBlank(packagePath)) {
            System.out.println("大兄弟你的packagePath没填写!");
            result = false;
        }
        return result;
    }*/

    private void loadDataBaseTools() {
        staticDataBaseTools = new DataBaseTools();
    }

    private void loadRandomDialogue() {

        Random ran = new Random();
        int x = ran.nextInt(5);//0,1,2,3,4

        String[] strArrays = new String[5];
        strArrays[0] = "Not born outstanding. - Aogeruimu. Destruction Hammer";
        strArrays[1] = "Finally, I finally liberated myself. - Geluomu. Hell growl";
        strArrays[2] = "Trembling bar, everyone. - Akemengde";
        strArrays[3] = "Desert hoist your gravel, obscured the sun shine. - No scars, Aoshilian";
        strArrays[4] = "Sometimes your whole life boils down to one insane move. - Avatar";

        System.out.println("Medusa: " + strArrays[x]);
        System.out.println("Loading.....");
    }

    private void loadYml(String medusaProFileName) {

        String resPaths = null;
        
        //文件名为空则使用默认文件名
        if(MedusaCommonUtils.isBlank(medusaProFileName)) {
            System.out.println("Medusa: 正在使用默认 application.yml&yaml 配置文件...");
            URL ymlres = this.getClass().getClassLoader().getResource("application.yml");
            URL yamlres = this.getClass().getClassLoader().getResource("application.yaml");

            if(ymlres == null && yamlres != null) {
                resPaths = yamlres.getPath();
            } else if(ymlres != null && yamlres == null) {
                resPaths = ymlres.getPath();
            } else if(ymlres == null && yamlres == null) {
                System.out.println("Medusa: 未能找到 application yml 或 yaml 配置文件...");
                loadProperties("");
                return;
            } else {
                System.out.println("Medusa: 请检查 application yml 或 yaml 配置文件是否存在重复...");
                return;
            }
        } else {///文件名不为空则使用指定文件名
            System.out.println("Medusa: 正在使用指定 yml&yaml 配置文件...");
            URL yyres = this.getClass().getClassLoader().getResource(medusaProFileName);
            if(yyres == null) {
                System.out.println("Medusa: 未能找到指定的yml配置文件...");
                return;
            } else {
                resPaths = yyres.getPath();
            }
        }

        //给proJavaPath赋值 其它地方也要使用
        gainTheProJavaPath(resPaths, medusaProFileName);

        //给proResourcePath赋值 其它地方也要使用
        gainTheProResourcePath(resPaths, medusaProFileName);

        FileInputStream fileInputStream = null;
        try {
            Yaml yaml = new Yaml();//实例化解析器
            File file = new File(resPaths);//配置文件地址
            fileInputStream = new FileInputStream(file);
            Map<String ,Object> map = yaml.loadAs(fileInputStream, Map.class);//装载的对象，这里使用Map, 当然也可使用自己写的对象

            if(map == null) {
                System.out.println("Medusa: 未找到 yml 文件里的 任何配置...");
                loadProperties("");
                return;
            }

            Map<String ,Object> childMap = (Map) map.get("medusa");

            if(childMap == null) {
                System.out.println("Medusa: 未找到 yml 文件里的 medusa 配置...");
                loadProperties("");
                return;
            }
            if(childMap.get("jdbc") == null) {
                System.out.println("Medusa: 未找到 yml 文件里的 medusa - jdbc 配置...");
                return;
            }

            Map<String ,String> jdbcMap = (Map<String, String>) childMap.get("jdbc");

            if(!jdbcMap.containsKey("driver")) {
                System.out.println("Medusa: 未找到 yml 文件里的 medusa - jdbc - driver 配置...");
                return;
            }
            if(!jdbcMap.containsKey("url")) {
                System.out.println("Medusa: 未找到 yml 文件里的 medusa - jdbc - url 配置...");
                return;
            }
            if(!jdbcMap.containsKey("username")) {
                System.out.println("Medusa: 未找到 yml 文件里的 medusa - jdbc - username 配置...");
                return;
            }
            if(!jdbcMap.containsKey("password")) {
                System.out.println("Medusa: 未找到 yml 文件里的 medusa - jdbc - password 配置...");
                return;
            }

            this.packagePath = childMap.get("packagePath") == null || MedusaCommonUtils.isBlank(childMap.get("packagePath").toString()) ? "com.medusa.xxx" : childMap.get("packagePath").toString().trim();
            this.tableName = childMap.get("tableName") == null || MedusaCommonUtils.isBlank(childMap.get("tableName").toString()) ?  "" : childMap.get("tableName").toString().trim();
            this.tag = childMap.get("tag") == null || MedusaCommonUtils.isBlank(childMap.get("tag").toString()) ?  "<" : childMap.get("tag").toString().trim();

            this.entitySuffix = childMap.get("entitySuffix") == null || MedusaCommonUtils.isBlank(childMap.get("entitySuffix").toString()) ?  "entity" : childMap.get("entitySuffix").toString().trim();
            this.serviceSuffix = childMap.get("serviceSuffix") == null || MedusaCommonUtils.isBlank(childMap.get("serviceSuffix").toString()) ?  "service" : childMap.get("serviceSuffix").toString().trim();
            this.serviceImplSuffix = childMap.get("serviceImplSuffix") == null || MedusaCommonUtils.isBlank(childMap.get("serviceImplSuffix").toString()) ?  "service.impl" : childMap.get("serviceImplSuffix").toString().trim();
            this.mapperSuffix = childMap.get("mapperSuffix") == null || MedusaCommonUtils.isBlank(childMap.get("mapperSuffix").toString()) ?  "persistence" : childMap.get("mapperSuffix").toString().trim();
            this.xmlSuffix = childMap.get("xmlSuffix") == null || MedusaCommonUtils.isBlank(childMap.get("xmlSuffix").toString()) ?  "persistence.xml" : childMap.get("xmlSuffix").toString().trim();
//            this.classpathXml = childMap.get("classpathXml") == null || MedusaCommonUtils.isBlank(childMap.get("classpathXml").toString()) ?  "" : childMap.get("classpathXml").toString().trim();
            this.controlJsonSuffix = childMap.get("controlJsonSuffix") == null || MedusaCommonUtils.isBlank(childMap.get("controlJsonSuffix").toString()) ?  "controller" : childMap.get("controlJsonSuffix").toString().trim();
            this.controlMortalSuffix = childMap.get("controlMortalSuffix") == null || MedusaCommonUtils.isBlank(childMap.get("controlMortalSuffix").toString()) ?  "" : childMap.get("controlMortalSuffix").toString().trim();

            this.associationColumn = childMap.get("associationColumn") == null || MedusaCommonUtils.isBlank(childMap.get("associationColumn").toString()) ?  "" : childMap.get("associationColumn").toString().trim();
            this.pluralAssociation = childMap.get("pluralAssociation") == null || MedusaCommonUtils.isBlank(childMap.get("pluralAssociation").toString()) ?  "s" : childMap.get("pluralAssociation").toString().trim();

            this.author = childMap.get("author") == null || MedusaCommonUtils.isBlank(childMap.get("author").toString()) ?  "administrator" : childMap.get("author").toString().trim();
            this.entityNameSuffix = childMap.get("entityNameSuffix") == null || MedusaCommonUtils.isBlank(childMap.get("entityNameSuffix").toString()) ?  "" : childMap.get("entityNameSuffix").toString().trim();
            this.lazyLoad = childMap.get("lazyLoad") == null || MedusaCommonUtils.isBlank(childMap.get("lazyLoad").toString()) ?  "" : "fetchType=\"lazy\"";
            this.entitySerializable = childMap.get("entitySerializable") == null || MedusaCommonUtils.isBlank(childMap.get("entitySerializable").toString()) ?  "" : childMap.get("entitySerializable").toString().trim();
            this.baseServiceSwitch = childMap.get("baseServiceSwitch") == null || MedusaCommonUtils.isBlank(childMap.get("baseServiceSwitch").toString()) ?  "" : childMap.get("baseServiceSwitch").toString();

            this.ftlDirPath = childMap.get("ftlDirPath") == null || MedusaCommonUtils.isBlank(childMap.get("ftlDirPath").toString()) ?  "" : childMap.get("ftlDirPath").toString().trim();

            this.jdbcDriver = jdbcMap.get("driver") == null || MedusaCommonUtils.isBlank(jdbcMap.get("driver").toString()) ?  "" : jdbcMap.get("driver").toString().trim();
            this.jdbcUrl = jdbcMap.get("url") == null || MedusaCommonUtils.isBlank(jdbcMap.get("url").toString()) ?  "" : jdbcMap.get("url").toString().trim();
            this.jdbcUsername = jdbcMap.get("username") == null || MedusaCommonUtils.isBlank(jdbcMap.get("username").toString()) ?  "" : jdbcMap.get("username").toString().trim();
            this.jdbcPassword = jdbcMap.get("password") == null || MedusaCommonUtils.isBlank(jdbcMap.get("password").toString()) ?  "" : jdbcMap.get("password").toString().trim();

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
     * @param medusaProFileName 参数
     * @param resPaths 参数
     */
    private void gainTheProJavaPath(String resPaths, String medusaProFileName) {
        String path = resPaths.replaceAll("/target/classes", "/src/main/java");
        this.proJavaPath = path.replaceAll("application.yml|application.yaml|medusa.properties", "").replaceAll(medusaProFileName, "");
    }

    /**
     * 多模块的环境下 System.getProperty("user.dir") 获得的路径不够深入 所以用classLoader处理
     * /Users/neo/Desktop/my-work/arbitrage/webapi/target/classes/application.yml
     * /Users/neo/Desktop/my-work/arbitrage/webapi/src/main/resources/
     * @param resPaths 参数
     */
    private void gainTheProResourcePath(String resPaths, String medusaProFileName) {
        String path = resPaths.replaceAll("/target/classes", "/src/main/resources");
        this.proResourcePath = path.replaceAll("application.yml|application.yaml|medusa.properties", "").replaceAll(medusaProFileName, "");
    }


    public static boolean checkIsFtl() {
        boolean result = false;
        if(MedusaCommonUtils.isNotBlank(ftlDirPath)) result = true;
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

    public static boolean checkBaseServiceSwitch() {
        boolean result = false;

        if(MedusaCommonUtils.isNotBlank(baseServiceSwitch) &&
                (baseServiceSwitch.trim().equalsIgnoreCase("true") || baseServiceSwitch.trim().equalsIgnoreCase("yes")
                        || baseServiceSwitch.trim().equalsIgnoreCase("y") || baseServiceSwitch.trim().equalsIgnoreCase("ok")))
            result = true;

        return result;
    }


    /**
     * 确认是否生成库里所有表
     * y 获取数据库的表所有名称
     * n 返回空的字符串
     * @return
     */
    private String getAllTableNameSwitch() {

        System.out.println("Medusa: 确定生成所有表? y/n");
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));

        try {
            String flag = bufferedReader.readLine();
            if(flag.equalsIgnoreCase("y")) {
                String allTabName = getAllTableName();
                if(MedusaCommonUtils.isBlank(allTabName)) System.out.println("Medusa: 请确认数据库里存在表结构...");
                return allTabName;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }

    /**
     * 获取数据库的表所有名称
     * @return 返回值类型
     */
    private String getAllTableName() {

        String result = "";

        DataBaseTools dataBaseTools = staticDataBaseTools;

        Connection conn = dataBaseTools.openConnection(); // 得到数据库连接
        PreparedStatement pstmt = null;
        try {
            DatabaseMetaData meta = conn.getMetaData();
            ResultSet rs = meta.getTables(null, null, null, new String[] {"TABLE"});
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


    /**
     * 检查表是否存在
     * @param tableName
     * @return
     */
    public boolean validateTableExist(String tableName) {
        boolean flag = false;

        DataBaseTools dataBaseTools =  staticDataBaseTools;

        Connection conn = dataBaseTools.openConnection(); // 得到数据库连接
        PreparedStatement pstmt = null;
        try {
            DatabaseMetaData meta = conn.getMetaData();
            ResultSet rs = meta.getTables(null, null, tableName, new String[] {"TABLE"});
            flag = rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            dataBaseTools.closeConnection(conn, pstmt);
        }

        return flag;
    }




    /**
     * modify by admin on 20220714 for 万弟弟
     * 1.yml文件找不到则启用properties文件加载 | 2.指定使用properties文件加载
     * @param medusaProFileName 一定有值 1.用户指定文件名 2.默认值 medusa.properties
     */
    private void loadProperties(String medusaProFileName) {

        if(MedusaCommonUtils.isBlank(medusaProFileName)) {
            System.out.println("Medusa: 正在使用默认 application.properties 配置文件生成...");
            medusaProFileName = "application.properties";
        } else {
            System.out.println("Medusa: 正在使用指定 properties 配置文件生成...");
        }

        URL yyres = this.getClass().getClassLoader().getResource(medusaProFileName);
        if(yyres == null) {
            System.out.println("Medusa: 未能找到指定的properties配置文件...");
            return;
        }

        Properties props = new Properties();
        try {
            props.load(this.getClass().getClassLoader().getResourceAsStream(medusaProFileName));
        } catch (FileNotFoundException e) {
            System.out.println("Medusa: 未找到配置文件的异常 " + medusaProFileName + " 请检查配置...");
            e.printStackTrace();
            return;
        } catch (Exception e) {
            System.out.println("Medusa: 加载properties文件的异常 " + medusaProFileName + " 请检查配置...");
            e.printStackTrace();
            System.out.println("Medusa: 请确认 yml 或 properties 配置文件正确...");
            return;
        }

        //给proJavaPath赋值 其它地方也要使用
        gainTheProJavaPath(this.getClass().getClassLoader().getResource(medusaProFileName).getPath(), medusaProFileName);

        //给proResourcePath赋值 其它地方也要使用
        gainTheProResourcePath(this.getClass().getClassLoader().getResource(medusaProFileName).getPath(), medusaProFileName);


        Map<String, String> childMap = new HashMap<>((Map) props);

        if(childMap.isEmpty()) {
            System.out.println("Medusa: 未找到 properties 文件里的 任何配置...");
            return;
        }

        if(!childMap.containsKey("medusa.jdbc.driver")) {
            System.out.println("Medusa: 未找到 properties 文件里的 medusa.jdbc.driver 配置...");
            return;
        }
        if(!childMap.containsKey("medusa.jdbc.url")) {
            System.out.println("Medusa: 未找到 properties 文件里的 medusa.jdbc.url 配置...");
            return;
        }
        if(!childMap.containsKey("medusa.jdbc.username")) {
            System.out.println("Medusa: 未找到 properties 文件里的 medusa.jdbc.username 配置...");
            return;
        }
        if(!childMap.containsKey("medusa.jdbc.password")) {
            System.out.println("Medusa: 未找到 properties 文件里的 medusa.jdbc.password 配置...");
            return;
        }


        String prefix = "medusa.";
        String jdbcPrefix = prefix + "jdbc.";

        this.packagePath = childMap.get(prefix + "packagePath") == null || MedusaCommonUtils.isBlank(childMap.get(prefix + "packagePath").toString()) ? "com.medusa.xxx" : childMap.get(prefix + "packagePath").toString().trim();
        this.tableName = childMap.get(prefix + "tableName") == null || MedusaCommonUtils.isBlank(childMap.get(prefix + "tableName").toString()) ?  "" : childMap.get(prefix + "tableName").toString().trim();
        this.tag = childMap.get(prefix + "tag") == null || MedusaCommonUtils.isBlank(childMap.get(prefix + "tag").toString()) ?  "<" : childMap.get(prefix + "tag").toString().trim();

        this.entitySuffix = childMap.get(prefix + "entitySuffix") == null || MedusaCommonUtils.isBlank(childMap.get(prefix + "entitySuffix").toString()) ?  "entity" : childMap.get(prefix + "entitySuffix").toString().trim();
        this.serviceSuffix = childMap.get(prefix + "serviceSuffix") == null || MedusaCommonUtils.isBlank(childMap.get(prefix + "serviceSuffix").toString()) ?  "service" : childMap.get(prefix + "serviceSuffix").toString().trim();
        this.serviceImplSuffix = childMap.get(prefix + "serviceImplSuffix") == null || MedusaCommonUtils.isBlank(childMap.get(prefix + "serviceImplSuffix").toString()) ?  "service.impl" : childMap.get(prefix + "serviceImplSuffix").toString().trim();
        this.mapperSuffix = childMap.get(prefix + "mapperSuffix") == null || MedusaCommonUtils.isBlank(childMap.get(prefix + "mapperSuffix").toString()) ?  "persistence" : childMap.get(prefix + "mapperSuffix").toString().trim();
        this.xmlSuffix = childMap.get(prefix + "xmlSuffix") == null || MedusaCommonUtils.isBlank(childMap.get(prefix + "xmlSuffix").toString()) ?  "persistence.xml" : childMap.get(prefix + "xmlSuffix").toString().trim();
//            this.classpathXml = childMap.get(prefix + "classpathXml") == null || MedusaCommonUtils.isBlank(childMap.get(prefix + "classpathXml").toString()) ?  "" : childMap.get(prefix + "classpathXml").toString().trim();
        this.controlJsonSuffix = childMap.get(prefix + "controlJsonSuffix") == null || MedusaCommonUtils.isBlank(childMap.get(prefix + "controlJsonSuffix").toString()) ?  "controller" : childMap.get(prefix + "controlJsonSuffix").toString().trim();
        this.controlMortalSuffix = childMap.get(prefix + "controlMortalSuffix") == null || MedusaCommonUtils.isBlank(childMap.get(prefix + "controlMortalSuffix").toString()) ?  "" : childMap.get(prefix + "controlMortalSuffix").toString().trim();

        this.associationColumn = childMap.get(prefix + "associationColumn") == null || MedusaCommonUtils.isBlank(childMap.get(prefix + "associationColumn").toString()) ?  "" : childMap.get(prefix + "associationColumn").toString().trim();
        this.pluralAssociation = childMap.get(prefix + "pluralAssociation") == null || MedusaCommonUtils.isBlank(childMap.get(prefix + "pluralAssociation").toString()) ?  "s" : childMap.get(prefix + "pluralAssociation").toString().trim();

        this.author = childMap.get(prefix + "author") == null || MedusaCommonUtils.isBlank(childMap.get(prefix + "author").toString()) ?  "administrator" : childMap.get(prefix + "author").toString().trim();
        this.entityNameSuffix = childMap.get(prefix + "entityNameSuffix") == null || MedusaCommonUtils.isBlank(childMap.get(prefix + "entityNameSuffix").toString()) ?  "" : childMap.get(prefix + "entityNameSuffix").toString().trim();
        this.lazyLoad = childMap.get(prefix + "lazyLoad") == null || MedusaCommonUtils.isBlank(childMap.get(prefix + "lazyLoad").toString()) ?  "" : "fetchType=\"lazy\"";
        this.entitySerializable = childMap.get(prefix + "entitySerializable") == null || MedusaCommonUtils.isBlank(childMap.get(prefix + "entitySerializable").toString()) ?  "" : childMap.get(prefix + "entitySerializable").toString().trim();
        this.baseServiceSwitch = childMap.get(prefix + "baseServiceSwitch") == null || MedusaCommonUtils.isBlank(childMap.get(prefix + "baseServiceSwitch").toString()) ?  "" : childMap.get(prefix + "baseServiceSwitch").toString();

        this.ftlDirPath = childMap.get(prefix + "ftlDirPath") == null || MedusaCommonUtils.isBlank(childMap.get(prefix + "ftlDirPath").toString()) ?  "" : childMap.get(prefix + "ftlDirPath").toString().trim();

        this.jdbcDriver = childMap.get(jdbcPrefix + "driver") == null || MedusaCommonUtils.isBlank(childMap.get(jdbcPrefix + "driver").toString()) ?  "" : childMap.get(jdbcPrefix + "driver").toString().trim();
        this.jdbcUrl = childMap.get(jdbcPrefix + "url") == null || MedusaCommonUtils.isBlank(childMap.get(jdbcPrefix + "url").toString()) ?  "" : childMap.get(jdbcPrefix + "url").toString().trim();
        this.jdbcUsername = childMap.get(jdbcPrefix + "username") == null || MedusaCommonUtils.isBlank(childMap.get(jdbcPrefix + "username").toString()) ?  "" : childMap.get(jdbcPrefix + "username").toString().trim();
        this.jdbcPassword = childMap.get(jdbcPrefix + "password") == null || MedusaCommonUtils.isBlank(childMap.get(jdbcPrefix + "password").toString()) ?  "" : childMap.get(jdbcPrefix + "password").toString().trim();
    }
}
