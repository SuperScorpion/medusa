package com.jy.medusa.gaze.provider;

import com.jy.medusa.gaze.stuff.MedusaSqlHelper;
import com.jy.medusa.gaze.stuff.exception.MedusaException;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.session.defaults.DefaultSqlSession;

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
     */
    public String selectAll(Map<String, Object> m) {

//        if(m.get("pobj") instanceof DefaultSqlSession.StrictMap) {
            return MedusaSqlHelper.getSqlGenerator(m).sqlOfFindAll(
                    (Object[]) ((DefaultSqlSession.StrictMap) m).get("array"));
//        } else {
//            throw new MedusaException("Medusa: selectAll DefaultSqlSession.StrictMap Exception");
//        }
    }



    /**
     * 查询总数
     * @param m 参数
     * @return 返回值类型
     */
    public String selectCount(Map<String, Object> m) {

//        if(m.get("pobj") instanceof DefaultSqlSession.StrictMap) {
            return MedusaSqlHelper.getSqlGenerator(m).sqlOfFindAllCount(
                    (Object[]) ((DefaultSqlSession.StrictMap) m).get("array"));
//        } else {
//            throw new MedusaException("Medusa: selectCount DefaultSqlSession.StrictMap Exception");
//        }
    }



    /**
     * 根据多条件查询数据
     * @param m 参数
     * @return 返回值类型
     */
    public String selectMedusaGaze(Map<String, Object> m) {

//        if(m.get("pobj") instanceof DefaultSqlSession.StrictMap) {
            return MedusaSqlHelper.getSqlGenerator(m).sqlOfFindMedusaGaze(
                    (Object[]) ((DefaultSqlSession.StrictMap) m).get("array"));
//        } else {
//            throw new MedusaException("Medusa: selectMedusaGaze DefaultSqlSession.StrictMap Exception");
//        }
    }
}
