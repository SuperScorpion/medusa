package ${entityPath?default("")};

import com.jy.medusa.gaze.stuff.annotation.Column;
import com.jy.medusa.gaze.stuff.annotation.Id;
import com.jy.medusa.gaze.stuff.annotation.Table;

<#if lazyLoad?? && lazyLoad==true>
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
</#if>
<#if entitySerializable?? && entitySerializable==true>
import java.io.Serializable;
</#if>
<#if isMedusaDateUtils?? && isMedusaDateUtils==true>
import com.jy.medusa.gaze.utils.MedusaDateUtils;
</#if>
<#if isDate?? && isDate==true>
import java.util.Date;
</#if>
<#if isSql?? && isSql==true>
import java.sql.*;
</#if>
<#if isMoney?? && isMoney==true>
import java.math.BigDecimal;
</#if>
<#if useValid?? && useValid==true>
import SystemConfigs.VALID_PATTERN_PATH;
import SystemConfigs.VALID_LENGTH_PATH;
import SystemConfigs.VALID_VALIDATOR_PATH;
</#if>

/**
* Created by ${author?default("")} on ${now_time?default("")}
* ${tableComment?default("")}
*/
@Table(name = "${tableName?default("")}")
<#if lazyLoad?? && lazyLoad==true>
@JsonIgnoreProperties(value={"handler"})
</#if>
public class ${upcaseFirstTableName?default("")}${entityNameSuffix?default("")} {

<#if columnDtos??>
<#list columnDtos as cl>
    <#if cl.comment??>
    /**
    ${cl.comment}
    */
    </#if>
    <#if cl.primarykeyFlag?? && cl.primarykeyFlag == true>
    @Id
    </#if>
	@Column(name = "${cl.column}")
    private ${cl.javaType?default("")} ${cl.lowwerName?default("")};

</#list>
<#list columnDtos as cl>
    <#if cl.notOnlyColumnFlag?? && cl.notOnlyColumnFlag == true>
    ${cl.associRemark}
    private ${cl.associUpperName?default("")} ${cl.associLowwerName?default("")};
    </#if>
</#list>


<#list columnDtos as cl>
	public ${cl.javaType?default("")} get${cl.upperName?default("")}() {
		return ${cl.lowwerName?default("")};
	}

	public void set${cl.upperName?default("")}(${cl.javaType?default("")} ${cl.lowwerName?default("")}) {
		this.${cl.lowwerName?default("")} = ${cl.lowwerName?default("")};
	}

</#list>
</#if>
}
