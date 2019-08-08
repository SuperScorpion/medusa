package com.jy.medusa.gaze.provider;

import com.jy.medusa.gaze.stuff.MyHelper;
import com.jy.medusa.gaze.stuff.exception.MedusaException;
import org.apache.ibatis.binding.MapperMethod;

import java.util.Map;

/**
 * BaseInsertProvider实现类，基础方法实现类
 *
 * Author neo
 */
public class BaseInsertProvider {

    /**
     * 非选择性插入
     * @param m 参数
     * @return 返回值类型
     */
    public String insert(Map<String, Object> m) {
        return MyHelper.getSqlGenerator(m).sqlOfInsert();//modify by neo on 2016/11/12 m.get("pobj")
    }

    /**
     * 选择性插入
     * @param m 参数
     * @return 返回值类型
     */
    public String insertSelective(Map<String, Object> m) {
        return MyHelper.getSqlGenerator(m).sqlOfInsertSelective(m.get("pobj"));//modify by neo on 2016/11/12 m.get("pobj")
    }

    /**
     * 批量插入
     * @param m 参数
     * @return 返回值类型
     */
    public String insertBatch(Map<String, Object> m) {

        if(m.get("pobj") instanceof MapperMethod.ParamMap) {
            return MyHelper.getSqlGenerator(m).sqlOfInsertBatch(
                    ((MapperMethod.ParamMap) m.get("pobj")).get("param1"),
                    ((MapperMethod.ParamMap) m.get("pobj")).get("param2"));
        } else {
            throw new MedusaException("Medusa: insertBatch MapperMethod.ParamMap Exception");
        }
    }

    /**
     * 批量插入mycat
     * @param m 参数
     * @return 返回值类型
     */
    public String insertBatchOfMyCat(Map<String, Object> m) {

        if(m.get("pobj") instanceof MapperMethod.ParamMap) {
            return MyHelper.getSqlGenerator(m).sqlOfInsertBatchForMyCat(
                    ((MapperMethod.ParamMap) m.get("pobj")).get("param1"),
                    ((MapperMethod.ParamMap) m.get("pobj")).get("param2"),
                    ((MapperMethod.ParamMap) m.get("pobj")).get("param3"));
        } else {
            throw new MedusaException("Medusa: insertBatchOfMyCat MapperMethod.ParamMap Exception");
        }
    }
}
