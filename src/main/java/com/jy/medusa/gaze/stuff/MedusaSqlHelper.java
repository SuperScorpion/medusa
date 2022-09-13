package com.jy.medusa.gaze.stuff;

import com.jy.medusa.gaze.stuff.annotation.Column;
import com.jy.medusa.gaze.stuff.annotation.Id;
import com.jy.medusa.gaze.stuff.annotation.Table;
import com.jy.medusa.gaze.stuff.cache.MedusaSqlHelperCacheManager;
import com.jy.medusa.gaze.stuff.cache.MyReflectCacheManager;
import com.jy.medusa.gaze.stuff.exception.MedusaException;
import com.jy.medusa.gaze.utils.MedusaCommonUtils;
import com.jy.medusa.gaze.utils.MedusaReflectionUtils;
import com.jy.medusa.gaze.utils.SystemConfigs;
import com.jy.medusa.generator.MedusaGenUtils;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.ParameterMode;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.defaults.DefaultSqlSession;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeException;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by neo on 16/9/14.
 */
public class MedusaSqlHelper {

    private static final Logger logger = LoggerFactory.getLogger(MedusaSqlHelper.class);

    //主要是为了解决 分页时 多次生成查询分页语句和总计数的查询语句时 缓存分页的查询语句
    public static ThreadLocal<String> myThreadLocal = new ThreadLocal<>();


    /**
     * com.jy.koubei.persistence.UserMapper.SelectOne
     * remove .SelectOne
     * then you can get the UserMapper path to build entity class
     * @param msid 参数
     * @return 返回值类型
     */
    public static String removeLastWord(String msid) {

        return msid.substring(0, msid.lastIndexOf("."));///moidify by neo on 2016.10.27

        /*String[] arr = origin.split("\\.");
        StringBuilder sbb = new StringBuilder(70);
        for(int i=0; i < arr.length; i++) {
            if(i < arr.length - 1) {
                sbb.append(arr[i]);
                sbb.append(".");
            }
        }
        sbb.deleteCharAt(sbb.length()-1);
        return sbb.toString();*/
    }

    /**
     * com.jy.koubei.persistence.UserMapper.SelectOne
     * remove com.jy.koubei.persistence.UserMapper.
     * @param msid 参数
     * @return 返回值类型
     */
    public static String getLastWord(String msid) {

        return msid.substring(msid.lastIndexOf(".") + 1);

    }


    /**
     * 在interceptori 里面判断方法是不是用户自定义方法
     * @param msidWho 参数
     * @return 返回值类型
     */
    public static boolean checkMortalMethds(String msidWho) {//modify by neo on 2016.10.25 sq

        return SystemConfigs.MY_ALL_METHOD_NANES_LIST.contains(msidWho) ? true : false;
    }

    /**
     * check methodName is insert or insertSelective
     * @param methodName 参数
     * @return 返回值类型
     */
    public static boolean checkInsertMethod(String methodName) {
        return methodName.equals("insertSelective") || methodName.equals("insert") ? true : false;
    }

    /**
     * check methodName is insertBatch or insertBatchOfMyCat
     * @param methodName 参数
     * @return 返回值类型
     */
    public static boolean checkInsertBatchMethod(String methodName) {
        return methodName.equals("insertBatchOfMyCat") || methodName.equals("insertBatch") ? true : false;
    }

    /**
     * check methodName is updateByPrimaryKey or updateByPrimaryKeySelective
     * @param methodName 参数
     * @return 返回值类型
     */
    public static boolean checkUpdateMethod(String methodName) {
        return (methodName.equals("updateByPrimaryKeySelective") || methodName.equals("updateByPrimaryKey")) ? true : false;
    }

    /**
     * check medusaGaze method
     * @param methodName  参数
     * @return 返回值类型
     */
    public static boolean checkMedusaGazeMethod(String methodName) {
        return methodName.equals("selectHolyGaze") ? true : false;
    }

    /**
     * check insertUUID method
     * @param methodName     参数
     * @return 返回值类型
     */
    public static boolean checkInsertUUIDMethod(String methodName) {
        return methodName.equals("insertSelectiveUUID") ? true : false;
    }

    /**
     * check insertUUID selectKey method
     * @param methodName     参数
     * @return 返回值类型
     */
    public static boolean checkInsertUUIDMethodSelectKey(String methodName) {
        return methodName.equals("insertSelectiveUUID!selectKey") ? true : false;
    }


    /**
     * 获取 CacheGenerator 有则返回缓存里的 没有则创建
     * @param m 参数
     * @return 返回值类型
     */
    public static MedusaSqlGenerator getSqlGenerator(Map<String, Object> m) {

        String p = m.get("msid").toString();

        MedusaSqlGenerator por = MedusaSqlHelperCacheManager.getCacheGenerator(p);

        if(por != null) {
            return por;
        } else {
            Class<?> c = getEntityClass(p);
            MedusaSqlGenerator q = initSqlGenerator(c);
            MedusaSqlGenerator msr = MedusaSqlHelperCacheManager.putCacheGenerator(p, q);

            if(msr != null) {
                return msr;
            } else {
                logger.debug("Medusa: Successfully initialize the " + c.getSimpleName() + " Basic information of MedusaSqlGenerator!");
                return q;
            }
        }
    }

    /**
     * 获取返回值类型 - 实体类型
     * @param msid 参数
     * @return 返回值类型
     */
    public static Class<?> getEntityClass(String msid) {

        Class<?> mapperClass = buildClassByPath(msid);

        String sn = mapperClass.getSimpleName();

        ///Class<?> ps = MedusaSqlHelperCacheManager.getCacheClass(sn);///modify by neo on 2016.12.1

        //if (ps != null) {
        //    return ps;
        //} else {
        Type[] types = mapperClass.getGenericInterfaces();
        for (Type type : types) {
            if (type instanceof ParameterizedType) {
                ParameterizedType t = (ParameterizedType) type;
                if (t.getRawType() == mapperClass || ((Class<?>) t.getRawType()).isAssignableFrom(mapperClass)) {
                    Class<?> returnType = (Class<?>) t.getActualTypeArguments()[0];
//                        MedusaSqlHelperCacheManager.putCacheClass(sn, returnType);///modify by neo on 2016.12.1
                    logger.debug("Medusa: Successfully initialize the " + sn + " cache information");
                    return returnType;
                }
            }
        }
//        }
        throw new MedusaException("Medusa: Can't get Mapper<T> generic type: " + sn);
    }

    /**
     * construct class by classpath
     * @param classPath        参数
     * @return 返回值类型
     */
    public static Class<?> buildClassByPath(String classPath) {
        if(classPath != null) {
            try {
                return Class.forName(classPath);
            } catch (ClassNotFoundException e) {
                logger.error("Medusa: According to the path to build Class object " + classPath + " appeared abnormal situation!");
                e.printStackTrace();
            }
        }
        return null;
    }

    ////Solving high concurrency problems modify by neo on 2017.07.19
    /*private static synchronized MedusaSqlGenerator initAndCacheGenerator(String p) {

        MedusaSqlGenerator por = MedusaSqlHelperCacheManager.getCacheGenerator(p);
        if(por != null) return por;

        Class<?> c = getEntityClass(p);
        MedusaSqlGenerator q = initSqlGenerator(c);
        MedusaSqlHelperCacheManager.putCacheGenerator(p, q);
        logger.debug("Medusa: Successfully initialize the " + c.getSimpleName() + " Basic information of MedusaSqlGenerator!");
        return q;
    }*/

    /**
     *
     * @param entityClass 参数
     * @return 返回值类型
     */
    private static MedusaSqlGenerator initSqlGenerator(Class<?> entityClass) {

        if(entityClass == null) {
            throw new MedusaException("Medusa: initSqlGenerator No entity type introduced!");
        }

        String pkName = SystemConfigs.PRIMARY_KEY;//默认实体类主键名称
        String tableName;//表名

        Map<String, String> currentColumnFieldNameMap = new HashMap<>();
        Map<String, String> currentFieldTypeNameMap = new HashMap<>();


        Field[] fields = MyReflectCacheManager.getCacheFieldArray(entityClass);////从缓存读取 modify by neo on 2016.11.13

        //获取子类属性上的注解
        String fieldName;
        String columnName;
        for (Field field : fields) {

            if (field != null) {

                fieldName = field.getName();

                Column tableColumn = field.getAnnotation(Column.class);

                if (tableColumn != null) {

                    columnName = tableColumn.name();

                    if (field.isAnnotationPresent(Id.class)) pkName = tableColumn.name();

                    // 如果未标识特殊的列名，默认取字段名
                    columnName = MedusaCommonUtils.isBlank(columnName) ? MedusaGenUtils.camelToUnderline(fieldName) : columnName.trim();

                    currentFieldTypeNameMap.put(fieldName, field.getType().getSimpleName());
                    currentColumnFieldNameMap.put(columnName, fieldName);
                }
            }
        }

        Table table = entityClass.getAnnotation(Table.class);
        if (table == null) throw new MedusaException("Medusa: class - " + entityClass + " Not using @Table annotation identification!");
        tableName = table.name();

        return new MedusaSqlGenerator(currentColumnFieldNameMap, currentFieldTypeNameMap, tableName, pkName, entityClass);
    }

    /**
     * reverse map - value
     * @param map      参数
     * @param <K>      参数
     * @param <T>      参数
     * @return 返回值类型
     */
    public static <K, T> Map<T, K> exchangeKeyValues(Map<K, T> map) {

         Map<T, K> resultMap = null;

        //map.forEach((K, T) -> resultMap.put(T, K));///modify by neo on 2016.12.3

        if(map != null && !map.isEmpty()) {
            resultMap = new HashMap<T, K>();
            for(Map.Entry<K, T> entry : map.entrySet()) {
                K key = entry.getKey();
                T value = entry.getValue();
                //resultMap.remove(key);会报错
                resultMap.put(value, key);
            }
        }

        return resultMap;
    }

    /**
     * 提供给生成查询字段使用
     * 上一步的方法会先判断 尾部参数是否为空 不为空也可能是pager参数 不是string参数 本方法会进一步校验
     * 若用户没有给字段的参数则用 *
     * 把实体字段名称改为数据库字段名称
     * 改进后的方法用map缓存直接取
     * @param psArray 参数
     * @param currentFieldColumnNameMap 参数
     * @return 返回值类型
     */
    public static String buildColumnNameForSelect(Object[] psArray, Map<String, String> currentFieldColumnNameMap) {

        StringBuilder sbb = new StringBuilder(255);

        if(psArray != null && psArray.length != 0) {
            for (Object z : psArray) {//["a,b,b,n,m","a"] 可选的字段有可能是分开写入的 多参数传入的值

                if (z instanceof String && MedusaCommonUtils.isNotBlank(z.toString())) {

                    String[] p = z.toString().split(",");//modify by neo on 2016.12.04

                    for (String m : p) {

                        if (MedusaCommonUtils.isNotBlank(m)) {

                            sbb.append(buildColumnNameForAll(m, currentFieldColumnNameMap));

                            sbb.append(",");
                        }
                    }
                }
            }
        }

        //modify by neo on 2016 10 13
        if(sbb.length() == 0) sbb.append(" * ");///如果后面一个参数都没匹配到string 则会查处所有的字段值
        if(sbb.lastIndexOf(",") != -1) sbb.deleteCharAt(sbb.lastIndexOf(","));//去掉最后一个,
        return sbb.toString();
    }

    /**
     * 用于给medusa gaze使用让对方能够 输入 列名或者属性名字都能转化为列名 容错机制
     * @param ori     参数
     * @param currentFieldColumnNameMap 参数
     * @return 返回值类型
     */
    public static String buildColumnNameForAll(String ori, Map<String, String> currentFieldColumnNameMap) {

        String result;
//        if (ori.contains("_"))//让用户用的可选字段 属性名字和数据库表字段名称容错
//            result = ori.trim();
        if(currentFieldColumnNameMap.containsKey(ori.trim()))//modify by neo on 2016.11.3
            result = currentFieldColumnNameMap.get(ori.trim());
        else
            result = ori.trim();

        return result;
    }

    /**
     * @deprecated
     * @param pss             参数
     * @return 返回值类型
     * buildColumnNameForSelect replace
     */
    public static String convertEntityName2SqlName(String pss) {

//        String[] p = MedusaCommonUtils.split(pss, ",");
        String[] p = pss.split(",");

        StringBuilder sbb = new StringBuilder();

        for(String m : p) {
            sbb.append(MedusaGenUtils.camelToUnderline(m));
            sbb.append(",");
        }

        sbb.deleteCharAt(sbb.lastIndexOf(","));
        return sbb.toString();
    }


    /**
     * @deprecated
     * generate the get totalPageCount sql for pager and work it
     * use the jdbc to get data for page
     * @param conne 参数
     * @param p 参数
     * @return 返回值类型
     */
    public static int caculatePagerTotalCount(Connection conne, Map<String, Object> p) {

        PreparedStatement countStmt = null;
        ResultSet rs = null;

//        String countSql = getSqlGenerator(p).sql_findAllCount(((MapperMethod.ParamMap) p.get("pobj")).get("param1"));
        String countSql = getSqlGenerator(p).sqlOfSelectHolyCount((Object[]) ((DefaultSqlSession.StrictMap) p.get("pobj")).get("array"));

        int totalCount = 0;

        try {
            countStmt = conne.prepareStatement(countSql);

            rs = countStmt.executeQuery();

            if (rs.next()) {
                totalCount = rs.getInt(1);
            }

        } catch (SQLException e) {
            logger.error("Medusa: The total number of queries is abnormal when paging " + e);
            e.printStackTrace();
        } finally {
            try {
                rs.close();
                countStmt.close();
            } catch (SQLException e) {
                logger.error("Medusa: Close connection appears abnormal in paging query total number " + e);
                e.printStackTrace();
            }
        }

        return totalCount;
    }


    /**
     * 预编译执行 分页的操作 总数
     * @param conne                  参数
     * @param mst                        参数
     * @param p                              参数
     * @return 返回值类型
     */
    public static int caculatePagerTotalCount(Connection conne, MappedStatement mst, Map<String, Object> p) {

        PreparedStatement countStmt = null;
        ResultSet rs = null;

        int totalCount = 0;

        try {
            BoundSql boundSql = mst.getBoundSql(p);

/*            String countSql = getSqlGenerator(p).sql_findAllCount(((MapperMethod.ParamMap) p.get("pobj")).get("param1"),
                    ((MapperMethod.ParamMap) p.get("pobj")).get("param2"));*/
            String countSql = getSqlGenerator(p).sqlOfSelectHolyCount((Object[]) ((MapperMethod.ParamMap) p).get("array"));//新老版本产生的 bug fixed (DefaultSqlSession.StrictMap - MapperMethod.ParamMap) 20210113

//            BoundSql countBS = new BoundSql(mst.getConfiguration(), countSql, boundSql.getParameterMappings(),p);

            String handSql = countSql.replaceAll("\\#\\{[^\\#]+\\}", "?");

            countStmt = conne.prepareStatement(handSql);

            /*DefaultParameterHandler handler = new DefaultParameterHandler(mst, p, boundSql);
            handler.setParameters(countStmt);*/

            setParameters(countStmt, mst, boundSql, p);

            rs = countStmt.executeQuery();

            if (rs.next()) {
                totalCount = rs.getInt(1);
            }

        } catch (SQLException e) {
            logger.error("Medusa: The total number of queries is abnormal when paging " + e);
            e.printStackTrace();
        } finally {
            try {
                if(rs != null) rs.close();
                if(countStmt != null) countStmt.close();
            } catch (SQLException e) {
                logger.error("Medusa: Close connection appears abnormal in paging query total number " + e);
                e.printStackTrace();
            }
        }

        return totalCount;
    }

    /**
     * 对SQL参数(?)设值,参考org.apache.ibatis.executor.parameter.DefaultParameterHandler
     * @param ps               参数
     * @param mappedStatement      参数
     * @param boundSql                 参数
     * @param parameterObject              参数
     * @throws SQLException
     */
    private static void setParameters(PreparedStatement ps,MappedStatement mappedStatement,BoundSql boundSql,Object parameterObject) throws SQLException {

        ErrorContext.instance().activity("setting parameters").object(mappedStatement.getParameterMap().getId());
        List parameterMappings = boundSql.getParameterMappings();
        Configuration configuration = mappedStatement.getConfiguration();
        TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();

        if(parameterMappings != null) {
            for(int i = 0; i < parameterMappings.size(); ++i) {
                ParameterMapping parameterMapping = (ParameterMapping)parameterMappings.get(i);
                if(parameterMapping.getMode() != ParameterMode.OUT) {
                    String propertyName = parameterMapping.getProperty();
                    Object value;
                    if(boundSql.hasAdditionalParameter(propertyName)) {
                        value = boundSql.getAdditionalParameter(propertyName);
                    } else if(parameterObject == null) {
                        value = null;
                    } else if(typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
                        value = parameterObject;
                    } else {
                        MetaObject typeHandler = configuration.newMetaObject(parameterObject);
                        value = typeHandler.getValue(propertyName);
                    }

                    TypeHandler var12 = parameterMapping.getTypeHandler();
                    JdbcType jdbcType = parameterMapping.getJdbcType();
                    if(value == null && jdbcType == null) {
                        jdbcType = configuration.getJdbcTypeForNull();
                    }

                    try {
                        var12.setParameter(ps, i + 1, value, jdbcType);
                    } catch (TypeException var10) {
                        throw new TypeException("Could not set parameters for mapping: " + parameterMapping + ". Cause: " + var10, var10);
                    } catch (SQLException var11) {
                        throw new TypeException("Could not set parameters for mapping: " + parameterMapping + ". Cause: " + var11, var11);
                    }
                }
            }
        }

    }


    /**
     * 生成插入的sql语句时 not selective
     * @param currentFieldTypeNameMap 参数
     * @param currentFieldColumnNameMap 参数
     * @param t 参数
     * @param primaryKeyColumn 参数
     * @return 返回值类型
     */
    public static String[] concatInsertDynamicSql(Map<String, String> currentFieldTypeNameMap, Map<String, String> currentFieldColumnNameMap, Object t, String primaryKeyColumn) {

        int dataSizeAll = currentFieldTypeNameMap.size();

        StringBuilder sbb = new StringBuilder(39 * dataSizeAll);
        StringBuilder sbs = new StringBuilder(15 * dataSizeAll);

        for (Map.Entry<String, String> entry : currentFieldTypeNameMap.entrySet()) {//同一个hashset 遍历的元素顺序是否一样的

            String fieName = entry.getKey();

            if (t == null || (t != null && MedusaReflectionUtils.obtainFieldValue(t, fieName) != null)) {///modify by neo on 20170117 selective

               /* if (fieName.trim().equalsIgnoreCase(primaryKeyColumn)) {

//                sbb.append("#{id, jdbcType=" + javaType2SqlTypes(currentFieldTypeNameMap.get(fieName)) + "},");
                    sbb.append("#{pobj.").append(primaryKeyColumn).append(", jdbcType=").append(javaType2SqlTypes(entry.getValue())).append("},");

                    sbs.append(currentFieldColumnNameMap.get(fieName));
                    sbs.append(",");
                } else {*/

                    sbb.append("#{pobj.");
                    sbb.append(fieName);
                    sbb.append(", jdbcType=");
                    sbb.append(javaType2SqlTypes(entry.getValue()));
                    sbb.append("},");

                    sbs.append(currentFieldColumnNameMap.get(fieName));
                    sbs.append(",");
                /*}*/
            }
        }

        if(sbb.lastIndexOf(",") != -1) sbb.deleteCharAt(sbb.lastIndexOf(","));

        if(sbs.lastIndexOf(",") != -1) sbs.deleteCharAt(sbs.lastIndexOf(","));

        String[] result = {sbb.toString(), sbs.toString()};///////一个是插入语句的 字段名 一个是动态值

        return result;
    }


    /**
     * 生成插入的sql语句时 要把动态部分缓存起 批量
     * @param currentColumnFieldNameMap 参数
     * @param currentFieldTypeNameMap 参数
     * @param t 参数
     * @param paramColumn 参数
     * @param primaryKeyColumn 参数
     * @param myCatSequence 参数
     * @return 返回值类型
     */
    public static String concatInsertDynamicSqlForBatch(Map<String, String> currentColumnFieldNameMap, Map<String, String> currentFieldTypeNameMap, Object t, String paramColumn, String primaryKeyColumn, String myCatSequence) {

        boolean myCatFlag = MedusaCommonUtils.isNotBlank(myCatSequence);//modify by neo on 2019.08.07 for mycat

        List<Object> obs = t instanceof List ? (ArrayList)t : new ArrayList<>();

        if(obs.size() == 0) throw new MedusaException("Medusa: insertBatch method parameter is null or empty!");

        String[] columnArr = paramColumn.split(",");

        int contentLength = obs.size() * columnArr.length * 50;

        StringBuilder sbb = new StringBuilder(contentLength);

        int len = obs.size(), i = 0;
        for (; i < len; i++) {

            sbb.append("(");

            for(String col : columnArr) {//同一个hashset 遍历的元素顺序是否一样的

                String fieName = currentColumnFieldNameMap.get(col);

                if(MedusaCommonUtils.isBlank(fieName)) throw new MedusaException("Medusa: The insertBatch method failed. It might be a field spelling error!");

                if (myCatFlag && col.trim().equalsIgnoreCase(primaryKeyColumn)) {//modify by neo on 2019.08.07 for mycat
                    sbb.append("next value for ").append(myCatSequence).append(",");
                } else {
                    sbb.append("#{param1[");
                    sbb.append(i);
                    sbb.append("].");
                    sbb.append(fieName);
                    sbb.append(", jdbcType=");
                    sbb.append(MedusaSqlHelper.javaType2SqlTypes(currentFieldTypeNameMap.get(fieName)));
                    sbb.append("},");
                }
            }

            if(sbb.lastIndexOf(",") != -1) sbb.deleteCharAt(sbb.lastIndexOf(","));
            sbb.append("),");
        }

        if(sbb.lastIndexOf(",") != -1) sbb.deleteCharAt(sbb.lastIndexOf(","));


        return sbb.toString();
    }


    /**
     * @deprecated
     * 提供给批量更新使用
     * 前一部分sql
     * @param t 参数
     * @param paramColumn 参数
     * @param currentColumnFieldNameMap 参数
     * @return 返回值类型
     */
    public static String concatUpdateDynamicSqlValuesForBatch(Object t, String paramColumn, Map<String, String> currentColumnFieldNameMap) {

        List<Object> obs = t instanceof List ? (ArrayList)t : new ArrayList<>();

        if(obs.size() == 0) throw new MedusaException("Medusa: updateBatch method parameter is null or empty!");

        String[] columnArr = paramColumn.split(",");

        StringBuilder sbb = new StringBuilder(512);

        int len = obs.size(), i = 0;
        for (; i < len; i++) {
            sbb.append("(");
            for(String columns : columnArr) {

                sbb.append("#{pobj.param1[");
                sbb.append(i);
                sbb.append("].");
                sbb.append(currentColumnFieldNameMap.get(columns));
                sbb.append("},");
            }

            if(sbb.lastIndexOf(",") != -1) sbb.deleteCharAt(sbb.lastIndexOf(","));
            sbb.append("),");
        }

        if(sbb.lastIndexOf(",") != -1) sbb.deleteCharAt(sbb.lastIndexOf(","));
        return sbb.toString();
    }

    /**
     * @deprecated
     * 提供给批量更新使用
     * 后一部分sql
     * @param paramColumn     参数
     * @return 返回值类型
     */
    public static String concatUpdateDynamicSqlValuesStrForBatch(String paramColumn) {

        String[] columnArr = paramColumn.split(",");

        StringBuilder sbb = new StringBuilder(512);

        for(String columns : columnArr) {
            if(!columns.equals(SystemConfigs.PRIMARY_KEY)) {
                sbb.append(columns).append("=values(").append(columns).append("),");
            }
        }

        if(sbb.lastIndexOf(",") != -1) sbb.deleteCharAt(sbb.lastIndexOf(","));

        return sbb.toString();
    }

    /**
     * 提供给批量更新使用
     * 使用update方式批量更新
     * 为解决insert into ... on duplicate key update引起的not null列需要default values
     * @param tableName 参数
     * @param t 参数
     * @param paramColumn 参数
     * @param currentColumnFieldNameMap 参数
     * @param primaryKeyColumn 参数
     * @return 返回值类型
     */
    public static String concatUpdateDynamicSqlValuesForBatchPre(String tableName, Object t, String paramColumn, Map<String, String> currentColumnFieldNameMap, String primaryKeyColumn) {

        /*UPDATE users SET
                name = CASE id
        WHEN 7 THEN '7nn'
        WHEN 8 THEN '8nn'
        WHEN 9 THEN '9nn'
        END,
                home_no = CASE id
        WHEN 7 THEN 'New Title 1'
        WHEN 8 THEN 'New Title 2'
        WHEN 9 THEN 'New Title 3'
        END
        WHERE id IN (7,8,9)*/

        List<Object> obs = t instanceof List ? (ArrayList)t : new ArrayList<>();

        if(obs.size() == 0) throw new MedusaException("Medusa: The list param is null or empty");

        String[] columnArr = paramColumn.split(",");

        int contentLength = columnArr.length * (30 + obs.size() * 70), iddLength = obs.size() * 30;

        StringBuilder sbb = new StringBuilder(contentLength), sbbIdd = new StringBuilder(iddLength);

        sbb.append("UPDATE ").append(tableName).append(" SET ");

        boolean flag = true;///只让sbbIdd 记录一次循环id值

        int len = obs.size();
        for(String columns : columnArr) {

            if(!columns.equals(primaryKeyColumn)) {

                if (!checkIsAllNullColumn(obs, columns, currentColumnFieldNameMap)) {//modify by neo on 2019.07.05

                    sbb.append(columns).append(" = CASE ").append(primaryKeyColumn);

                    for (int i = 0; i < len; i++) {

                        if (flag) sbbIdd.append("#{param1[").append(i).append("].").append(currentColumnFieldNameMap.get(primaryKeyColumn)).append("},");

                        sbb.append(" WHEN ")
                                .append("#{param1[").append(i).append("].").append(currentColumnFieldNameMap.get(primaryKeyColumn)).append("}")
                                .append(" THEN ")
                                .append("#{param1[").append(i).append("].").append(currentColumnFieldNameMap.get(columns)).append("}");
                    }

                    if (flag && sbbIdd.lastIndexOf(",") != -1) sbbIdd.deleteCharAt(sbbIdd.lastIndexOf(","));

                    flag = false;

                    sbb.append(" END, ");
                }
            }
        }

        if(sbb.indexOf("param1") == -1) throw new MedusaException("The entity attribute values are all null!");///modify by neo on 2019.07.05

        if(sbb.lastIndexOf(",") != -1) sbb.deleteCharAt(sbb.lastIndexOf(","));

        sbb.append(" WHERE ").append(primaryKeyColumn).append(" IN (").append(sbbIdd).append(")");

        return sbb.toString();
    }

    /**
     * 这种批量更新的方法不能完全解决各实体类里各个字段空时不更新
     * 只能做到所有实体类的某属性值全都为null才会不更新
     * 因为sql语句的限制
     * @param obs 参数
     * @param columns 参数
     * @param currentColumnFieldNameMap 参数
     * @return 返回值类型
     */
    private static boolean checkIsAllNullColumn(List<Object> obs, String columns, Map<String, String> currentColumnFieldNameMap) {
        boolean result = true;
        int i = 0;
        for(Object s : obs) {
            Object k = MedusaReflectionUtils.obtainFieldValue(s, currentColumnFieldNameMap.get(columns));
            if(k != null) i++;
        }

        if(i == obs.size()) {
            return false;
        } else if(i < obs.size() && i > 0) {
            logger.warn("Medusa: Because of the column [{}] attribute values are not uniform in batch updates, some column attributes are updated to null.", columns);
            return false;
        } else {
          return result;
        }
    }


    public static String javaType2SqlTypes(String javaType) {
        if (javaType.equals("Boolean")) {
            return "BIT";
        } else if (javaType.equals("Byte")) {
            return "TINYINT";
        } else if (javaType.equals("Short")) {
            return "SMALLINT";
        } else if (javaType.equals("Integer")) {
            return "INTEGER";
        } else if (javaType.equals("Long")) {
            return "BIGINT";
        } else if (javaType.equals("Float")) {
            return "FLOAT";
        } else if (javaType.equals("Double")) {
            return "DOUBLE";
        } else if (javaType.equals("BigDecimal")) {
            return "DECIMAL";
        } else if (javaType.equals("String")) {
            return "VARCHAR";
        } else if (javaType.equals("Date")) {
            return "TIMESTAMP";
        } else if (javaType.equals("Blob")) {
            return "IMAGE";
        } else if (javaType.equals("Clob")) {
            return "TEXT";
        } else {
            throw new MedusaException("Medusa: The java type no one can match");
        }
    }
}
