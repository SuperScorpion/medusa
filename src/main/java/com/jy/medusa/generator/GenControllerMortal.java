package com.jy.medusa.generator;

import com.jy.medusa.utils.MyDateUtils;
import com.jy.medusa.utils.MyUtils;
import com.jy.medusa.utils.SystemConfigs;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * Created by neo on 16/7/27.
 */
public class GenControllerMortal {

    private String packagePath;
    private String entityPath;
    private String servicePath;

    private String entityName;

    private List<String> markServiceList;
    private String tag;//标记 mark


    public GenControllerMortal(String tableName, String packagePath, String entityPath, String servicePath, String tag){
        this.entityPath = entityPath;
        this.servicePath = servicePath;
        this.entityName = MyGenUtils.upcaseFirst(tableName);
        this.packagePath = packagePath;
        this.tag = tag;
        this.markServiceList = MyGenUtils.genTagStrList(entityName + "Controller.java", packagePath, tag, "java");
    }

    public void process(){

        try {
            String path = System.getProperty("user.dir") + "/src/main/java/" + packagePath.replaceAll("\\.", "/");
            File file = new File(path);
            if(!file.exists()){
                file.mkdirs();
            }
            String resPath = path + "/" + entityName + "Controller.java";
            MyUtils.writeString2File(new File(resPath), home(), "UTF-8");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * controller
     * @return
     */
    private String home() {

        StringBuilder sbb = new StringBuilder();

        sbb.append("package " + packagePath + ";\r\n\r\n");

        sbb.append("import " + entityPath + "." + entityName + Home.entityNameSuffix + ";\r\n");
        sbb.append("import " + servicePath + "." + entityName + "Service" + ";\r\n");
//        sbb.append("import com.alibaba.fastjson.JSONObject;\r\n");

        sbb.append("import " + SystemConfigs.MEDUSA_PAGER_PATH + ";\r\n");
        sbb.append("import " + SystemConfigs.MEDUSA_MYRESTRICTION_PATH + ";\r\n");

        sbb.append("import org.slf4j.Logger;\r\n");
        sbb.append("import org.slf4j.LoggerFactory;\r\n");
        sbb.append("import org.springframework.stereotype.Controller;\r\n");
        sbb.append("import org.springframework.ui.ModelMap;\r\n");
        sbb.append("import org.springframework.web.bind.annotation.RequestMapping;\r\n");
        sbb.append("import org.springframework.web.bind.annotation.RequestMethod;\r\n");
        sbb.append("import org.springframework.web.bind.annotation.RequestParam;\r\n");
//        sbb.append("import org.springframework.web.bind.annotation.ResponseBody;\r\n");
        sbb.append("import javax.servlet.http.HttpServletRequest;\r\n");
        sbb.append("import javax.annotation.Resource;\r\n\r\n");

        //添加作者
        sbb.append("/**\r\n");
        sbb.append(" * Created by " + Home.author + " on " + MyDateUtils.convertDateToStr(new Date(), null) + "\r\n");
        sbb.append(" */\r\n");

        sbb.append("@Controller\r\n");
        sbb.append("@RequestMapping(\"/" + MyGenUtils.lowcaseFirst(entityName) + "\")\r\n");
        sbb.append("public class " + entityName + "Controller {\r\n\r\n");
        sbb.append("\tprivate static final Logger logger = LoggerFactory.getLogger(" + entityName + "Controller.class);\r\n\r\n");

        sbb.append("\t@Resource\r\n");
        sbb.append("\t" + entityName + "Service " + MyGenUtils.lowcaseFirst(entityName) + "Service;\r\n\r\n");

        //index.do
        sbb.append("\t@RequestMapping(value = \"/index.do\", method = RequestMethod.GET)\r\n");
        sbb.append("\tpublic String index(@RequestParam Integer pageNum, " + entityName + Home.entityNameSuffix + " param, ModelMap model, HttpServletRequest request) {\r\n\r\n");

        sbb.append("\t\t" + "Pager<" + entityName + Home.entityNameSuffix +"> pager = MyRestrictions.getPager().setPageSize(10).setPageNumber(pageNum);\r\n");

        sbb.append("\t\t" + MyGenUtils.lowcaseFirst(entityName) + "Service.selectByGaze(param, pager);\r\n\r\n");

        sbb.append("\t\t" + "model.put(\"result\", pager);\r\n\r\n");

        sbb.append("\t\t" + "return \"" + MyGenUtils.lowcaseFirst(entityName) + "/index\";\r\n");

        sbb.append("\t}\r\n\r\n");


        //tosave.do
        sbb.append("\t@RequestMapping(value = \"/toSave.do\", method = RequestMethod.GET)\r\n");
        sbb.append("\tpublic String toSave(ModelMap model, HttpServletRequest request) {\r\n\r\n");

        sbb.append("\t\treturn \"" + MyGenUtils.lowcaseFirst(entityName) + "/add\";\r\n");
        sbb.append("\t}\r\n\r\n");

        //save.do
        sbb.append("\t@RequestMapping(value = \"/save.do\", method = RequestMethod.POST)\r\n");
        sbb.append("\tpublic String save(" + entityName + Home.entityNameSuffix + " param, ModelMap model, HttpServletRequest request) {\r\n\r\n");

        sbb.append("\t\ttry {\r\n");
        sbb.append("\t\t\tif(param != null) " + MyGenUtils.lowcaseFirst(entityName) + "Service.save(param);\r\n");
        sbb.append("\t\t\tmodel.put(\"result\", param);\r\n");
        sbb.append("\t\t\treturn \"" + MyGenUtils.lowcaseFirst(entityName) + "/success\";\r\n");
        sbb.append("\t\t} catch (Exception e) {\r\n");
        sbb.append("\t\t\te.printStackTrace();\r\n");
        sbb.append("\t\t\tlogger.error(e.getMessage());\r\n");
        sbb.append("\t\t\treturn \"" + MyGenUtils.lowcaseFirst(entityName) +"/fail\";\r\n");
        sbb.append("\t\t}\r\n");
        sbb.append("\t}\r\n\r\n");

        //toupdate.do
        sbb.append("\t@RequestMapping(value = \"/toUpdate.do\", method = RequestMethod.GET)\r\n");
        sbb.append("\tpublic String toUpdate(@RequestParam Integer id, ModelMap model, HttpServletRequest request) {\r\n\r\n");

        sbb.append("\t\t" + entityName + " param = " + MyGenUtils.lowcaseFirst(entityName) + "Service.selectById(id);\r\n\r\n");

        sbb.append("\t\tmodel.put(\"result\", param);\r\n\r\n");

        sbb.append("\t\treturn \"" + MyGenUtils.lowcaseFirst(entityName) + "/update\";\r\n");
        sbb.append("\t}\r\n\r\n");

        //update.do
        sbb.append("\t@RequestMapping(value = \"/update.do\", method = RequestMethod.POST)\r\n");
        sbb.append("\tpublic String update(" + entityName + Home.entityNameSuffix + " param, ModelMap model, HttpServletRequest request) {\r\n\r\n");

        sbb.append("\t\ttry {\r\n");
        sbb.append("\t\t\tif(param != null) " + MyGenUtils.lowcaseFirst(entityName) + "Service.updateSelective(param);\r\n");
        sbb.append("\t\t\tmodel.put(\"result\", param);\r\n");
        sbb.append("\t\t\treturn \"" + MyGenUtils.lowcaseFirst(entityName) + "/success\";\r\n");
        sbb.append("\t\t} catch (Exception e) {\r\n");
        sbb.append("\t\t\te.printStackTrace();\r\n");
        sbb.append("\t\t\tlogger.error(e.getMessage());\r\n");
        sbb.append("\t\t\treturn \"" + MyGenUtils.lowcaseFirst(entityName) +"/fail\";\r\n");
        sbb.append("\t\t}\r\n");
        sbb.append("\t}\r\n\r\n");

        //delete.do
        sbb.append("\t@RequestMapping(value = \"/delete.do\", method = RequestMethod.GET)\r\n");
        sbb.append("\tpublic String delete(@RequestParam Integer id, ModelMap model, HttpServletRequest request) {\r\n\r\n");

        sbb.append("\t\tint param = " + MyGenUtils.lowcaseFirst(entityName) + "Service.deleteById(id);\r\n\r\n");

        sbb.append("\t\tmodel.put(\"result\", param);\r\n");
        sbb.append("\t\treturn \"" + MyGenUtils.lowcaseFirst(entityName) + "/success\";\r\n");
        sbb.append("\t}\r\n\r\n");

        MyGenUtils.processAllRemains(markServiceList, sbb, tag, "java");

        sbb.append("}");

        return sbb.toString();
    }
}

