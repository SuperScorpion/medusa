
package com.jy.medusa.provider;

import com.jy.medusa.stuff.MyHelper;

import java.util.Map;

/**
 * BaseInsertProvider实现类，基础方法实现类
 *
 * @author neo
 */
public class BaseInsertProvider {

    public String insertSelective(Map<String, Object> m) {
        return MyHelper.getSqlGenerator(m).sql_create();//modify by neo on 2016/11/12 m.get("pobj")
    }
}
