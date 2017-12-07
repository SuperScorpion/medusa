<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="${mapperPath}.${entityName}Mapper">
    <resultMap id="BaseResultMap" type="${entityPath}.${entityName}${entityNameSuffix}">
<#if resultMapStrList??>
    <#list resultMapStrList as cl>
        ${cl}
    </#list>
</#if>
    </resultMap>

<#if xaList??>
    <#list xaList as xa>
    <select id = "find${xa.upperName}ById" resultType="${entityPath}.${xa.upperName}${entityNameSuffix}">
        SELECT ${xa.paramSql} FROM ${xa.lowwerName} WHERE id = ${r'#{id}'} limit 0,1
    </select>
    </#list>
</#if>


    <!--<sql id="Base_Column_List" >
        ${base_column_list}
    </sql>-->

    <!-- Created by ${author} on ${now_time} -->

</mapper>
