package com.jy.medusa.generator.ftl;

import com.jy.medusa.generator.Home;
import com.jy.medusa.generator.MedusaGenUtils;
import com.jy.medusa.gaze.utils.MedusaDateUtils;
import com.jy.medusa.gaze.utils.SystemConfigs;
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
 */
public class GenControllerFtl {

    private String controlPath;
    private String entityPath;
    private String servicePath;

    private String entityName;

    private List<String> markServiceList;
    private String tag;//标记 mark


    public GenControllerFtl(String tableName, String controlPath, String entityPath, String servicePath) {
        this.entityPath = entityPath;
        this.servicePath = servicePath;
        this.entityName = MedusaGenUtils.upcaseFirst(tableName);
        this.controlPath = controlPath;
        this.tag = Home.tag;
//        this.markServiceList = MedusaGenUtils.genTagStrList(entityName + "Controller.java", controlPath, tag, "java");
    }

    public void process() {

        try {
            String path = Home.proJavaPath + controlPath.replaceAll("\\.", "/");
            File file = new File(path);
            if(!file.exists()) {
                file.mkdirs();
            }
            String resPath = path + "/" + entityName + "Controller.java";
//            MedusaCommonUtils.writeString2File(new File(resPath), home(), "UTF-8");

            Map<String, Object> map = home();

            Configuration cfg = new Configuration(Configuration.VERSION_2_3_22);

            if(!Home.checkIsFtlAvailable()) {

                cfg.setClassLoaderForTemplateLoading(this.getClass().getClassLoader(), "/template");
            } else {

                cfg.setDirectoryForTemplateLoading(new File(Home.ftlDirPath));
            }


            Template temp = cfg.getTemplate("controller.ftl");//TODO

            FileOutputStream fos = new FileOutputStream(new File(resPath));

            Writer out = new BufferedWriter(new OutputStreamWriter(fos, "utf-8"), 10240);

            if(temp != null) {
                try {
                    temp.process(map, out);
                } catch (TemplateException e) {
                    e.printStackTrace();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * controller
     * @return 返回值类型
     */
    private Map<String, Object> home() {

        Map<String, Object> map = new HashMap<>();

        map.put("controlPath", controlPath);
        map.put("entityName", entityName);
        map.put("entityPath", entityPath);
        map.put("servicePath", servicePath);
        map.put("entityNameSuffix", Home.entityNameSuffix);

        map.put("medusa_pager_path", SystemConfigs.MEDUSA_PAGER_PATH);
        map.put("medusa_myrestriction_path", SystemConfigs.MEDUSA_MYRESTRICTION_PATH);

        map.put("lowcaseFirstEntityName", MedusaGenUtils.lowcaseFirst(entityName));

        map.put("author", Home.author);
        map.put("now_time", MedusaDateUtils.convertDateToStr(new Date(), null));

        return map;

    }
}
