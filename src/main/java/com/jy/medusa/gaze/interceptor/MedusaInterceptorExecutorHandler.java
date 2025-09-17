package com.jy.medusa.gaze.interceptor;

/**
 * Created by SuperScorpion on 16/9/15.
 */

import com.jy.medusa.gaze.stuff.MedusaSqlHelper;
import com.jy.medusa.gaze.stuff.Pager;
import com.jy.medusa.gaze.stuff.PagerHelper;
import com.jy.medusa.gaze.stuff.annotation.Id;
import com.jy.medusa.gaze.stuff.exception.MedusaException;
import com.jy.medusa.gaze.utils.MedusaReflectionUtils;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.builder.annotation.ProviderSqlSource;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.scripting.defaults.RawSqlSource;
import org.apache.ibatis.scripting.xmltags.DynamicSqlSource;
import org.apache.ibatis.scripting.xmltags.SqlNode;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

abstract class MedusaInterceptorExecutorHandler extends MedusaInterceptorStatementHandler {

    /**
     * 处理拦截器 Executor 的逻辑
     * For mybatis interceptor of Executor
     * @param invocation 参数
     * @return 结果
     * @throws InvocationTargetException 异常
     * @throws IllegalAccessException 异常
     * @throws ParseException 异常
     * @throws SQLException 异常
     * @throws NoSuchFieldException 异常
     */
    protected Object processExecutor(Invocation invocation) throws InvocationTargetException, IllegalAccessException, ParseException, SQLException, NoSuchFieldException {

        Object result;

        MappedStatement mt = (MappedStatement) invocation.getArgs()[0];

        String medusaMethodName = MedusaSqlHelper.getLastWord(mt.getId()).trim();

        //过滤只有medusa框架相关方法才能进入 Modify by SuperScorpion on 2019.05.31
        if (mt.getSqlSource() instanceof ProviderSqlSource && MedusaSqlHelper.checkMortalMethds(medusaMethodName)) {

            //重新构造invocation里的map参数
            Map<String, Object> p = rebuildParamMap(invocation, mt);

            //通过反射改变 insert相关方法的 keyProperties 的主键属性 实现插入时动态变更 @Options-keyProperty 的功能 modify by SuperScorpion on 20210522
            processInsertKeyPropertiesBeforeProceed(mt, medusaMethodName, p);

            //执行db操作 (当然 mybatis 的后续还有很多处理 比如 insert update delete 都会进下一个StatementHandler 的 interceptor)
            result = invocationProceed(invocation);

            //processBatchInsertPrimaryKeyWriteBack()先执行完后才到此处
            //medusa的一些方法后续处理(insert相关 update相关 medusaGaze相关)
            processMedusaMethod(medusaMethodName, result, invocation, p, mt);

            //clean map params
            resetParamMap(invocation, p);

        } /*else if (mt.getSqlSource() instanceof RawSqlSource//processExecutor里的invocationProceed(invocation)嵌套进入
                // medusa的insertSelectiveUUID 生成UUID时 SELECT REPLACE(UUID(), '-', '') 内部嵌套查询UUID的查询方法
                 && MedusaSqlHelper.checkInsertUUIDMethodSelectKey(medusaMethodName)) {

            result = invocationProceed(invocation);

            //modify by SuperScorpion on 2019.08.19 for UUID insert 需要先获得生成的uuid值 再注入到插入的实体里 再进行插入操作 否则插入主键为空
            Map<String, Object> p = (Map) invocation.getArgs()[1];

            MedusaReflectionUtils.invokeSetterMethod(p.get("pobj"), MedusaSqlHelper.getSqlGenerator(p).getPkPropertyName(), ((ArrayList) result).get(0));//注入属性id值
        }*/ else {//其他的各种普通方法

            //添加非medusa方法的查询分页处理 add by SuperScorpion on 20250906
            //只处理RawSqlSource 和 DynamicSqlSource
            //Pager.startPage启用
            if(MedusaSqlHelper.myPagerThreadLocal.get() != null
                    && (mt.getSqlSource() instanceof RawSqlSource || mt.getSqlSource() instanceof DynamicSqlSource)) {
                try {
                    result = processRawAndDynamicSqlSourcePagerHandler(invocation, mt);
                } finally {
                    MedusaSqlHelper.myPagerThreadLocal.remove();
                }
            } else {
                result = invocationProceed(invocation);
            }
        }

        return result;
    }

    private Object processRawAndDynamicSqlSourcePagerHandler(Invocation invocation, MappedStatement mt) throws InvocationTargetException, IllegalAccessException, SQLException {

        Object result;

        //重新构造invocation里的map参数
        Map<String, Object> p = rebuildParamMap(invocation, mt);

        //获取原有的BoundSql
        BoundSql boundSql = mt.getSqlSource().getBoundSql(p);

        //RawSqlSource里面其实包的就是StaticSqlSource
        //DynamicSqlSource 最后转成的也是StaticSqlSource
        //这里直接通过反射修改BoundSql里面的sql是无效的 因为获取sql都是通过各个sqlSource里getBoundSql方法实现
        //所以这里实现了自定义的两种sqlSource类 并在getBoundSql里在原有的sql返回前拼接了分页limit的sql语句
        //构造新的MappedStatement然后覆盖原有的 并用的自定义的sqlSource
        if(mt.getSqlSource() instanceof RawSqlSource) {
            invocation.getArgs()[0] = PagerHelper.rebuildMappedStatement(mt, new PagerHelper.MedusaStaticSqlSource(mt.getConfiguration(), boundSql.getSql(), boundSql.getParameterMappings()));
        } else if(mt.getSqlSource() instanceof DynamicSqlSource) {
            SqlNode rootSqlNode = (SqlNode) MedusaReflectionUtils.obtainFieldValue(mt.getSqlSource(), "rootSqlNode");
            invocation.getArgs()[0] = PagerHelper.rebuildMappedStatement(mt, new PagerHelper.MedusaDynamicSqlSource(mt.getConfiguration(), boundSql.getParameterMappings(), rootSqlNode));
        } else {
            //do nothing
        }

        //执行查询sql逻辑
        //此处会调用sqlSource里的getBoundSql方法 MedusaSqlHelper.myPagerThreadLocal.get()也会被调用
        result = invocationProceed(invocation);
        //注意:mybatis缓存的坑 mybatis有sqlSession缓存 MappedStatement每次查询都会产生新的 但是里面的StaticSqlSource会被缓存起来
        //StaticSqlSource里的sql 连续多次xml里同样方法名的查询
        //第二次第三次...执行sql语句和第一次一样 第一次拼接的分页sql依然还存在 依然还包含limit语句
        //使用了自定义的sqlSource实现类 所以下面的废弃
        //StaticSqlSource sss = (StaticSqlSource) MedusaReflectionUtils.obtainFieldValue(mt.getSqlSource(), "sqlSource");
        //MedusaReflectionUtils.setFieldValue(sss, "sql", sbb.toString());

        //拿到缓存的Pager类
        Pager z = MedusaSqlHelper.myPagerThreadLocal.get();
        //给pager赋result值和count值
        z.setList((List) result);//若结果集不为空则 给原有的pager参数注入list属性值
        z.setTotalCount(MedusaSqlHelper.caculatePagerTotalCount(((Executor) invocation.getTarget()).getTransaction().getConnection(), mt, p));/////通过invocation参数获得connection连接 并且通过这个连接查询出totalCount 注意: 不通过mybatis的 interceptor

        //clean map params
        resetParamMap(invocation, p);

        return result;
    }


    /**
     * 对mybatis的invocation里的map重新赋值
     * @param invocation 参数
     * @param mt         参数
     * @return
     */
    private Map<String, Object> rebuildParamMap(Invocation invocation, MappedStatement mt) {

        Map<String, Object> p;

        //批量插入 解析不了 多级对象如 pobj.param1.id 只能解析到param1.id 所以需要做以下区分功能
        if (invocation.getArgs()[1] instanceof Map) {//1.方法里有一个参数以上或参数类型为array或list 则mybatis会自动封装它为 map集合 modify by SuperScorpion on 2020.02.13

            p = (Map<String, Object>) invocation.getArgs()[1];

            p.put("msid", MedusaSqlHelper.removeLastWord(mt.getId()));

        } else {//2.方法里若是单个参数且不是array或list 需要用到 msid 所以手动封装map 带到provider方法去使用

            p = new HashMap<>(1 << 2);/// 1<<1

            p.put("pobj", invocation.getArgs()[1]);

            p.put("msid", MedusaSqlHelper.removeLastWord(mt.getId()));

            invocation.getArgs()[1] = p;
        }

        return p;
    }

    /**
     * 还原到方法执行之前invocation的原始参数
     * @param invocation 参数
     * @param p          参数
     */
    private void resetParamMap(Invocation invocation, Map<String, Object> p) {
        if(p.containsKey("pobj")) {//第二种情况清除新建的 hashmap
            invocation.getArgs()[1] = p.get("pobj");//还原
            p.clear();//help gc
        } else {//第一种情况 只删除put进去的msid
            p.remove("msid");//还原
        }
    }

    /**
     * 通过反射改变 insert 相关方法的 keyProperties 的主键属性 实现插入时动态变更 @Options-keyProperty 的功能 modify by SuperScorpion on 20210522
     * 在processExecutor里的invocationProceed(invocation) 处理前进入
     * For mybatis interceptor of Executor
     * @param mt
     * @param medusaMethodName
     * @param p
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    private void processInsertKeyPropertiesBeforeProceed(MappedStatement mt, String medusaMethodName, Map<String, Object> p) throws NoSuchFieldException, IllegalAccessException {

        //判断是否是insert相关的方法
        if (medusaMethodName.startsWith("insert")) {
            if (MedusaSqlHelper.checkInsertMethod(medusaMethodName)) {
                Field f = mt.getClass().getDeclaredField("keyProperties");
                f.setAccessible(true);
                f.set(mt, new String[]{MedusaSqlHelper.getSqlGenerator(p).getPkPropertyName()});
            } else if (MedusaSqlHelper.checkInsertBatchMethod(medusaMethodName)) {
                Field f = mt.getClass().getDeclaredField("keyProperties");
                f.setAccessible(true);
                f.set(mt, new String[]{"param1.".concat(MedusaSqlHelper.getSqlGenerator(p).getPkPropertyName())});
            } /*else if (MedusaSqlHelper.checkInsertUUIDMethod(medusaMethodName)) {
                //插入主键为UUID的方法时 keyProperties没啥用 因为是嵌套生成的UUID
                //do nothing
            } */else {
                //do nothing
            }
        }
    }

    /**
     * 主要是insert update 和 medusaGaze 相关方法的后续处理
     * 通过processExecutor里的invocationProceed(invocation) 处理完成后进入
     * For mybatis interceptor of Executor
     * @param medusaMethodName
     * @param result
     * @param invocation
     * @param p
     * @param mt
     * @throws SQLException
     * @throws ParseException
     */
    private void processMedusaMethod(String medusaMethodName, Object result, Invocation invocation, Map<String, Object> p, MappedStatement mt) throws SQLException, ParseException {

        //测试结果由高到低:startWith->indexOf->contains modify by SuperScorpion on 2016.11.07
        //medusa的一些方法后续处理(insert相关 update相关 medusaGaze相关)
        if (MedusaSqlHelper.checkMedusaGazeMethod(medusaMethodName) || MedusaSqlHelper.checkInsertMethod(medusaMethodName)
                || MedusaSqlHelper.checkUpdateMethod(medusaMethodName)) {//查询比较多 避免再去判断是不是以下方法中的 提升性能 modify by SuperScorpion on 2025.08.30

            if (MedusaSqlHelper.checkMedusaGazeMethod(medusaMethodName)) {//若是多条件查询 medusa

                //新老版本产生的 bug fixed (DefaultSqlSession.StrictMap - MapperMethod.ParamMap) 20210113
                Object[] x = (Object[]) ((MapperMethod.ParamMap) p).get("array");//modify by SuperScorpion on 2020.02.13

                for (Object m : x) {//同 sqlOfFindMedusaGaze 处理 Pager modify by SuperScorpion on 2019.08.20
                    if (m instanceof Pager) {
                        Pager z = (Pager) m;
                        z.setList((List) result);//若结果集不为空则 给原有的pager参数注入list属性值
                        z.setTotalCount(MedusaSqlHelper.caculatePagerTotalCount(((Executor) invocation.getTarget()).getTransaction().getConnection(), mt, p));/////通过invocation参数获得connection连接 并且通过这个连接查询出totalCount 注意: 不通过mybatis的 interceptor
                    }
                }

                //add by SuperScorpion on 2025.09.16 for 插件分页功能
                if(MedusaSqlHelper.myPagerThreadLocal.get() != null) {
                    Pager z = MedusaSqlHelper.myPagerThreadLocal.get();
                    MedusaSqlHelper.myPagerThreadLocal.remove();
                    z.setList((List) result);//若结果集不为空则 给原有的pager参数注入list属性值
                    z.setTotalCount(MedusaSqlHelper.caculatePagerTotalCount(((Executor) invocation.getTarget()).getTransaction().getConnection(), mt, p));/////通过invocation参数获得connection连接 并且通过这个连接查询出totalCount 注意: 不通过mybatis的 interceptor
                }

//                for (Object m : x) {//帮助用户自动让MedusaRestrictions清空 modify by SuperScorpion on 2019.08.20
//                    if (m instanceof MedusaRestrictions) ((MedusaRestrictions) m).clear();
//                }

            } else if (MedusaSqlHelper.checkInsertMethod(medusaMethodName)) {//如果是insert方法相关的则通过反射来修改传入对象的主键

                //returns generator id key change return values
                //增加动态主键 modify by SuperScorpion on 20210521
                //增加id type modify by SuperScorpion on 20250830
                String pkName = MedusaSqlHelper.getSqlGenerator(p).getPkPropertyName();
                Id.Type pkGeneratedType = MedusaSqlHelper.getSqlGenerator(p).getPkGeneratedType();

                //modify by SuperScorpion on 20161027 使用@Options(useGeneratedKeys = true 才会返回id
                //modify by SuperScorpion on 20250830 自定义增长主键的值(snowflake uuid)已经在实体插入前通过反射写入了 所以不需要再反射写入主键值
                if (((Map) invocation.getArgs()[1]).get(pkName) != null && pkGeneratedType.equals(Id.Type.AUTO)) {

                    Long m = Long.valueOf((((Map) invocation.getArgs()[1]).get(pkName).toString()));// mybatis long is default id types

                    MedusaReflectionUtils.invokeSetterMethod(p.get("pobj"), MedusaSqlHelper.getSqlGenerator(p).getPkPropertyName(), m);//注入属性id值
                }

            } else if (MedusaSqlHelper.checkUpdateMethod(medusaMethodName)) {

                if (result.toString().equals("0"))
                    throw new MedusaException("Medusa: The update method is there a number of exceptions to zero!(Maybe your incoming primary key is empty please check!)");
            } else {
//                        do nothing
            }
        } else {
//                        do nothing
        }
    }
}