package ${packagePath};

import com.alibaba.fastjson.JSONObject;
import ${medusa_pager_path};
import ${medusa_myrestriction_path};
import ${servicePath}.${entityName}Service;
import ${entityPath}.${entityName}${entityNameSuffix};
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* Created by ${author} on ${now_time}
*/
@Controller
@RequestMapping("/${lowcaseFirstEntityName}")
public class ${entityName}Controller {

    private static final Logger logger = LoggerFactory.getLogger(${entityName}Controller.class);

    @Resource
    ${entityName}Service ${lowcaseFirstEntityName}Service;

    @RequestMapping(value = "/index.json", method = RequestMethod.GET)
    @ResponseBody
    public JSONObject index(@RequestParam Integer pageNum, ${entityName} param, HttpServletRequest request) {

        JSONObject json = new JSONObject();

        try {
            Pager<${entityName}> pager = MyRestrictions.getPager().setPageSize(10).setPageNumber(pageNum);
            ${lowcaseFirstEntityName}Service.selectByGaze(pager);

            ${lowcaseFirstEntityName}Service.resultSuccess(pager, "ok");
        } catch (Exception e) {
            ${lowcaseFirstEntityName}Service.resultError(null, "服务器异常：" + e.getMessage());
            e.printStackTrace();
            logger.error(e.getMessage());
        }
        return json;
    }

    @RequestMapping(value = "/save.json", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject save(${entityName} param, HttpServletRequest request) {

        JSONObject json = new JSONObject();

        try {
            if(param != null) ${lowcaseFirstEntityName}Service.save(param);

            ${lowcaseFirstEntityName}Service.resultSuccess(param, "ok");
        } catch (Exception e) {
            ${lowcaseFirstEntityName}Service.resultError(null, "服务器异常：" + e.getMessage());
            e.printStackTrace();
            logger.error(e.getMessage());
        }
        return json;
    }

    @RequestMapping(value = "/update.json", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject update(${entityName} param, HttpServletRequest request) {

        JSONObject json = new JSONObject();

        try {
            if(param != null) ${lowcaseFirstEntityName}Service.updateSelective(param);

            ${lowcaseFirstEntityName}Service.resultSuccess(param, "ok");
        } catch (Exception e) {
            ${lowcaseFirstEntityName}Service.resultError(null, "服务器异常：" + e.getMessage());
            e.printStackTrace();
            logger.error(e.getMessage());
        }
        return json;
    }

    @RequestMapping(value = "/delete.json", method = RequestMethod.GET)
    @ResponseBody
    public JSONObject delete(@RequestParam Integer id, HttpServletRequest request) {

        JSONObject json = new JSONObject();

        try {
            int param = ${lowcaseFirstEntityName}Service.deleteById(id);

            ${lowcaseFirstEntityName}Service.resultSuccess(param, "ok");
        } catch (Exception e) {
            ${lowcaseFirstEntityName}Service.resultError(null, "服务器异常：" + e.getMessage());
            e.printStackTrace();
            logger.error(e.getMessage());
        }
        return json;
    }

}