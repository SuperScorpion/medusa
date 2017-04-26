package com.jy.medusa.interceptor;

/**
 * Created by neo on 16/9/15.
 */

import com.jy.medusa.stuff.MyHelper;
import com.jy.medusa.stuff.Pager;
import com.jy.medusa.stuff.exception.MedusaException;
import com.jy.medusa.utils.MyReflectionUtils;
import com.jy.medusa.utils.SystemConfigs;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.defaults.DefaultSqlSession;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Intercepts({
        @Signature(method = "update", type = StatementHandler.class, args = {Statement.class}),
        @Signature(method = "query", type = Executor.class, args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
        @Signature(method = "update", type = Executor.class, args = {MappedStatement.class, Object.class})
})
public class MyInterceptor implements Interceptor {

    public Object intercept(Invocation invocation) throws Throwable {

        Object result;

        if (invocation.getTarget() instanceof Executor) {

            MappedStatement mt = (MappedStatement) invocation.getArgs()[0];

            String medusaMethodName = MyHelper.getLastWord(mt.getId()).trim();

            if (MyHelper.checkMortalMethds(medusaMethodName)) {//Modify by neo on 2016.10.25

            /*mt.getConfiguration().setAggressiveLazyLoading(false);//TODO
            mt.getConfiguration().setLazyLoadingEnabled(true);//TODO*/

                Map<String, Object> p = new HashMap<>();

                p.put("pobj", invocation.getArgs()[1]);

                p.put("msid", MyHelper.removeLastWord(mt.getId()));

                invocation.getArgs()[1] = p;

                result = invocation.proceed();//mybatis 的后续还有很多处理

                //测试结果由高到低:startWith->indexOf->contains modify by neo on 2016.11.07
                if (!medusaMethodName.startsWith("select") && !medusaMethodName.startsWith("delete")) {//查询比较多 避免再去判断是不是以下方法中的 提升性能 可以 短路 modify by neo on 2016.11.07

                    if (MyHelper.checkMedusaMethod(medusaMethodName)) {//若是多条件查询 medusas

//                    Object[] x = (Object[]) ((MapperMethod.ParamMap) p.get("pobj")).get("param2");
                        Object[] x = (Object[]) ((DefaultSqlSession.StrictMap) p.get("pobj")).get("array");//modify by neo on 2016.12.23

                        Pager z = null;

                        for (Object m : x) {
                            if (m instanceof Pager) {
                                z = (Pager) m;
                                break;//只要第一个对象 pager
                            }
                        }

                        if (z != null && result != null) {//modify by neo on 2016.10.11
//                        MyHelper.myThreadLocal.set(1);
                            z.setList((List) result);//若结果集不为空则 给原有的pager参数注入list属性值
                            z.setTotalCount(MyHelper.caculatePagerTotalCount(((Executor) invocation.getTarget()).getTransaction().getConnection(), mt, p));/////通过invocation参数获得connection连接 并且通过这个连接查询出totalCount
                            z.setPageCount(z.getPageCount());
                        }

                        /*for (Object m : x) {
                            if(m instanceof MyRestrictions) {
                                ((MyRestrictions) m).clear();//自动帮使用者删除了 条件list modify by neo on 2017.04.26
                            }
                        }*/


                        //MyReflectionUtils.invokeSetterMethod(z, "list", result, List.class);//参数注入属性的值
                    } else if (MyHelper.checkInsertMethod(medusaMethodName)) {//如果是insert方法相关的则修改传入对象的id 回执其

                        //returns generator id key change return values

                        if (((Map) invocation.getArgs()[1]).get(SystemConfigs.PRIMARY_KEY) != null) {///modify by neo on 2016.10.27 因为有些非自增主键 手动添加的id值 不会返回艾迪

                            Object m = Integer.valueOf((((Map) invocation.getArgs()[1]).get(SystemConfigs.PRIMARY_KEY).toString()));//TODO mybaits long is default id types

                            MyReflectionUtils.invokeSetterMethod(p.get("pobj"), SystemConfigs.PRIMARY_KEY, m);//注入属性id值
                        }

                    } else if (MyHelper.checkUpdateMethod(medusaMethodName)) {

                        if (result.toString().equals("0"))
                            throw new MedusaException("Medusa: The update method is there a number of exceptions to zero!(Maybe your incoming primary key is empty please check!)");

                    } else if (MyHelper.checkInsertUUIDMethod(medusaMethodName)) {//modify by neo on 2016.12.17

                        //returns generator id key change return values

                        if (((Map) invocation.getArgs()[1]).get(SystemConfigs.PRIMARY_KEY) != null) {///modify by neo on 2016.10.27 因为有些非自增主键 手动添加的id值 不会返回艾迪

                            Object m = String.valueOf((((Map) invocation.getArgs()[1]).get(SystemConfigs.PRIMARY_KEY).toString()));//TODO mybaits long is default id types

                            MyReflectionUtils.invokeSetterMethod(p.get("pobj"), SystemConfigs.PRIMARY_KEY, m);//注入属性id值
                        }
                    }
                }
            } else {///如果为普通方法 自定义

                result = invocation.proceed();
            }
        } else if(invocation.getTarget() instanceof StatementHandler) {//delete insert update 都会进来此拦截(medusa的或非medusa的)

            result = invocation.proceed();//先执行再处理的 不然处理的是上次的

            StatementHandler sh = (StatementHandler) invocation.getTarget();

            if(sh.getBoundSql().getSql().contains("INSERT INTO") && !sh.getBoundSql().getSql().contains("on duplicate key update")) {//过滤掉delete update 批量更新之类的 非insert方法

                Object c = ((Map) sh.getBoundSql().getParameterObject()).containsKey("pobj") ? ((Map) sh.getBoundSql().getParameterObject()).get("pobj") : null;//过滤掉用户自定义的方法

                if (c instanceof Map) {//过滤掉普通插入

                    List<Object> paramList = (List) ((Map) c).get("list");

                    if (paramList != null && !paramList.isEmpty()) {

                        Statement st = (Statement) invocation.getArgs()[0];

                        ResultSet rs = st.getGeneratedKeys();

                        for (Object ot : paramList) {

                            if (!rs.next()) break;

                            MyReflectionUtils.invokeSetterMethod(ot, SystemConfigs.PRIMARY_KEY, rs.getInt(1));//注入属性id值 rs.getInt(1) 每次执行都取到不同 id 很神奇 (一行多列的原因是吗)
                        }
                    }
                }
            }

        } else {
            result = invocation.proceed();
        }

        return result;
    }

    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    public void setProperties(Properties properties) {
/*        String prop1 = properties.getProperty("prop1");
        String prop2 = properties.getProperty("prop2");*/
        /*System.out.println(prop1 + "------" + prop2);*/
    }

}