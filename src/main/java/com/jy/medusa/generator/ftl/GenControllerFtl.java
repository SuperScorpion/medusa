package com.jy.medusa.generator.ftl;

import com.jy.medusa.generator.Home;
import com.jy.medusa.generator.MyGenUtils;
import com.jy.medusa.utils.MyDateUtils;
import com.jy.medusa.utils.SystemConfigs;
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
 */
public class GenControllerFtl {

    private String packagePath;
    private String entityPath;
    private String servicePath;

    private String entityName;

    private List<String> markServiceList;
    private String tag;//标记 mark


    public GenControllerFtl(String tableName, String packagePath, String entityPath, String servicePath, String tag){
        this.entityPath = entityPath;
        this.servicePath = servicePath;
        this.entityName = MyGenUtils.upcaseFirst(tableName);
        this.packagePath = packagePath;
        this.tag = tag;
//        this.markServiceList = MyGenUtils.genTagStrList(entityName + "Controller.java", packagePath, tag, "java");
    }

    public void process() {

        try {
            String path = System.getProperty("user.dir") + "/src/main/java/" + packagePath.replaceAll("\\.", "/");
            File file = new File(path);
            if(!file.exists()){
                file.mkdirs();
            }
            String resPath = path + "/" + entityName + "Controller.java";
//            MyUtils.writeString2File(new File(resPath), home(), "UTF-8");

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

        map.put("packagePath", packagePath);
        map.put("entityName", entityName);
        map.put("entityPath", entityPath);
        map.put("servicePath", servicePath);
        map.put("entityNameSuffix", Home.entityNameSuffix);

        map.put("medusa_pager_path", SystemConfigs.MEDUSA_PAGER_PATH);
        map.put("medusa_myrestriction_path", SystemConfigs.MEDUSA_MYRESTRICTION_PATH);

        map.put("lowcaseFirstEntityName", MyGenUtils.lowcaseFirst(entityName));

        map.put("author", Home.author);
        map.put("now_time", MyDateUtils.convertDateToStr(new Date(), null));

        return map;

    }
}
