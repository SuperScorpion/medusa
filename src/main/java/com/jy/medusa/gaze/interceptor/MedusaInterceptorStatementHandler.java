package com.jy.medusa.gaze.interceptor;

/**
 * Created by neo on 16/9/15.
 */

import com.jy.medusa.gaze.stuff.MedusaSqlHelper;
import com.jy.medusa.gaze.utils.MedusaReflectionUtils;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.plugin.Invocation;

import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

abstract class MedusaInterceptorStatementHandler extends MedusaInterceptorBaseHandler {

    /**
     * 处理拦截器 StatementHandler 的逻辑
     * @param invocation
     * @return
     * @throws SQLException
     * @throws ParseException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    protected Object processStatementHandler(Invocation invocation) throws SQLException, ParseException, InvocationTargetException, IllegalAccessException {

        Object result;

        result = invocationProceed(invocation);//先执行再处理的 不然处理的是上次的

        processBatchInsertPrimaryKeyWriteBack(invocation);

        return result;
    }

    /**
     * 批量插入时的生成的主键通过反射回写入实体的相关处理
     * 通过processExecutor里的invocationProceed(invocation)嵌套进入
     * For mybatis interceptor of StatementHandler
     * @param invocation
     * @throws SQLException
     * @throws ParseException
     */
    private void processBatchInsertPrimaryKeyWriteBack(Invocation invocation) throws SQLException, ParseException {

        StatementHandler sh = (StatementHandler) invocation.getTarget();

        Object parObj = sh.getBoundSql().getParameterObject();

        //获取不到MappedStatement 所以使用下判断
        if (parObj instanceof Map && ((Map) parObj).containsKey("msid")//过滤掉非medusa方法
                && ((Map) parObj).containsKey("param1")//过滤掉非batch方法
                && sh.getBoundSql().getSql().startsWith("INSERT INTO")) {//过滤掉delete update之类的 非insert方法 modify by neo on 2017.12.13

            List<Object> paramList = (List) ((Map) parObj).get("param1");

            //过滤medusa单个insert delete update 的方法
            if (paramList != null && !paramList.isEmpty()) {

                Statement st = (Statement) invocation.getArgs()[0];

                ResultSet rs = st.getGeneratedKeys();

                for (Object ot : paramList) {

                    if (!rs.next()) break;

                    //modify by neo on 2019.08.07 for mycat
                    int currentIdValue = sh.getBoundSql().getSql().toLowerCase().contains("next value for") ? rs.getInt(1) - 1 : rs.getInt(1);//注入属性id值 mycat 方式取到的都是+1 所以这里-1

                    MedusaReflectionUtils.invokeSetterMethod(ot, MedusaSqlHelper.getSqlGenerator((Map) parObj).getPkPropertyName(), currentIdValue);
                }
            }
        }
    }
}