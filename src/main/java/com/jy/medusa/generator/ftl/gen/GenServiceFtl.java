package com.jy.medusa.generator.ftl.gen;

import com.jy.medusa.generator.Home;
import com.jy.medusa.generator.MedusaGenUtils;
import com.jy.medusa.gaze.utils.MedusaDateUtils;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.*;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by SuperScorpion on 16/7/27.
 * service
 * service impl
 * mapper
 */
public class GenServiceFtl {

    private String entityPath;
    private String servicePath;
    private String serviceImplPath;
    private String mapperPath;

    private String entityName;

    private String mixMapper = Home.mixMapper;

    private List<String> markServiceList;
    private List<String> markServiceImplList;
    private List<String> markMapperList;
//    private String tag;//标记 mark

    public GenServiceFtl(String tableName, String entityPath, String servicePath, String serviceImplPath, String mapperPath) {
        this.entityPath = entityPath;
        this.mapperPath = mapperPath;
        this.servicePath = servicePath;
        this.serviceImplPath = serviceImplPath;
        this.entityName = MedusaGenUtils.upcaseFirst(tableName);

//        this.tag = Home.tag;

//        this.markServiceList = MedusaGenUtils.genTagStrList(entityName + "Service.java", servicePath, tag, "service");
//        this.markServiceImplList = MedusaGenUtils.genTagStrList(entityName + "ServiceImpl.java", serviceImplPath, tag, "serviceImpl");
//        this.markMapperList = MedusaGenUtils.genTagStrList(entityName + "Mapper.java", mapperPath, tag, "mapper");
    }

    public void process() {

        try {
            //写入service 和 impl
            String path = Home.proJavaPath + servicePath.replaceAll("\\.", "/");
            File file1 = new File(path);
            if(!file1.exists()) {
                file1.mkdirs();
            }
            String resPath1 = path + "/" + entityName + "Service.java";
//            MedusaCommonUtils.writeString2File(new File(resPath1), process1(), "UTF-8");

            String pathImp = Home.proJavaPath + serviceImplPath.replaceAll("\\.", "/");
            File file2 = new File(pathImp);
            if(!file2.exists()) {
                file2.mkdirs();
            }
            String resPath2 = pathImp + "/" + entityName + "ServiceImpl.java";
//            MedusaCommonUtils.writeString2File(new File(resPath2), process2(), "UTF-8");



            Configuration cfg = new Configuration(Configuration.VERSION_2_3_22);

            if(!Home.checkIsFtlAvailable()) {

                cfg.setClassLoaderForTemplateLoading(this.getClass().getClassLoader(), "/template");
            } else {

                cfg.setDirectoryForTemplateLoading(new File(Home.ftlDirPath));
            }


            //service
            Map<String, Object> map1 = process1();

            Template temp1 = cfg.getTemplate("service.ftl");//TODO

            //如果目标文件已存在 则跳过 add by SuperScorpion on 20230221
            File resPathFile1 = new File(resPath1);
            if(resPathFile1.exists()) {
                System.out.println("Medusa: " + entityName + "Service.java" + " 文件已存在 将跳过生成...");
                return;
            }
            FileOutputStream fos1 = new FileOutputStream(resPathFile1);

            Writer out1 = new BufferedWriter(new OutputStreamWriter(fos1, "utf-8"), 9999);

            if(temp1 != null) temp1.process(map1, out1);


            //serviceImpl
            Map<String, Object> map2 = process2();

            Template temp2 = cfg.getTemplate("serviceImpl.ftl");//TODO

            //如果目标文件已存在 则跳过 add by SuperScorpion on 20230221
            File resPathFile2 = new File(resPath2);
            if(resPathFile2.exists()) {
                System.out.println("Medusa: " + entityName + "ServiceImpl.java" + " 文件已存在 将跳过生成...");
                return;
            }
            FileOutputStream fos2 = new FileOutputStream(resPathFile2);

            Writer out2 = new BufferedWriter(new OutputStreamWriter(fos2, "utf-8"), 9999);

            if(temp2 != null) temp2.process(map2, out2);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TemplateException e) {
            e.printStackTrace();
        }
    }


    /**
     * service
     * @return 返回值类型
     */
    private Map<String, Object> process1() {

        Map<String, Object> map = new HashMap<>();

        map.put("servicePath", servicePath);
        map.put("entityPath", entityPath);
        map.put("entityName", entityName);
        map.put("entityNameSuffix", Home.entityNameSuffix);

        map.put("author", Home.author);
        map.put("now_time", MedusaDateUtils.convertDateToStr(new Date(), null));

        String medusaStarterServicePacName = Home.checkBaseServiceSwitch() ? "" : "com.ysl.medusa.base.BaseService";
        map.put("medusaStarterServicePacName", medusaStarterServicePacName);

        return map;
    }

    /**
     * serviceImpl
     * @return 返回值类型
     */
    private Map<String, Object> process2() {

        Map<String, Object> map = new HashMap<>();

        map.put("serviceImplPath", serviceImplPath);
        map.put("entityPath", entityPath);
        map.put("entityName", entityName);
        map.put("entityNameSuffix", Home.entityNameSuffix);

        map.put("servicePath", servicePath);
        map.put("mapperPath", mapperPath);

        map.put("author", Home.author);
        map.put("now_time", MedusaDateUtils.convertDateToStr(new Date(), null));

        map.put("lowcaseFirstEntityName", MedusaGenUtils.lowcaseFirst(entityName));

        String medusaStarterServiceImplPacName = Home.checkBaseServiceSwitch() ? "" : "com.ysl.medusa.base.BaseServiceImpl";
        map.put("medusaStarterServiceImplPacName", medusaStarterServiceImplPacName);

        return map;
    }
}
