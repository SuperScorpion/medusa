package com.jy.medusa.gaze.provider;

import com.jy.medusa.gaze.stuff.MedusaSqlHelper;
import org.apache.ibatis.binding.MapperMethod;

import java.util.Map;

/**
 * BaseSelectProvider实现类，基础方法实现类
 * Author neo
 */
public class BaseSelectProvider {

    /**
     * 可根据ids条件查询出记录
     * @param m 参数
     * @return 返回值类型
     */
    public String selectByPrimaryKeyBatch(Map<String, Object> m) {

//        if(m.get("pobj") instanceof MapperMethod.ParamMap) {
            return MedusaSqlHelper.getSqlGenerator(m).sqlOfFindBatchOfIds(
                    ((MapperMethod.ParamMap) m).get("param1"),
                    (Object[]) ((MapperMethod.ParamMap) m).get("param2"));
//        } else {
//            throw new MedusaException("Medusa: selectByIds MapperMethod.ParamMap Exception");
//        }
    }

    /**
     * 可根据条件查询出一个记录
     * @param m 参数
     * @return 返回值类型
     */
    public String selectOne(Map<String, Object> m) {

//        if(m.get("pobj") instanceof MapperMethod.ParamMap) {
            return MedusaSqlHelper.getSqlGenerator(m).sqlOfFindOne(
                    ((MapperMethod.ParamMap) m).get("param1"),
                    (Object[]) ((MapperMethod.ParamMap) m).get("param2"));
//        } else {
//            throw new MedusaException("Medusa: selectOne MapperMethod.ParamMap Exception");
//        }
    }


    /**
     * 查询
     * @param m 参数
     * @return 返回值类型
     */
    public String select(Map<String, Object> m) {

//        if(m.get("pobj") instanceof MapperMethod.ParamMap) {
            return MedusaSqlHelper.getSqlGenerator(m).sqlOfFindListBy(
                    ((MapperMethod.ParamMap) m).get("param1"),
                    (Object[]) ((MapperMethod.ParamMap) m).get("param2"));
//        } else {
//            throw new MedusaException("Medusa: select MapperMethod.ParamMap Exception");
//        }
    }


    /**
     * 根据主键进行查询
     * @param m 参数
     * @return 返回值类型
     */
    public String selectByPrimaryKey(Map<String, Object> m) {

//        if(m.get("pobj") instanceof MapperMethod.ParamMap) {
            return MedusaSqlHelper.getSqlGenerator(m).sqlOfFindOneById(
                    ((MapperMethod.ParamMap) m).get("param1"),
                    (Object[]) ((MapperMethod.ParamMap) m).get("param2"));
//        } else {
//            throw new MedusaException("Medusa: selectByPrimaryKey MapperMethod.ParamMap Exception");
//        }
    }



    /**
     * 查询全部结果
     * @param m 参数
     * @return 返回值类型
     * 新老版本产生的 bug fixed (DefaultSqlSession.StrictMap - MapperMethod.ParamMap) 20210113
     */
    public String selectAll(Map<String, Object> m) {

            return MedusaSqlHelper.getSqlGenerator(m).sqlOfFindAll(
                    (Object[]) ((MapperMethod.ParamMap) m).get("array"));
    }



    /**
     * 查询总数
     * @param m 参数
     * @return 返回值类型
     * 新老版本产生的 bug fixed (DefaultSqlSession.StrictMap - MapperMethod.ParamMap) 20210113
     */
    public String selectCount(Map<String, Object> m) {

            return MedusaSqlHelper.getSqlGenerator(m).sqlOfFindAllCount(
                    (Object[]) ((MapperMethod.ParamMap) m).get("array"));
    }



    /**
     * 根据多条件查询数据
     * @param m 参数
     * @return 返回值类型
     * 新老版本产生的 bug fixed (DefaultSqlSession.StrictMap - MapperMethod.ParamMap) 20210113
     */
    public String selectMedusaGaze(Map<String, Object> m) {

            return MedusaSqlHelper.getSqlGenerator(m).sqlOfFindMedusaGaze(
                    (Object[]) ((MapperMethod.ParamMap) m).get("array"));
    }
}
