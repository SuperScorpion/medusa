package ${entityPath};

import com.jy.medusa.gaze.stuff.annotation.Column;
import com.jy.medusa.gaze.stuff.annotation.Id;
import com.jy.medusa.gaze.stuff.annotation.Table;

<#if lazyLoad?? && lazyLoad==true>
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
</#if>
<#if entitySerializable?? && entitySerializable==true>
import java.io.Serializable;
</#if>
<#if isMyDateUtils?? && isMyDateUtils==true>
import com.jy.medusa.gaze.utils.MyDateUtils;
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
* Created by ${author} on ${now_time}
*/
@Table(name = "${tableName}")
<#if lazyLoad?? && lazyLoad==true>
@JsonIgnoreProperties(value={"handler"})
</#if>
public class ${upcaseFirstTableName}${entityNameSuffix} {

<#if columnDtos??>
<#list columnDtos as cl>
<#if cl.primarykeyFlag?? && cl.primarykeyFlag == true>
	@Id
</#if>
	@Column(name = "${cl.column}")
	private ${cl.javaType} ${cl.lowwerName};

</#list>
<#list columnDtos as cl>
    <#if cl.notOnlyColumnFlag?? && cl.notOnlyColumnFlag == true>
    ${cl.associRemark}
    private ${cl.associUpperName} ${cl.associLowwerName};
    </#if>
</#list>


<#list columnDtos as cl>
	public ${cl.javaType} get${cl.upperName}() {
		return ${cl.lowwerName};
	}

	public void set${cl.upperName}(${cl.javaType} ${cl.lowwerName}) {
		this.${cl.lowwerName} = ${cl.lowwerName};
	}

</#list>
</#if>
}