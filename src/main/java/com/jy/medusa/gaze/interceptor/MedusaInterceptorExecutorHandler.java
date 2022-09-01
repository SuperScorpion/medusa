package com.jy.medusa.gaze.interceptor;

/**
 * Created by neo on 16/9/15.
 */

import com.jy.medusa.gaze.stuff.MedusaSqlHelper;
import com.jy.medusa.gaze.stuff.Pager;
import com.jy.medusa.gaze.stuff.exception.MedusaException;
import com.jy.medusa.gaze.utils.MedusaReflectionUtils;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.builder.annotation.ProviderSqlSource;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.scripting.defaults.RawSqlSource;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

abstract class MedusaInterceptorExecutorHandler extends MedusaInterceptorStatementHandler {

    /**
     * 处理拦截器 Executor 的逻辑
     * For mybatis interceptor of Executor
     * @param invocation
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws ParseException
     * @throws SQLException
     * @throws NoSuchFieldException
     */
    protected Object processExecutor(Invocation invocation) throws InvocationTargetException, IllegalAccessException, ParseException, SQLException, NoSuchFieldException {

        Object result;

        MappedStatement mt = (MappedStatement) invocation.getArgs()[0];

        String medusaMethodName = MedusaSqlHelper.getLastWord(mt.getId()).trim();

        if (mt.getSqlSource() instanceof ProviderSqlSource && MedusaSqlHelper.checkMortalMethds(medusaMethodName)) {//Modify by neo on 2019.05.31

            Map<String, Object> p;

            //批量插入 解析不了 多级对象如 pobj.param1.id 只能解析到param1.id 所以需要做以下区分功能
            if (invocation.getArgs()[1] instanceof Map) {//1.方法里有两个参数以上或参数类型为array或list 则mybatis会自动封装它为 map集合 modify by neo on 2020.02.13

                p = (Map<String, Object>) invocation.getArgs()[1];

                p.put("msid", MedusaSqlHelper.removeLastWord(mt.getId()));

            } else {//2.方法里若是单个参数且不是array或list 需要用到 msid 所以手动封装map 带到provider方法去使用

                p = new HashMap<>(1 << 2);/// 1<<1

                p.put("pobj", invocation.getArgs()[1]);

                p.put("msid", MedusaSqlHelper.removeLastWord(mt.getId()));

                invocation.getArgs()[1] = p;
            }

            //通过反射改变 insert相关方法的 keyProperties 的主键属性 实现插入时动态变更 @Options-keyProperty 的功能 modify by neo on 20210522
            processInsertKeyPropertiesBeforeProceed(mt, medusaMethodName, p);

            //执行db操作 (当然 mybatis 的后续还有很多处理 比如 insert update delete 都会进下一个StatementHandler 的 interceptor)
            result = invocationProceed(invocation);

            //processBatchInsertPrimaryKeyWriteBack()先执行完后才到此处
            //medusa的一些方法后续处理(insert相关 update相关 medusaGaze相关)
            processMedusaMethod(medusaMethodName, result, invocation, p, mt);

            //clean map params
            if(p.containsKey("pobj")) {//第二种情况清除新建的 hashmap
                p.clear();//help gc
            } else {//第一种情况 只删除put进去的msid
                p.remove("msid");
            }

        } else if (mt.getSqlSource() instanceof RawSqlSource//processExecutor里的invocationProceed(invocation)嵌套进入
                // medusa的insertSelectiveUUID 生成UUID时 SELECT REPLACE(UUID(), '-', '') 内部嵌套查询UUID的查询方法
                 && MedusaSqlHelper.checkInsertUUIDMethodSelectKey(medusaMethodName)) {

            result = invocationProceed(invocation);

            //modify by neo on 2019.08.19 for UUID insert 需要先获得生成的uuid值 再注入到插入的实体里 再进行插入操作 否则插入主键为空

            Map<String, Object> p = (Map) invocation.getArgs()[1];

            MedusaReflectionUtils.invokeSetterMethod(p.get("pobj"), MedusaSqlHelper.getSqlGenerator(p).getPkPropertyName(), ((ArrayList) result).get(0));//注入属性id值
        } else {//其他的各种方法
            result = invocationProceed(invocation);
        }

        return result;
    }

    /**
     * 通过反射改变 insert 相关方法的 keyProperties 的主键属性 实现插入时动态变更 @Options-keyProperty 的功能 modify by neo on 20210522
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
        if(medusaMethodName.startsWith("insert")) {
            if (MedusaSqlHelper.checkInsertMethod(medusaMethodName)) {
                Field f = mt.getClass().getDeclaredField("keyProperties");
                f.setAccessible(true);
                f.set(mt, new String[]{MedusaSqlHelper.getSqlGenerator(p).getPkPropertyName()});
            } else if (MedusaSqlHelper.checkInsertBatchMethod(medusaMethodName)) {
                Field f = mt.getClass().getDeclaredField("keyProperties");
                f.setAccessible(true);
                f.set(mt, new String[]{"param1.".concat(MedusaSqlHelper.getSqlGenerator(p).getPkPropertyName())});
            } else if (MedusaSqlHelper.checkInsertUUIDMethod(medusaMethodName)) {
                //插入主键为UUID的方法时 keyProperties没啥用 因为是嵌套生成的UUID
                //do nothing
            } else {
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

        //测试结果由高到低:startWith->indexOf->contains modify by neo on 2016.11.07
        if (!medusaMethodName.startsWith("select") && !medusaMethodName.startsWith("delete")) {//查询比较多 避免再去判断是不是以下方法中的 提升性能 可以 短路 modify by neo on 2016.11.07

            if (MedusaSqlHelper.checkMedusaGazeMethod(medusaMethodName)) {//若是多条件查询 medusa

                //新老版本产生的 bug fixed (DefaultSqlSession.StrictMap - MapperMethod.ParamMap) 20210113
                Object[] x = (Object[]) ((MapperMethod.ParamMap) p).get("array");//modify by neo on 2020.02.13

                Pager z = null;

                for (Object m : x) {//保留最后一个对象 pager 同 sqlOfFindMedusaGaze 处理 Pager modify by neo on 2019.08.20
                    if (m instanceof Pager) z = (Pager) m;
                }

                if (z != null && result != null) {//modify by neo on 2016.10.11
                    z.setList((List) result);//若结果集不为空则 给原有的pager参数注入list属性值
                    z.setTotalCount(MedusaSqlHelper.caculatePagerTotalCount(((Executor) invocation.getTarget()).getTransaction().getConnection(), mt, p));/////通过invocation参数获得connection连接 并且通过这个连接查询出totalCount 注意: 不通过mybatis的 interceptor
                    z.setPageCount(z.getPageCount());
                }

//                for (Object m : x) {//帮助用户自动让MedusaRestrictions清空 modify by neo on 2019.08.20
//                    if (m instanceof MedusaRestrictions) ((MedusaRestrictions) m).clear();
//                }

            } else if (MedusaSqlHelper.checkInsertMethod(medusaMethodName)) {//如果是insert方法相关的则通过反射来修改传入对象的主键

                //returns generator id key change return values
                //增加动态主键 modify by neo on 20210521
                String pkName = MedusaSqlHelper.getSqlGenerator(p).getPkPropertyName();

                if (((Map) invocation.getArgs()[1]).get(pkName) != null) {///modify by neo on 2016.10.27 因为有些非自增主键 手动添加的id值 不会返回id

                    Object m = Integer.valueOf((((Map) invocation.getArgs()[1]).get(pkName).toString()));// mybatis long is default id types

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