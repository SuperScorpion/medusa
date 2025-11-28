package ${serviceImplPath};

import ${entityPath}.${entityName}${entityNameSuffix};
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import ${mapperPath}.${entityName}Mapper;
import org.springframework.stereotype.Service;
import ${servicePath}.${entityName}Service;

/**
* Created by ${author} on ${now_time}
*/
@Service
public class ${entityName}ServiceImpl extends BaseServiceImpl<${entityName}${entityNameSuffix}> implements ${entityName}Service {

    @Resource
    private ${entityName}Mapper ${lowcaseFirstEntityName}Mapper;

    @Resource
    private HttpServletRequest request;
}