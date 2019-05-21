package com.jy.medusa.provider;

import com.jy.medusa.stuff.MyHelper;
import com.jy.medusa.stuff.exception.MedusaException;
import org.apache.ibatis.binding.MapperMethod;

import java.util.Map;

/**
 * BaseInsertProvider实现类，基础方法实现类
 *
 * @author neo
 */
public class BaseInsertProvider {

    public String insert(Map<String, Object> m) {
        return MyHelper.getSqlGenerator(m).sql2Create();//modify by neo on 2016/11/12 m.get("pobj")
    }

    public String insertSelective(Map<String, Object> m) {
        return MyHelper.getSqlGenerator(m).sql2CreateSelective(m.get("pobj"));//modify by neo on 2016/11/12 m.get("pobj")
    }

    public String insertBatch(Map<String, Object> m) {

        if(m.get("pobj") instanceof MapperMethod.ParamMap) {
            return MyHelper.getSqlGenerator(m).sql2InsertOfBatch(
                    ((MapperMethod.ParamMap) m.get("pobj")).get("param1"),
                    ((MapperMethod.ParamMap) m.get("pobj")).get("param2"));
        } else {
            throw new MedusaException("Medusa: insertBatch MapperMethod.ParamMap Exception");
        }
    }
}
