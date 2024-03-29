package com.jy.medusa.generator.gen;

import com.jy.medusa.gaze.utils.MedusaDateUtils;
import com.jy.medusa.gaze.utils.MedusaCommonUtils;
import com.jy.medusa.gaze.utils.SystemConfigs;
import com.jy.medusa.generator.Home;
import com.jy.medusa.generator.MedusaGenUtils;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * Created by SuperScorpion on 16/7/19.
 * @deprecated
 */
public class GenControllerMortal {

    private String controlPath;
    private String entityPath;
    private String servicePath;

    private String entityName;

    private List<String> markServiceList;
    private String tag;//标记 mark


    public GenControllerMortal(String tableName, String controlPath, String entityPath, String servicePath) {
        this.entityPath = entityPath;
        this.servicePath = servicePath;
        this.entityName = MedusaGenUtils.upcaseFirst(tableName);
        this.controlPath = controlPath;
        this.tag = Home.tag;
        this.markServiceList = MedusaGenUtils.genTagStrList(entityName + "Controller.java", controlPath, tag, "java");
    }

    public Boolean process() {

        try {
            String path = Home.proJavaPath + controlPath.replaceAll("\\.", "/");
            File file = new File(path);
            if(!file.exists()) {
                file.mkdirs();
            }
            String resPath = path + "/" + entityName + "Controller.java";

            //如果目标文件已存在 则跳过 add by SuperScorpion on 20230221
            File resPathFile = new File(resPath);
            if(resPathFile.exists()) {
                System.out.println("Medusa: " + entityName + "Controller.java" + " 文件已存在 已跳过生成...");
                return false;
            }
            MedusaCommonUtils.writeString2File(resPathFile, home(), "UTF-8");

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * controller
     * @return 返回值类型
     */
    private String home() {

        StringBuilder sbb = new StringBuilder();

        sbb.append("package " + controlPath + ";\r\n\r\n");

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
        sbb.append(" * Created by " + Home.author + " on " + MedusaDateUtils.convertDateToStr(new Date(), null) + "\r\n");
        sbb.append(" */\r\n");

        sbb.append("@Controller\r\n");
        sbb.append("@RequestMapping(\"/" + MedusaGenUtils.lowcaseFirst(entityName) + "\")\r\n");
        sbb.append("public class " + entityName + "Controller {\r\n\r\n");
        sbb.append("\tprivate static final Logger logger = LoggerFactory.getLogger(" + entityName + "Controller.class);\r\n\r\n");

        sbb.append("\t@Resource\r\n");
        sbb.append("\t" + entityName + "Service " + MedusaGenUtils.lowcaseFirst(entityName) + "Service;\r\n\r\n");

        //index.do
        sbb.append("\t@RequestMapping(value = \"/index.do\", method = RequestMethod.GET)\r\n");
        sbb.append("\tpublic String index(@RequestParam Integer pageNum, " + entityName + Home.entityNameSuffix + " param, ModelMap model, HttpServletRequest request) {\r\n\r\n");

        sbb.append("\t\t" + "Pager<" + entityName + Home.entityNameSuffix +"> pager = Pager.getPager().setPageSize(10).setPageNumber(pageNum);\r\n");

        sbb.append("\t\t" + MedusaGenUtils.lowcaseFirst(entityName) + "Service.selectByGazeMagic(param, pager);\r\n\r\n");

        sbb.append("\t\t" + "model.put(\"result\", pager);\r\n\r\n");

        sbb.append("\t\t" + "return \"" + MedusaGenUtils.lowcaseFirst(entityName) + "/index\";\r\n");

        sbb.append("\t}\r\n\r\n");


        //tosave.do
        sbb.append("\t@RequestMapping(value = \"/toSave.do\", method = RequestMethod.GET)\r\n");
        sbb.append("\tpublic String toSave(ModelMap model, HttpServletRequest request) {\r\n\r\n");

        sbb.append("\t\treturn \"" + MedusaGenUtils.lowcaseFirst(entityName) + "/add\";\r\n");
        sbb.append("\t}\r\n\r\n");

        //save.do
        sbb.append("\t@RequestMapping(value = \"/save.do\", method = RequestMethod.POST)\r\n");
        sbb.append("\tpublic String save(" + entityName + Home.entityNameSuffix + " param, ModelMap model, HttpServletRequest request) {\r\n\r\n");

        sbb.append("\t\ttry {\r\n");
        sbb.append("\t\t\tif(param != null) " + MedusaGenUtils.lowcaseFirst(entityName) + "Service.insertSelective(param);\r\n");
        sbb.append("\t\t\tmodel.put(\"result\", param);\r\n");
        sbb.append("\t\t\treturn \"" + MedusaGenUtils.lowcaseFirst(entityName) + "/success\";\r\n");
        sbb.append("\t\t} catch (Exception e) {\r\n");
        sbb.append("\t\t\te.printStackTrace();\r\n");
        sbb.append("\t\t\tlogger.error(e.getMessage());\r\n");
        sbb.append("\t\t\treturn \"" + MedusaGenUtils.lowcaseFirst(entityName) +"/fail\";\r\n");
        sbb.append("\t\t}\r\n");
        sbb.append("\t}\r\n\r\n");

        //toupdate.do
        sbb.append("\t@RequestMapping(value = \"/toUpdate.do\", method = RequestMethod.GET)\r\n");
        sbb.append("\tpublic String toUpdate(@RequestParam Integer id, ModelMap model, HttpServletRequest request) {\r\n\r\n");

        sbb.append("\t\t" + entityName + " param = " + MedusaGenUtils.lowcaseFirst(entityName) + "Service.selectById(id);\r\n\r\n");

        sbb.append("\t\tmodel.put(\"result\", param);\r\n\r\n");

        sbb.append("\t\treturn \"" + MedusaGenUtils.lowcaseFirst(entityName) + "/update\";\r\n");
        sbb.append("\t}\r\n\r\n");

        //update.do
        sbb.append("\t@RequestMapping(value = \"/update.do\", method = RequestMethod.POST)\r\n");
        sbb.append("\tpublic String update(" + entityName + Home.entityNameSuffix + " param, ModelMap model, HttpServletRequest request) {\r\n\r\n");

        sbb.append("\t\ttry {\r\n");
        sbb.append("\t\t\tif(param != null) " + MedusaGenUtils.lowcaseFirst(entityName) + "Service.updateSelective(param);\r\n");
        sbb.append("\t\t\tmodel.put(\"result\", param);\r\n");
        sbb.append("\t\t\treturn \"" + MedusaGenUtils.lowcaseFirst(entityName) + "/success\";\r\n");
        sbb.append("\t\t} catch (Exception e) {\r\n");
        sbb.append("\t\t\te.printStackTrace();\r\n");
        sbb.append("\t\t\tlogger.error(e.getMessage());\r\n");
        sbb.append("\t\t\treturn \"" + MedusaGenUtils.lowcaseFirst(entityName) +"/fail\";\r\n");
        sbb.append("\t\t}\r\n");
        sbb.append("\t}\r\n\r\n");

        //delete.do
        sbb.append("\t@RequestMapping(value = \"/delete.do\", method = RequestMethod.GET)\r\n");
        sbb.append("\tpublic String delete(@RequestParam Integer id, ModelMap model, HttpServletRequest request) {\r\n\r\n");

        sbb.append("\t\tint param = " + MedusaGenUtils.lowcaseFirst(entityName) + "Service.deleteById(id);\r\n\r\n");

        sbb.append("\t\tmodel.put(\"result\", param);\r\n");
        sbb.append("\t\treturn \"" + MedusaGenUtils.lowcaseFirst(entityName) + "/success\";\r\n");
        sbb.append("\t}\r\n\r\n");

        MedusaGenUtils.processAllRemains(markServiceList, sbb, tag, "java");

        sbb.append("}");

        return sbb.toString();
    }
}

