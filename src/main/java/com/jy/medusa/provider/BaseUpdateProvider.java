package com.jy.medusa.provider;

import com.jy.medusa.stuff.MyHelper;
import com.jy.medusa.stuff.exception.MedusaException;
import org.apache.ibatis.binding.MapperMethod;

import java.util.Map;

/**
 * BaseUpdateProvider实现类，基础方法实现类
 *
 * @author neo
 */
public class BaseUpdateProvider {

    /**
     * 通过主键更新全部字段
     */
    public String updateByPrimaryKey(Map<String, Object> m) throws MedusaException {

        if(m.get("pobj") instanceof MapperMethod.ParamMap)
            return MyHelper.getSqlGenerator(m).sql_modify_null(
                    ((MapperMethod.ParamMap) m.get("pobj")).get("param1"),
                    ((MapperMethod.ParamMap) m.get("pobj")).get("param2"));

        throw new RuntimeException("Medusa: updateByPrimaryKey MapperMethod.ParamMap Exception");
    }

    /**
     * 通过主键更新不为null的字段
     *
     * @return
     */
    public String updateByPrimaryKeySelective(Map<String, Object> m) throws MedusaException {
        return MyHelper.getSqlGenerator(m).sql_modify(m.get("pobj"));
    }

    /**
     * 通过主键更新批量的
     *
     * @return
     */
    public String updateByPrimaryKeyBatch(Map<String, Object> m) throws MedusaException {

        if(m.get("pobj") instanceof MapperMethod.ParamMap)
            return MyHelper.getSqlGenerator(m).sql_modifyOfBatch(
                    ((MapperMethod.ParamMap) m.get("pobj")).get("param1"),
                    ((MapperMethod.ParamMap) m.get("pobj")).get("param2"));

        throw new RuntimeException("Medusa: updateByPrimaryKeyBatch MapperMethod.ParamMap Exception");
    }
}
