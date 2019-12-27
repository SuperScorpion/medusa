package com.jy.medusa.generator.ftl;

import com.jy.medusa.generator.Home;
import com.jy.medusa.generator.MyGenUtils;
import com.jy.medusa.gaze.utils.MyDateUtils;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.*;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by neo on 16/7/27.
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
        this.servicePath = servicePath;
        this.serviceImplPath = serviceImplPath;
        this.mapperPath = mapperPath;
        this.entityName = MyGenUtils.upcaseFirst(tableName);

//        this.tag = Home.tag;

//        this.markServiceList = MyGenUtils.genTagStrList(entityName + "Service.java", servicePath, tag, "service");
//        this.markServiceImplList = MyGenUtils.genTagStrList(entityName + "ServiceImpl.java", serviceImplPath, tag, "serviceImpl");
//        this.markMapperList = MyGenUtils.genTagStrList(entityName + "Mapper.java", mapperPath, tag, "mapper");
    }

    public void process() {

        try {
            //写入service 和 impl
            String path = Home.proPath + servicePath.replaceAll("\\.", "/");
            File file1 = new File(path);
            if(!file1.exists()) {
                file1.mkdirs();
            }
            String resPath1 = path + "/" + entityName + "Service.java";
//            MyCommonUtils.writeString2File(new File(resPath1), process1(), "UTF-8");

            String pathImp = Home.proPath + serviceImplPath.replaceAll("\\.", "/");
            File file2 = new File(pathImp);
            if(!file2.exists()) {
                file2.mkdirs();
            }
            String resPath2 = pathImp + "/" + entityName + "ServiceImpl.java";
//            MyCommonUtils.writeString2File(new File(resPath2), process2(), "UTF-8");

            //mapper
            String pathmm = Home.proPath + mapperPath.replaceAll("\\.", "/");
            File file3 = new File(pathmm);
            if(!file3.exists()) {
                file3.mkdirs();
            }
            String resPath3 = pathmm + "/" + entityName + "Mapper.java";
//            MyCommonUtils.writeString2File(new File(resPath3), process3(), "UTF-8");





            Configuration cfg = new Configuration(Configuration.VERSION_2_3_22);

            if(!Home.checkIsFtlAvailable()) {

                cfg.setClassLoaderForTemplateLoading(this.getClass().getClassLoader(), "/template");
            } else {

                cfg.setDirectoryForTemplateLoading(new File(Home.ftlDirPath));
            }


            //service
            Map<String, Object> map1 = process1();

            Template temp1 = cfg.getTemplate("service.ftl");//TODO

            FileOutputStream fos1 = new FileOutputStream(new File(resPath1));

            Writer out1 = new BufferedWriter(new OutputStreamWriter(fos1, "utf-8"), 9999);

            if(temp1 != null) temp1.process(map1, out1);


            //serviceImpl
            Map<String, Object> map2 = process2();

            Template temp2 = cfg.getTemplate("serviceImpl.ftl");//TODO

            FileOutputStream fos2 = new FileOutputStream(new File(resPath2));

            Writer out2 = new BufferedWriter(new OutputStreamWriter(fos2, "utf-8"), 9999);

            if(temp2 != null) temp2.process(map2, out2);


            //mapper
            Map<String, Object> map3 = process3();

            Template temp3 = cfg.getTemplate("mapper.ftl");//TODO

            FileOutputStream fos3 = new FileOutputStream(new File(resPath3));

            Writer out3 = new BufferedWriter(new OutputStreamWriter(fos3, "utf-8"), 9999);

            if(temp3 != null) temp3.process(map3, out3);

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
        map.put("now_time", MyDateUtils.convertDateToStr(new Date(), null));

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
        map.put("now_time", MyDateUtils.convertDateToStr(new Date(), null));

        map.put("lowcaseFirstEntityName", MyGenUtils.lowcaseFirst(entityName));

        return map;
    }

    /**
     * mapper
     * @return 返回值类型
     */
    private Map<String, Object> process3() {

        Map<String, Object> map = new HashMap<>();

        map.put("mapperPath", mapperPath);
        map.put("entityPath", entityPath);
        map.put("entityName", entityName);
        map.put("entityNameSuffix", Home.entityNameSuffix);

        map.put("mixMapper", mixMapper);

        map.put("author", Home.author);
        map.put("now_time", MyDateUtils.convertDateToStr(new Date(), null));

        return map;
    }
}
