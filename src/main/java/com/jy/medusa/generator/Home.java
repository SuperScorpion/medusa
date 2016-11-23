package com.jy.medusa.generator;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;

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

    public static final String mixMapper = "com.jy.medusa.commons.Mapper";

    String packagePath;
    String tableName;
    String jdbcName;

    String tag;
    String medusaProName;
    String entitySuffix;
    String serviceSuffix;
    String mapperSuffix;
    String xmlSuffix;

    String controlJsonSuffix;
    String controlMortalSuffix;
    String validJsonStr;//参数校验
    String ignorAssociation;//忽略的不映射的普通数据库字段
    String pluralAssociation;//设定映射字段的后罪名
    String baseServiceSwitch;///是否生成基础的service类

    public Home(String medusaProName) {
        this.medusaProName = medusaProName;
    }

    public static String getProperPath() {
        return devflag ? medusaPropPath : medusaPropPathDev;
    }

    public static void main(String[] args) {

//        System.out.println(System.getProperty("user.dir") + "/src/main/resources/");

        new Home("medusa.properties").process();
    }

    public void process() {

        System.out.println("**^_^**   有问题或者建议请联系 Binya ");

        loadProperties(medusaProName);

        if(!checkParams()) return;

        String entityPath = packagePath.concat(".").concat(entitySuffix);
        String servicePath = packagePath.concat(".").concat(serviceSuffix);
        String mapperPath = packagePath.concat(".").concat(mapperSuffix);
        String xmlPath = packagePath.concat(".").concat(xmlSuffix);
        String controlPathJson = packagePath.concat(".").concat(controlJsonSuffix);
        String controlPathMortal = packagePath.concat(".").concat(controlMortalSuffix);

        String[] tableNameArray = tableName.split(",");
        if(tableNameArray == null || tableNameArray.length == 0) return;

        //参数校验
        JSONObject job;
        JSONArray m = null;
        if(StringUtils.isNotBlank(validJsonStr)) {
            job = parseValidJson(validJsonStr);
            m = (JSONArray) job.get("validator");
        }

        for(String tabName : tableNameArray) {
            if(StringUtils.isNotBlank(tabName)) {

                tabName = tabName.trim();//祛除空格

                //参数校验
                JSONArray colValidArray = null;
                if(m != null && !m.isEmpty()) {
                    for (Object p : m) {
                        if ((((JSONObject) p).get(tabName)) != null) colValidArray = ((JSONArray) ((JSONObject) p).get(tabName));
                    }
                }

                if(StringUtils.isNotBlank(entitySuffix)) {
                    long nanoSs = System.nanoTime();
                    new GenEntity(entityPath, tabName, jdbcName, tag, colValidArray, ignorAssociation, pluralAssociation).process();//生成 实体类
                    System.out.println(tabName + " entity文件生成用时:" + (System.nanoTime()-nanoSs) + " ns");
                }

                if(StringUtils.isNotBlank(serviceSuffix) && StringUtils.isNotBlank(entitySuffix) && StringUtils.isNotBlank(mapperSuffix)) {
                    long nanoSs = System.nanoTime();
                    new GenService(tabName, entityPath, servicePath, mapperPath, tag).process();//执行生成service serviceimpl mapper
                    System.out.println(tabName + " service文件 mapper文件生成用时:" + (System.nanoTime()-nanoSs) + " ns");
                }

                if(StringUtils.isNotBlank(xmlSuffix) && StringUtils.isNotBlank(entitySuffix) && StringUtils.isNotBlank(mapperSuffix)) {
                    long nanoSs = System.nanoTime();
                    new GenXml(mapperPath, xmlPath, entityPath, tabName, jdbcName, tag, ignorAssociation, pluralAssociation).process();//执行生成xml
                    System.out.println(tabName + " xml文件生成用时:" + (System.nanoTime()-nanoSs) + " ns");
                }

                if(StringUtils.isNotBlank(controlJsonSuffix) && StringUtils.isNotBlank(entitySuffix) && StringUtils.isNotBlank(serviceSuffix)) {
                    long nanoSs = System.nanoTime();
                    new GenControllerJson(tabName, controlPathJson, entityPath, servicePath, tag).process();//生成controller
                    System.out.println(tabName + " controllerJson文件生成用时:" + (System.nanoTime()-nanoSs) + " ns");
                }

                if(StringUtils.isNotBlank(controlMortalSuffix) && StringUtils.isNotBlank(entitySuffix) && StringUtils.isNotBlank(serviceSuffix)) {
                    long nanoSs = System.nanoTime();
                    new GenControllerMortal(tabName, controlPathMortal, entityPath, servicePath, tag).process();//生成controller
                    System.out.println(tabName + " controllerMortal文件生成用时:" + (System.nanoTime()-nanoSs) + " ns");
                }

            }
        }

        if(StringUtils.isNotBlank(baseServiceSwitch)) new GenBaseService(servicePath, tag).process();//处理生成基础的 service
    }

    private JSONObject parseValidJson(String validJsonStr) {
        return StringUtils.isNotBlank(validJsonStr) ? JSONObject.parseObject(validJsonStr) : null;
    }

    private boolean checkParams() {

        boolean result = true;

        if(StringUtils.isBlank(packagePath)) {
            System.out.println("大兄弟你的packagePath没填写!");
            result = false;
        }
        if(StringUtils.isBlank(tableName)) {
            System.out.println("大兄弟你的tableName没填写!");
            result = false;
        }
        if(StringUtils.isBlank(jdbcName)) {
            System.out.println("大兄弟你的jdbcName没填写!");
            result = false;
        }
        if(StringUtils.isBlank(tag)) {
            System.out.println("大兄弟你的tag没填写!");
            result = false;
        }
        if(StringUtils.isBlank(entitySuffix)) {
            System.out.println("大兄弟你的entitySuffix没填写!");
            result = false;
        }
        /*if(StringUtils.isBlank(serviceSuffix)) {
            System.out.println("大兄弟你的serviceSuffix没填写!");
            result = false;
        }
        if(StringUtils.isBlank(mapperSuffix)) {
            System.out.println("大兄弟你的mapperSuffix没填写!");
            result = false;
        }
        if(StringUtils.isBlank(controlSuffix)) {
            System.out.println("大兄弟你的controlSuffix没填写!");
            result = false;
        }*/
/*        if(StringUtils.isBlank(author)) {
            System.out.println("大兄弟你的author没有填写完!");
            result = false;
        }*/
/*        if(StringUtils.isBlank(basePoPath)) {
            System.out.println("大兄弟你的basePoPath没填写!");
            result = false;
        }*/
        /*if(StringUtils.isBlank(baseMapperPath)) {
            System.out.println("大兄弟你的mapperImplPath没填写!");
            result = false;
        }*/
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
        this.jdbcName = props.getProperty("medusa.jdbcName") == null ? "" : props.getProperty("medusa.jdbcName");
        this.tag = props.getProperty("medusa.tag") == null ? "" : props.getProperty("medusa.tag");

        this.entitySuffix = props.getProperty("medusa.entitySuffix") == null ? "" : props.getProperty("medusa.entitySuffix");
        this.serviceSuffix = props.getProperty("medusa.serviceSuffix") == null ? "" : props.getProperty("medusa.serviceSuffix");
        this.mapperSuffix = props.getProperty("medusa.mapperSuffix") == null ? "" : props.getProperty("medusa.mapperSuffix");
        this.xmlSuffix = props.getProperty("medusa.xmlSuffix") == null ? "" : props.getProperty("medusa.xmlSuffix");
        this.controlJsonSuffix = props.getProperty("medusa.controlJsonSuffix") == null ? "" : props.getProperty("medusa.controlJsonSuffix");
        this.controlMortalSuffix = props.getProperty("medusa.controlMortalSuffix") == null ? "" : props.getProperty("medusa.controlMortalSuffix");

        this.validJsonStr = props.getProperty("medusa.validator") == null ? "" : props.getProperty("medusa.validator");
        this.ignorAssociation = props.getProperty("medusa.ignorAssociation") == null ? "" : props.getProperty("medusa.ignorAssociation");
        this.pluralAssociation = props.getProperty("medusa.pluralAssociation") == null ? "" : props.getProperty("medusa.pluralAssociation");

        this.author = StringUtils.isBlank(props.getProperty("medusa.author")) ? "administrator" : props.getProperty("medusa.author");
        this.entityNameSuffix = props.getProperty("medusa.entityNameSuffix") == null ? "" : props.getProperty("medusa.entityNameSuffix");
        this.lazyLoad = StringUtils.isBlank(props.getProperty("medusa.lazyLoad"))  ? "" : "fetchType=\"lazy\"";
        this.baseServiceSwitch = StringUtils.isBlank(props.getProperty("medusa.baseServiceSwitch"))  ? "" : "gen";

        //this.basePoPath = props.getProperty("medusa.basePoPath");
//        this.baseMapperPath = props.getProperty("medusa.baseMapperPath");
    }
}
