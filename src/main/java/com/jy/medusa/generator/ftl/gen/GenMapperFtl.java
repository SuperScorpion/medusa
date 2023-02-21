package com.jy.medusa.generator.ftl.gen;

import com.jy.medusa.gaze.utils.MedusaDateUtils;
import com.jy.medusa.generator.Home;
import com.jy.medusa.generator.MedusaGenUtils;
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
public class GenMapperFtl {

    private String entityPath;
    private String mapperPath;

    private String entityName;

    private String mixMapper = Home.mixMapper;

    private List<String> markServiceList;
    private List<String> markServiceImplList;
    private List<String> markMapperList;
//    private String tag;//标记 mark

    public GenMapperFtl(String tableName, String entityPath, String mapperPath) {
        this.entityPath = entityPath;
        this.mapperPath = mapperPath;
        this.entityName = MedusaGenUtils.upcaseFirst(tableName);

//        this.tag = Home.tag;

//        this.markServiceList = MedusaGenUtils.genTagStrList(entityName + "Service.java", servicePath, tag, "service");
//        this.markServiceImplList = MedusaGenUtils.genTagStrList(entityName + "ServiceImpl.java", serviceImplPath, tag, "serviceImpl");
//        this.markMapperList = MedusaGenUtils.genTagStrList(entityName + "Mapper.java", mapperPath, tag, "mapper");
    }

    public void process() {

        try {
            //mapper
            String pathmm = Home.proJavaPath + mapperPath.replaceAll("\\.", "/");
            File file3 = new File(pathmm);
            if(!file3.exists()) {
                file3.mkdirs();
            }
            String resPath3 = pathmm + "/" + entityName + "Mapper.java";
//            MedusaCommonUtils.writeString2File(new File(resPath3), process3(), "UTF-8");



            Configuration cfg = new Configuration(Configuration.VERSION_2_3_22);

            if(!Home.checkIsFtlAvailable()) {

                cfg.setClassLoaderForTemplateLoading(this.getClass().getClassLoader(), "/template");
            } else {

                cfg.setDirectoryForTemplateLoading(new File(Home.ftlDirPath));
            }



            //mapper
            Map<String, Object> map3 = process3();

            Template temp3 = cfg.getTemplate("mapper.ftl");//TODO

            //如果目标文件已存在 则跳过 add by SuperScorpion on 20230221
            File resPathFile3 = new File(resPath3);
            if(resPathFile3.exists()) {
                System.out.println("Medusa: " + entityName + "Mapper.java" + " 文件已存在 将跳过生成...");
                return;
            }
            FileOutputStream fos3 = new FileOutputStream(resPathFile3);

            Writer out3 = new BufferedWriter(new OutputStreamWriter(fos3, "utf-8"), 9999);

            if(temp3 != null) temp3.process(map3, out3);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (TemplateException e) {
            e.printStackTrace();
        }
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
        map.put("now_time", MedusaDateUtils.convertDateToStr(new Date(), null));

        return map;
    }
}
