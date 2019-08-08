package com.jy.medusa.gaze.provider;

import com.jy.medusa.gaze.stuff.MyHelper;
import com.jy.medusa.gaze.stuff.exception.MedusaException;
import org.apache.ibatis.binding.MapperMethod;

import java.util.Map;

/**
 * BaseUpdateProvider实现类，基础方法实现类
 *
 * Author neo
 */
public class BaseUpdateProvider {

    /**
     * 通过主键更新全部字段
     * @param m 参数
     * @return 返回值类型
     */
    public String updateByPrimaryKey(Map<String, Object> m) {

        if(m.get("pobj") instanceof MapperMethod.ParamMap) {
            return MyHelper.getSqlGenerator(m).sqlOfModifyNull(
                    ((MapperMethod.ParamMap) m.get("pobj")).get("param1"),
                    ((MapperMethod.ParamMap) m.get("pobj")).get("param2"));
        } else {
            throw new MedusaException("Medusa: updateByPrimaryKey MapperMethod.ParamMap Exception");
        }
    }

    /**
     * 通过主键更新不为null的字段
     * @param m 参数
     * @return 返回值类型
     */
    public String updateByPrimaryKeySelective(Map<String, Object> m) {
        return MyHelper.getSqlGenerator(m).sqlOfModify(m.get("pobj"));
    }

    /**
     * 通过主键更新批量的
     * @param m 参数
     * @return 返回值类型
     */
    public String updateByPrimaryKeyBatch(Map<String, Object> m) {

        if(m.get("pobj") instanceof MapperMethod.ParamMap) {
            return MyHelper.getSqlGenerator(m).sqlOfModifyOfBatch(
                    ((MapperMethod.ParamMap) m.get("pobj")).get("param1"),
                    ((MapperMethod.ParamMap) m.get("pobj")).get("param2"));
        } else {
            throw new MedusaException("Medusa: updateByPrimaryKeyBatch MapperMethod.ParamMap Exception");
        }
    }
}
