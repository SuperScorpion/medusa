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
public class GenControllerJson {

    private String controlPath;
    private String entityPath;
    private String servicePath;

    private String entityName;

    private List<String> markServiceList;
    private String tag;//标记 mark


    public GenControllerJson(String tableName, String controlPath, String entityPath, String servicePath) {
        this.entityPath = entityPath;
        this.servicePath = servicePath;
        this.entityName = MedusaGenUtils.upcaseFirst(tableName);
        this.controlPath = controlPath;
        this.tag = Home.tag;
        this.markServiceList = MedusaGenUtils.genTagStrList(entityName + "Controller.java", controlPath, tag, "java");
    }

    public void process() {

        try {
            String path = Home.proJavaPath + controlPath.replaceAll("\\.", "/");
            File file = new File(path);
            if(!file.exists()) {
                file.mkdirs();
            }
            String resPath = path + "/" + entityName + "Controller.java";
            MedusaCommonUtils.writeString2File(new File(resPath), home(), "UTF-8");

        } catch (IOException e) {
            e.printStackTrace();
        }
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
        sbb.append("import com.alibaba.fastjson.JSONObject;\r\n");

        sbb.append("import " + SystemConfigs.MEDUSA_PAGER_PATH + ";\r\n");
        sbb.append("import " + SystemConfigs.MEDUSA_MYRESTRICTION_PATH + ";\r\n");

        sbb.append("import org.slf4j.Logger;\r\n");
        sbb.append("import org.slf4j.LoggerFactory;\r\n");
        sbb.append("import org.springframework.stereotype.Controller;\r\n");
//        sbb.append("import org.springframework.ui.ModelMap;\r\n");
        sbb.append("import org.springframework.web.bind.annotation.RequestMapping;\r\n");
        sbb.append("import org.springframework.web.bind.annotation.RequestMethod;\r\n");
        sbb.append("import org.springframework.web.bind.annotation.RequestParam;\r\n");
        sbb.append("import org.springframework.web.bind.annotation.ResponseBody;\r\n");
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

        //index.json
        sbb.append("\t@RequestMapping(value = \"/index.json\", method = RequestMethod.GET)\r\n");
        sbb.append("\t@ResponseBody\r\n");
        sbb.append("\tpublic JSONObject index(@RequestParam Integer pageNum, " + entityName + Home.entityNameSuffix + " param, HttpServletRequest request) {\r\n\r\n");
        sbb.append("\t\tJSONObject json = new JSONObject();\r\n\r\n");

        StringBuilder sb2 = new StringBuilder();
        sb2.append("\t\t\t" + "Pager<" + entityName + Home.entityNameSuffix +"> pager = Pager.getPager().setPageSize(10).setPageNumber(pageNum);\r\n");
        sb2.append("\t\t\t" + MedusaGenUtils.lowcaseFirst(entityName) + "Service.selectByGazeMagic(param, pager);\r\n\r\n");

        genTryCatch(sbb, sb2.toString());
        sbb.append("\t\treturn json;\r\n");
        sbb.append("\t}\r\n\r\n");

        //save.json
        sbb.append("\t@RequestMapping(value = \"/save.json\", method = RequestMethod.POST)\r\n");
        sbb.append("\t@ResponseBody\r\n");
        sbb.append("\tpublic JSONObject save(" + entityName + Home.entityNameSuffix + " param, HttpServletRequest request) {\r\n\r\n");
        sbb.append("\t\tJSONObject json = new JSONObject();\r\n\r\n");

        sb2.delete(0, sb2.length());
        sb2.append("\t\t\tif(param != null) " + MedusaGenUtils.lowcaseFirst(entityName) + "Service.save(param);\r\n\r\n");
        genTryCatch(sbb, sb2.toString());
        sbb.append("\t\treturn json;\r\n");
        sbb.append("\t}\r\n\r\n");

        //update.json
        sbb.append("\t@RequestMapping(value = \"/update.json\", method = RequestMethod.POST)\r\n");
        sbb.append("\t@ResponseBody\r\n");
        sbb.append("\tpublic JSONObject update(" + entityName + Home.entityNameSuffix + " param, HttpServletRequest request) {\r\n\r\n");
        sbb.append("\t\tJSONObject json = new JSONObject();\r\n\r\n");

        sb2.delete(0, sb2.length());
        sb2.append("\t\t\tif(param != null) " + MedusaGenUtils.lowcaseFirst(entityName) + "Service.updateSelective(param);\r\n\r\n");
        genTryCatch(sbb, sb2.toString());
        sbb.append("\t\treturn json;\r\n");
        sbb.append("\t}\r\n\r\n");

        //delete.json
        sbb.append("\t@RequestMapping(value = \"/delete.json\", method = RequestMethod.GET)\r\n");
        sbb.append("\t@ResponseBody\r\n");
        sbb.append("\tpublic JSONObject delete(@RequestParam Integer id, HttpServletRequest request) {\r\n\r\n");
        sbb.append("\t\tJSONObject json = new JSONObject();\r\n\r\n");

        sb2.delete(0, sb2.length());
        sb2.append("\t\t\tint param = " + MedusaGenUtils.lowcaseFirst(entityName) + "Service.deleteById(id);\r\n\r\n");
        genTryCatch(sbb, sb2.toString());
        sbb.append("\t\treturn json;\r\n");
        sbb.append("\t}\r\n\r\n");



        MedusaGenUtils.processAllRemains(markServiceList, sbb, tag, "java");

        sbb.append("}");

        return sbb.toString();
    }

    public void genTryCatch(StringBuilder sb, String sbbb) {
        sb.append("\t\ttry {\r\n");

        sb.append(sbbb);

        if(sbbb.contains("pager"))
            sb.append("\t\t\t" + MedusaGenUtils.lowcaseFirst(entityName) + "Service.resultSuccess(pager, \"ok\", json);\r\n");
//            sb.append("\t\t\tjson.put(\"data\", pager);\r\n");
        else
            sb.append("\t\t\t" + MedusaGenUtils.lowcaseFirst(entityName) + "Service.resultSuccess(param, \"ok\", json);\r\n");
//            sb.append("\t\t\tjson.put(\"data\", param);\r\n");

        /*sb.append("\t\t\tjson.put(\"result\",0);\r\n");
        sb.append("\t\t\tjson.put(\"msg\",\"ok\");\r\n");*/

        sb.append("\t\t} catch (Exception e) {\r\n");
//        sb.append("\t\t\tjson.put(\"result\",1);\r\n");
//        sb.append("\t\t\tjson.put(\"msg\",\"服务器异常：\" + e.getMessage());\r\n");
//        sb.append("\t\t\tjson.put(\"data\", null);\r\n");
        sb.append("\t\t\t" + MedusaGenUtils.lowcaseFirst(entityName) + "Service.resultError(null, \"服务器异常：\" + e.getMessage(), json);\r\n");
        sb.append("\t\t\te.printStackTrace();\r\n");
        sb.append("\t\t\tlogger.error(e.getMessage());\r\n");
        sb.append("\t\t}\r\n");
    }

}
