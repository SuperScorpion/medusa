
package com.jy.medusa.provider;

import com.jy.medusa.stuff.MyHelper;
import com.jy.medusa.stuff.exception.MedusaException;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.session.defaults.DefaultSqlSession;

import java.util.List;
import java.util.Map;

/**
 * BaseInsertProvider实现类，基础方法实现类
 *
 * @author neo
 */
public class BaseInsertProvider {

    public String insert(Map<String, Object> m) {
        return MyHelper.getSqlGenerator(m).sql_create();//modify by neo on 2016/11/12 m.get("pobj")
    }

    public String insertSelective(Map<String, Object> m) {
        return MyHelper.getSqlGenerator(m).sql_create_selective(m.get("pobj"));//modify by neo on 2016/11/12 m.get("pobj")
    }

    public String insertBatch(Map<String, Object> m) throws MedusaException {

        if(m.get("pobj") instanceof MapperMethod.ParamMap)
            return MyHelper.getSqlGenerator(m).sql_insertOfBatch(
                    ((MapperMethod.ParamMap) m.get("pobj")).get("param1"),
                    ((MapperMethod.ParamMap) m.get("pobj")).get("param2"));

        throw new RuntimeException("Medusa: insertBatch MapperMethod.ParamMap Exception");
    }
}
