package com.jy.medusa.gaze.interceptor;

/**
 * Created by neo on 16/9/15.
 */

import com.jy.medusa.gaze.stuff.MyHelper;
import com.jy.medusa.gaze.stuff.Pager;
import com.jy.medusa.gaze.stuff.exception.MedusaException;
import com.jy.medusa.gaze.stuff.param.MyRestrictions;
import com.jy.medusa.gaze.utils.MyReflectionUtils;
import com.jy.medusa.gaze.utils.SystemConfigs;
import org.apache.ibatis.builder.annotation.ProviderSqlSource;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.defaults.DefaultSqlSession;

import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.*;

@Intercepts({
        @Signature(method = "update", type = StatementHandler.class, args = {Statement.class}),
        @Signature(method = "query", type = Executor.class, args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
        @Signature(method = "update", type = Executor.class, args = {MappedStatement.class, Object.class})
})
public class MedusaInterceptor implements Interceptor {

    public Object intercept(Invocation invocation) throws Throwable {

        Object result;

        if (invocation.getTarget() instanceof Executor) {

            result = processExecutor(invocation);

        } else if(invocation.getTarget() instanceof StatementHandler) {//delete insert update 都会进来此拦截(medusa的或非medusa的)

            result = invocation.proceed();//先执行再处理的 不然处理的是上次的

            processBatchInsertPrimaryKeyWriteBack(invocation);

        } else {
            result = invocation.proceed();
        }

        return result;
    }

    private Object processExecutor(Invocation invocation) throws InvocationTargetException, IllegalAccessException, ParseException, SQLException {

        Object result;

        MappedStatement mt = (MappedStatement) invocation.getArgs()[0];

        String medusaMethodName = MyHelper.getLastWord(mt.getId()).trim();

        if (mt.getSqlSource() instanceof ProviderSqlSource && MyHelper.checkMortalMethds(medusaMethodName)) {//Modify by neo on 2019.05.31

            Map<String, Object> p = new HashMap<>(1 << 2);

            p.put("pobj", invocation.getArgs()[1]);

            p.put("msid", MyHelper.removeLastWord(mt.getId()));

            invocation.getArgs()[1] = p;

            result = invocation.proceed();//mybatis 的后续还有很多处理 比如 insert update delete 都会进下一个StatementHandler 的 interceptor

            processMedusaMethod(medusaMethodName, result, invocation, p, mt);

            p.clear();//help gc

        } else {///如果为用户的自定义普通方法 或者 插入UUID方法

            result = invocation.proceed();

            //modify by neo on 2019.08.19 for UUID insert 需要先获得生成的uuid值 再注入到插入的实体里 再进行插入操作 否则插入主键为空
            if(invocation.getArgs()[1] instanceof Map && MyHelper.checkInsertUUIDMethodSelectKey(medusaMethodName)) {

                Map<String, Object> p = (Map) invocation.getArgs()[1];

                MyReflectionUtils.invokeSetterMethod(p.get("pobj"), MyHelper.getSqlGenerator(p).getPkName(), ((ArrayList) result).get(0));//注入属性id值
            }
        }

        return result;
    }

    private void processMedusaMethod(String medusaMethodName, Object result, Invocation invocation, Map<String, Object> p, MappedStatement mt) throws SQLException, ParseException {

        //测试结果由高到低:startWith->indexOf->contains modify by neo on 2016.11.07
        if (!medusaMethodName.startsWith("select") && !medusaMethodName.startsWith("delete")) {//查询比较多 避免再去判断是不是以下方法中的 提升性能 可以 短路 modify by neo on 2016.11.07

            if (MyHelper.checkMedusaMethod(medusaMethodName)) {//若是多条件查询 medusas

                Object[] x = (Object[]) ((DefaultSqlSession.StrictMap) p.get("pobj")).get("array");//modify by neo on 2016.12.23

                Pager z = null;

                for (Object m : x) {//保留最后一个对象 pager 同 sqlOfFindMedusaGaze 处理 Pager modify by neo on 2019.08.20
                    if (m instanceof Pager) z = (Pager) m;
                }

                if (z != null && result != null) {//modify by neo on 2016.10.11
                    z.setList((List) result);//若结果集不为空则 给原有的pager参数注入list属性值
                    z.setTotalCount(MyHelper.caculatePagerTotalCount(((Executor) invocation.getTarget()).getTransaction().getConnection(), mt, p));/////通过invocation参数获得connection连接 并且通过这个连接查询出totalCount 注意: 不通过mybatis的 interceptor
                    z.setPageCount(z.getPageCount());
                }

                for (Object m : x) {//帮助用户自动让MyRestrictions清空 modify by neo on 2019.08.20
                    if (m instanceof MyRestrictions) ((MyRestrictions) m).clear();
                }

            } else if (MyHelper.checkInsertMethod(medusaMethodName)) {//如果是insert方法相关的则通过反射来修改传入对象的主键

                //returns generator id key change return values

                if (((Map) invocation.getArgs()[1]).get(SystemConfigs.PRIMARY_KEY) != null) {///modify by neo on 2016.10.27 因为有些非自增主键 手动添加的id值 不会返回id

                    Object m = Integer.valueOf((((Map) invocation.getArgs()[1]).get(SystemConfigs.PRIMARY_KEY).toString()));// mybatis long is default id types

                    MyReflectionUtils.invokeSetterMethod(p.get("pobj"), MyHelper.getSqlGenerator(p).getPkName(), m);//注入属性id值
                }

            } else if (MyHelper.checkUpdateMethod(medusaMethodName)) {

                if (result.toString().equals("0"))
                    throw new MedusaException("Medusa: The update method is there a number of exceptions to zero!(Maybe your incoming primary key is empty please check!)");

            } else {
//                        do nothing
            }
        } else {
//                        do nothing
        }
    }

    private void processBatchInsertPrimaryKeyWriteBack(Invocation invocation) throws SQLException, ParseException {

        StatementHandler sh = (StatementHandler) invocation.getTarget();

        Object parObj = sh.getBoundSql().getParameterObject();

        if (parObj instanceof Map) {//过滤掉用户自定义的各种方法

            Object pobj = ((Map) parObj).containsKey("pobj") ? ((Map) parObj).get("pobj") : null;

            if (pobj instanceof Map && sh.getBoundSql().getSql().contains("INSERT INTO")) {//过滤medusa单个insert delete update 的方法 //过滤掉deleteBatch insertBatch updateBatch之类的 非insert方法 modify by neo on 2017.12.13

                List<Object> paramList = (List) ((Map) pobj).get("param1");

                if (paramList != null && !paramList.isEmpty()) {

                    Statement st = (Statement) invocation.getArgs()[0];

                    ResultSet rs = st.getGeneratedKeys();

                    for (Object ot : paramList) {

                        if (!rs.next()) break;

                        //modify by neo on 2019.08.07 for mycat
                        int currentIdValue = sh.getBoundSql().getSql().toLowerCase().contains("next value for") ? rs.getInt(1) - 1 : rs.getInt(1);//注入属性id值 mycat 方式取到的都是+1 所以这里-1

                        MyReflectionUtils.invokeSetterMethod(ot, MyHelper.getSqlGenerator((Map) parObj).getPkName(), currentIdValue);
                    }
                }
            }
        }
    }

    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    public void setProperties(Properties properties) {
/*        String prop1 = properties.getProperty("prop1");
        String prop2 = properties.getProperty("prop2");*/
    }

}