package com.jy.medusa.generator;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jy.medusa.generator.ftl.GenControllerFtl;
import com.jy.medusa.generator.ftl.GenEntityFtl;
import com.jy.medusa.generator.ftl.GenServiceFtl;
import com.jy.medusa.generator.ftl.GenXmlFtl;
import com.jy.medusa.utils.MyUtils;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by neo on 16/8/4.
 */
public class Home {

    private static boolean devflag = true;

    public static final String medusaPropPath = "/src/main/resources/";
    public static final String medusaPropPathDev = "/";

    public static String author;
    public static String entityNameSuffix;//实体文件后缀名
    public static String lazyLoad;
    public static String entitySerializable;
    public static String unitModel;
    public static String ftlDirPath;

    public static String jdbcDriver;
    public static String jdbcUrl;
    public static String jdbcUsername;
    public static String jdbcPassword;

    public static final String mixMapper = "com.jy.medusa.commons.Mapper";

    String packagePath;
    String tableName;
//    String jdbcName;///////该字段已弃用了

    String tag;
    String medusaProName;
    String entitySuffix;
    String serviceSuffix;
    String serviceImplSuffix;
    String mapperSuffix;
    String xmlSuffix;

    String controlJsonSuffix;
    String controlMortalSuffix;
    String validJsonStr;//参数校验
    String associationColumn;//映射的普通数据库字段级联
    String pluralAssociation;//设定映射字段的后罪名
    String baseServiceSwitch;///是否生成基础的service类

    public Home(String medusaProName) {
        this.medusaProName = medusaProName;
    }

    public static String getProperPath() {
        return devflag ? medusaPropPath : medusaPropPathDev;
    }


    public void process() throws IOException, TemplateException {

        System.out.println("Strike down upon the with great venganceandfury!Xbinya");
        System.out.println("Loading...");

        loadProperties(medusaProName);

        if(!checkParams()) return;

        String entityPath = packagePath.concat(".").concat(entitySuffix);
        String servicePath = packagePath.concat(".").concat(serviceSuffix);
        String serviceImplPath = packagePath.concat(".").concat(serviceImplSuffix);
        String mapperPath = packagePath.concat(".").concat(mapperSuffix);
        String xmlPath = packagePath.concat(".").concat(xmlSuffix);
        String controlPathJson = packagePath.concat(".").concat(controlJsonSuffix);
        String controlPathMortal = packagePath.concat(".").concat(controlMortalSuffix);

        String[] tableNameArray = tableName.split(",");
        if(tableNameArray == null || tableNameArray.length == 0) return;

        //参数校验
        JSONObject job;
        JSONArray m = null;
        if(MyUtils.isNotBlank(validJsonStr)) {
            job = parseValidJson(validJsonStr);
            m = (JSONArray) job.get("validator");
        }

        for(String tabName : tableNameArray) {
            if(MyUtils.isNotBlank(tabName)) {

                tabName = tabName.trim();//祛除空格

                //参数校验
                JSONArray colValidArray = null;
                if(m != null && !m.isEmpty()) {
                    for (Object p : m) {
                        if ((((JSONObject) p).get(tabName)) != null) colValidArray = ((JSONArray) ((JSONObject) p).get(tabName));
                    }
                }

                if(MyUtils.isNotBlank(entitySuffix)) {
                    long nanoSs = System.nanoTime();
                    if(checkIsFtl()) {
                        new GenEntityFtl(entityPath, tabName, tag, colValidArray, associationColumn, pluralAssociation).process();
                    } else {
                        new GenEntity(entityPath, tabName, tag, colValidArray, associationColumn, pluralAssociation).process();//生成 实体类
                    }
                    System.out.println(tabName + " entity文件生成用时:" + (System.nanoTime() - nanoSs) + " ns");
                }

                if(MyUtils.isNotBlank(serviceImplSuffix) && MyUtils.isNotBlank(serviceSuffix) && MyUtils.isNotBlank(entitySuffix) && MyUtils.isNotBlank(mapperSuffix)) {
                    long nanoSs = System.nanoTime();
                    if(checkIsFtl()) {
                        new GenServiceFtl(tabName, entityPath, servicePath, serviceImplPath, mapperPath, tag).process();
                    } else {
                        new GenService(tabName, entityPath, servicePath, serviceImplPath, mapperPath, tag).process();//执行生成service serviceimpl mapper
                    }
                    System.out.println(tabName + " service文件 mapper文件生成用时:" + (System.nanoTime() - nanoSs) + " ns");
                }

                if(MyUtils.isNotBlank(xmlSuffix) && MyUtils.isNotBlank(entitySuffix) && MyUtils.isNotBlank(mapperSuffix)) {
                    long nanoSs = System.nanoTime();
                    if(checkIsFtl()) {
                        new GenXmlFtl(mapperPath, xmlPath, entityPath, tabName, tag, associationColumn, pluralAssociation).process();
                    } else {
                        new GenXml(mapperPath, xmlPath, entityPath, tabName, tag, associationColumn, pluralAssociation).process();//执行生成xml
                    }
                    System.out.println(tabName + " xml文件生成用时:" + (System.nanoTime() - nanoSs) + " ns");
                }


                if(checkIsFtl()) {
                    long nanoSs = System.nanoTime();
                    new GenControllerFtl(tabName, controlPathJson, entityPath, servicePath, tag).process();
                    System.out.println(tabName + " controller 文件生成用时:" + (System.nanoTime() - nanoSs) + " ns");
                } else {
                    if(MyUtils.isNotBlank(controlJsonSuffix) && MyUtils.isNotBlank(entitySuffix) && MyUtils.isNotBlank(serviceSuffix)) {
                        long nanoSs = System.nanoTime();
                        new GenControllerJson(tabName, controlPathJson, entityPath, servicePath, tag).process();//生成controller
                        System.out.println(tabName + " controllerJson文件生成用时:" + (System.nanoTime() - nanoSs) + " ns");
                    }

                    if(MyUtils.isNotBlank(controlMortalSuffix) && MyUtils.isNotBlank(entitySuffix) && MyUtils.isNotBlank(serviceSuffix)) {
                        long nanoSs = System.nanoTime();
                        new GenControllerMortal(tabName, controlPathMortal, entityPath, servicePath, tag).process();//生成controller
                        System.out.println(tabName + " controllerMortal文件生成用时:" + (System.nanoTime() - nanoSs) + " ns");
                    }
                }

            }
        }

        if(MyUtils.isNotBlank(baseServiceSwitch)) new GenBaseService(servicePath, serviceImplPath, tag).process();//处理生成基础的 service
    }

    private JSONObject parseValidJson(String validJsonStr) {
        return MyUtils.isNotBlank(validJsonStr) ? JSONObject.parseObject(validJsonStr) : null;
    }

    private boolean checkParams() {

        boolean result = true;

        if(MyUtils.isBlank(packagePath)) {
            System.out.println("大兄弟你的packagePath没填写!");
            result = false;
        }
        if(MyUtils.isBlank(tableName)) {
            System.out.println("大兄弟你的tableName没填写!");
            result = false;
        }
        if(MyUtils.isBlank(tag)) {
            System.out.println("大兄弟你的tag没填写!");
            result = false;
        }
        if(MyUtils.isBlank(entitySuffix)) {
            System.out.println("大兄弟你的entitySuffix没填写!");
            result = false;
        }
        if(MyUtils.isBlank(jdbcDriver)) {
            System.out.println("大兄弟你的jdbcDriver没填写!");
            result = false;
        }
        if(MyUtils.isBlank(jdbcUrl)) {
            System.out.println("大兄弟你的jdbcUrl没填写!");
            result = false;
        }
        if(MyUtils.isBlank(jdbcUsername)) {
            System.out.println("大兄弟你的jdbcUsername没填写!");
            result = false;
        }

        return result;
    }


    private void loadProperties(String fileName) {

        String resPaths = System.getProperty("user.dir") + getProperPath() + fileName;

        Properties props = new Properties();
        try {
            props.load(new FileInputStream(resPaths));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.packagePath = props.getProperty("medusa.packagePath") == null ? "" : props.getProperty("medusa.packagePath");
        this.tableName = props.getProperty("medusa.tableName") == null ? "" : props.getProperty("medusa.tableName");
        this.tag = props.getProperty("medusa.tag") == null ? "" : props.getProperty("medusa.tag");

        this.entitySuffix = props.getProperty("medusa.entitySuffix") == null ? "" : props.getProperty("medusa.entitySuffix");
        this.serviceSuffix = props.getProperty("medusa.serviceSuffix") == null ? "" : props.getProperty("medusa.serviceSuffix");
        this.serviceImplSuffix = props.getProperty("medusa.serviceImplSuffix") == null ? "" : props.getProperty("medusa.serviceImplSuffix");
        this.mapperSuffix = props.getProperty("medusa.mapperSuffix") == null ? "" : props.getProperty("medusa.mapperSuffix");
        this.xmlSuffix = props.getProperty("medusa.xmlSuffix") == null ? "" : props.getProperty("medusa.xmlSuffix");
        this.controlJsonSuffix = props.getProperty("medusa.controlJsonSuffix") == null ? "" : props.getProperty("medusa.controlJsonSuffix");
        this.controlMortalSuffix = props.getProperty("medusa.controlMortalSuffix") == null ? "" : props.getProperty("medusa.controlMortalSuffix");

        this.validJsonStr = props.getProperty("medusa.validator") == null ? "" : props.getProperty("medusa.validator");
        this.associationColumn = props.getProperty("medusa.associationColumn") == null ? "" : props.getProperty("medusa.associationColumn");
        this.pluralAssociation = MyUtils.isBlank(props.getProperty("medusa.pluralAssociation")) ? "" : props.getProperty("medusa.pluralAssociation");

        this.author = MyUtils.isBlank(props.getProperty("medusa.author")) ? "administrator" : props.getProperty("medusa.author");
        this.entityNameSuffix = MyUtils.isBlank(props.getProperty("medusa.entityNameSuffix")) ? "" : props.getProperty("medusa.entityNameSuffix");
        this.lazyLoad = MyUtils.isBlank(props.getProperty("medusa.lazyLoad"))  ? "" : "fetchType=\"lazy\"";
        this.entitySerializable = MyUtils.isBlank(props.getProperty("medusa.entitySerializable"))  ? "" : props.getProperty("medusa.entitySerializable");
        this.baseServiceSwitch = MyUtils.isBlank(props.getProperty("medusa.baseServiceSwitch"))  ? "" : "gen";


        this.jdbcDriver = props.getProperty("jdbc.driver") == null ? "" : props.getProperty("jdbc.driver");
        this.jdbcUrl = props.getProperty("jdbc.url") == null ? "" : props.getProperty("jdbc.url");
        this.jdbcUsername = props.getProperty("jdbc.username") == null ? "" : props.getProperty("jdbc.username");
        this.jdbcPassword = props.getProperty("jdbc.password") == null ? "" : props.getProperty("jdbc.password");

        this.unitModel = MyUtils.isBlank(props.getProperty("medusa.unitModel")) ? "" : props.getProperty("medusa.unitModel");
        this.ftlDirPath = MyUtils.isBlank(props.getProperty("medusa.ftlDirPath")) ? "" : props.getProperty("medusa.ftlDirPath");
    }

    public static boolean checkIsFtl() {
        boolean result = false;
        if(MyUtils.isNotBlank(ftlDirPath)) result = true;
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
}
