package ${controlPath};

import com.alibaba.fastjson.JSONObject;
import ${medusa_pager_path};
import ${medusa_myrestriction_path};
import ${servicePath}.${entityName}Service;
import ${entityPath}.${entityName}${entityNameSuffix};
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;

/**
* Created by ${author} on ${now_time}
*/
@RestController
@RequestMapping("/${lowcaseFirstEntityName}")
public class ${entityName}Controller {

    private static final Logger logger = LoggerFactory.getLogger(${entityName}Controller.class);

    @Resource
    private ${entityName}Service ${lowcaseFirstEntityName}Service;

    @GetMapping(value = "/index")
    public JSONObject index(@RequestParam Integer pageNum, ${entityName}${entityNameSuffix} param) {

        JSONObject json = new JSONObject();

        try {
            Pager<${entityName}${entityNameSuffix}> pager = Pager.getPager().setPageSize(10).setPageNumber(pageNum);
            ${lowcaseFirstEntityName}Service.selectMedusaCombo(pager);

            ${lowcaseFirstEntityName}Service.resultSuccess(pager, "ok", json);
        } catch (Exception e) {
            ${lowcaseFirstEntityName}Service.resultError(null, "服务器异常：" + e.getMessage(), json);
            e.printStackTrace();
            logger.error(e.getMessage());
        }
        return json;
    }

    @PostMapping(value = "/save")
    public JSONObject save(${entityName}${entityNameSuffix} param) {

        JSONObject json = new JSONObject();

        try {
            if(param != null) ${lowcaseFirstEntityName}Service.insertSelective(param);

            ${lowcaseFirstEntityName}Service.resultSuccess(param, "ok", json);
        } catch (Exception e) {
            ${lowcaseFirstEntityName}Service.resultError(null, "服务器异常：" + e.getMessage(), json);
            e.printStackTrace();
            logger.error(e.getMessage());
        }
        return json;
    }

    @PostMapping(value = "/update")
    public JSONObject update(${entityName}${entityNameSuffix} param) {

        JSONObject json = new JSONObject();

        try {
            if(param != null) ${lowcaseFirstEntityName}Service.updateSelective(param);

            ${lowcaseFirstEntityName}Service.resultSuccess(param, "ok", json);
        } catch (Exception e) {
            ${lowcaseFirstEntityName}Service.resultError(null, "服务器异常：" + e.getMessage(), json);
            e.printStackTrace();
            logger.error(e.getMessage());
        }
        return json;
    }

    @GetMapping(value = "/delete")
    public JSONObject delete(@RequestParam Integer id) {

        JSONObject json = new JSONObject();

        try {
            int param = ${lowcaseFirstEntityName}Service.deleteById(id);

            ${lowcaseFirstEntityName}Service.resultSuccess(param, "ok", json);
        } catch (Exception e) {
            ${lowcaseFirstEntityName}Service.resultError(null, "服务器异常：" + e.getMessage(), json);
            e.printStackTrace();
            logger.error(e.getMessage());
        }
        return json;
    }

}