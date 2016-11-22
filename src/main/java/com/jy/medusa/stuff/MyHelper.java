package com.jy.medusa.stuff;

import com.jy.medusa.generator.MyGenUtils;
import com.jy.medusa.utils.MySqlGenerator;
import com.jy.medusa.utils.SystemConfigs;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.builder.SqlSourceBuilder;
import org.apache.ibatis.builder.annotation.ProviderSqlSource;
import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.executor.ExecutorException;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.ParameterMode;
import org.apache.ibatis.parsing.GenericTokenParser;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.property.PropertyTokenizer;
import org.apache.ibatis.scripting.defaults.DefaultParameterHandler;
import org.apache.ibatis.scripting.xmltags.ForEachSqlNode;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeException;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by neo on 16/9/14.
 */
public class MyHelper {

    private static final Logger logger = LoggerFactory.getLogger(MyHelper.class);

    //private static Map<String, Class<?>> entityClassMap = new HashMap<>();//缓存下来数据

    /**
     * 获取返回值类型 - 实体类型
     * @return
     */
    public static Class<?> getEntityClass(String msid) {

        Class<?> mapperClass = buildClassByPath(msid);

        String sn = mapperClass.getSimpleName();

        Class<?> ps = MyHelperCacheManager.getCacheClass(sn);

        if (ps != null) {
            return ps;
        } else {
            Type[] types = mapperClass.getGenericInterfaces();
            for (Type type : types) {
                if (type instanceof ParameterizedType) {
                    ParameterizedType t = (ParameterizedType) type;
                    if (t.getRawType() == mapperClass || ((Class<?>) t.getRawType()).isAssignableFrom(mapperClass)) {
                        Class<?> returnType = (Class<?>) t.getActualTypeArguments()[0];
                        MyHelperCacheManager.putCacheClass(sn, returnType);
                        logger.debug("成功初始化 " + sn + " 的缓存信息");
                        return returnType;
                    }
                }
            }
        }
        throw new RuntimeException("无法获取Mapper<T>泛型类型:" + sn);
    }

    /**
     * construct class by classpath
     * @param classPath
     * @return
     */
    public static Class<?> buildClassByPath(String classPath) {
        if(classPath != null) {
            try {
                return Class.forName(classPath);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * com.jy.koubei.persistence.UserMapper.SelectOne
     * remove .SelectOne
     * then you can get the UserMapper path to build entity class
     * @return
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
     * @return
     */
    public static String getLastWord(String msid) {

        return msid.substring(msid.lastIndexOf(".") + 1);

    }

    /**
     * check end with-insertSelective
     * @return
     */
    public static boolean checkInsertMethod(String methodName) {
        if(methodName.equals("insertSelective")) {
            return true;
        }
        return false;
    }

    /**
     * check end with updateByPrimaryKey or  updateByPrimaryKeySelective
     * @return
     */
    public static boolean checkUpdateMethod(String methodName) {
        if(methodName.equals("updateByPrimaryKeySelective") || methodName.equals("updateByPrimaryKey")) {
            return true;
        }
        return false;
    }

    /**
     * check page methods
     * @deprecated
     * @return
     */
    public static boolean checkPageMethod(String methodName) {
        if(methodName.equals("selectPage")) {
            return true;
        }
        return false;
    }

    /**
     * check medusas methdss
     * @return
     */
    public static boolean checkMedusaMethod(String methodName) {
        if(methodName.equals("medusaGaze")) {
            return true;
        }
        return false;
    }

//    private static Map<String, MySqlGenerator> generatorMap = new HashMap<>();//缓存下来数据

    public static MySqlGenerator getSqlGenerator(Map<String, Object> m) {

        String p = m.get("msid").toString();

        MySqlGenerator por = MyHelperCacheManager.getCacheGenerator(p);

        if(por != null){
            return por;
        } else {
            Class<?> c = getEntityClass(p);
            MySqlGenerator q = initSqlGenerator(c);
            MyHelperCacheManager.putCacheGenerator(p, q);
            logger.debug("成功缓存初始化 " + c.getSimpleName() + " 的MySqlGenerator基础信息!");
            return q;
        }
    }

    private static MySqlGenerator initSqlGenerator(Class<?> entityClass) {

        if(entityClass == null) {
            throw new RuntimeException("initSqlGenerator 没有实体类型传入!");
        }

        String pkName = SystemConfigs.PRIMARY_KEY;//实体类主键名称
        String tableName;//表名

        Map<String, String> currentColumnFieldNameMap = new HashMap<>();
        Map<String, String> currentFieldTypeNameMap = new HashMap<>();


        Field[] fields = MyReflectCacheManager.getCacheFieldArray(entityClass);////从缓存读取 modify by neo on 2016.11.13

        //获取子类属性上的注解
        String fieldName = null;
        String columnName = null;
        for (Field field : fields) {

            if(field == null) continue;

            fieldName = field.getName();

            Column tableColumn = field.getAnnotation(Column.class);
            if (tableColumn != null) {
                columnName = tableColumn.name();
            } else {
                continue;
            }

            if (field.isAnnotationPresent(Id.class)) pkName = tableColumn.name();

            // 如果未标识特殊的列名，默认取字段名
            columnName = StringUtils.isEmpty(columnName) ? MyGenUtils.camelToUnderline(fieldName) : columnName;

            currentFieldTypeNameMap.put(fieldName, field.getType().getSimpleName());
            currentColumnFieldNameMap.put(columnName, fieldName);
        }

        Table table = entityClass.getAnnotation(Table.class);
        if (table == null) throw new RuntimeException("类- " + entityClass + " 未用@Table注解标识!");
        tableName = table.name();

        return new MySqlGenerator(currentColumnFieldNameMap, currentFieldTypeNameMap, tableName, pkName);
    }


    public static <K, T> Map<T, K> exchangeKeyValues(Map<K, T> map) {

        Map<T, K> resultMap = null;

        if(map != null && !map.isEmpty()) {
            resultMap = new HashMap<T, K>();
            for(K key : map.keySet()) {
                T value = map.get(key);
                //resultMap.remove(key);
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
     * @return
     */
    public static String buildColumnName2(Object[] pssArray, Map<String, String> currentFieldColumnNameMap) {

        StringBuilder sbb = new StringBuilder(100);

        for(Object z : pssArray) {//["a,b,b,n,m","a"] 可选的字段有可能是分开写入的 多参数传入的值

            if(z instanceof String) {

                if (StringUtils.isBlank(z.toString())) continue;

                String[] p = StringUtils.split(z.toString(), ",");

                for (String m : p) {

                    if(StringUtils.isBlank(m)) continue;

//                    if(m.contains("_"))//让用户用的可选字段 属性名字和数据库表字段名称容错
//                        sbb.append(m.trim());
                    if(currentFieldColumnNameMap.get(m.trim()) != null)//modify by neo on 2016.11.19
                        sbb.append(currentFieldColumnNameMap.get(m.trim()));
                    else
                        sbb.append(m.trim());//都取不到时候则

                    sbb.append(",");
                }
            }
        }

        //modify by neo on 2016 10 13
        if(sbb.length() == 0) sbb.append(" * ");///如果后面一个参数都没匹配到string 则会查处所有的字段值
        if(sbb.indexOf(",") != -1) sbb.deleteCharAt(sbb.lastIndexOf(","));//去掉最后一个,
        return sbb.toString();
    }

    /**
     * 用于给medusa gaze使用让对方能够 输入 列名或者属性名字都能转化为列名 容错机制
     * @param ori
     * @param currentFieldColumnNameMap
     * @return
     */
    public static String buildColumnName3(String ori, Map<String, String> currentFieldColumnNameMap) {

        if(StringUtils.isBlank(ori)) return "";

        String result;
        if (ori.contains("_"))//让用户用的可选字段 属性名字和数据库表字段名称容错
            result = ori.trim();
        else if(currentFieldColumnNameMap.get(ori.trim()) != null)//modify by neo on 2016.11.3
            result = currentFieldColumnNameMap.get(ori.trim());
        else
            result = ori.trim();


        return result;
    }

    /**
     * @deprecated
     * @param pss
     * @return
     */
    public static String convertEntityName2SqlName(String pss) {

        String[] p = StringUtils.split(pss, ",");

        StringBuilder sbb = new StringBuilder();

        for(String m : p) {
            sbb.append(MyGenUtils.camelToUnderline(m));
            sbb.append(",");
        }

        sbb.deleteCharAt(sbb.lastIndexOf(","));
        return sbb.toString();
    }


    /**
     * @deprecated
     * generate the get totalPageCount sql for pager and work it
     * use the jdbc to get data for page
     * @param conne
     * @return
     */
    public static int caculatePagerTotalCount(Connection conne, Map<String, Object> p){

        PreparedStatement countStmt = null;
        ResultSet rs = null;

        String countSql = getSqlGenerator(p).sql_findAllCount(((MapperMethod.ParamMap) p.get("pobj")).get("param1"));

        int totalCount = 0;

        try {
            countStmt = conne.prepareStatement(countSql);

            rs = countStmt.executeQuery();

            if (rs.next()) {
                totalCount = rs.getInt(1);
            }

        } catch (SQLException e) {
            logger.error("Neo: Pager分页时查询总记录数出现了异常   " + e);
//            e.printStackTrace();
        } finally {
            try {
                rs.close();
                countStmt.close();
            } catch (SQLException e) {
                logger.error("Neo: Pager分页时查询总记录数连接关闭出现了异常   " + e);
            }
        }

        return totalCount;
    }


    //在interceptori 里面判断方法是不是用户自定义方法
    public static boolean checkMortalMethds(String msidWho) {//modify by neo on 2016.10.25 sq

        return SystemConfigs.MY_ALL_METHOD_NANES_LIST.contains(msidWho) ? true : false;
    }


    /**
     * 生成插入的sql语句时 要把动态部分缓存起
     * @return
     */
    public static String[] concatInsertDynamicSql(Map<String, String> currentFieldTypeNameMap, Map<String, String> currentFieldColumnNameMap) {

        StringBuilder sbb = new StringBuilder(512);

        StringBuilder sbs = new StringBuilder(256);

        for(String fieName : currentFieldTypeNameMap.keySet()) {
            if(fieName.trim().equalsIgnoreCase(SystemConfigs.PRIMARY_KEY)) continue;

            sbb.append("#{pobj.");
            sbb.append(fieName);
            sbb.append(", jdbcType=");
            sbb.append(javaType2SqlTypes(currentFieldTypeNameMap.get(fieName)));
            sbb.append("},");

            sbs.append(currentFieldColumnNameMap.get(fieName));
            sbs.append(",");
        }

        if(sbb.indexOf(",") != -1) sbb.deleteCharAt(sbb.lastIndexOf(","));

        if(sbs.indexOf(",") != -1) sbs.deleteCharAt(sbs.lastIndexOf(","));

        String[] result = {sbb.toString(), sbs.toString()};///////一个是插入语句的 字段名 一个是动态值

        return result;
    }

    private static String javaType2SqlTypes(String javaType) {
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
        }
        return null;
    }


    /**
     * 预编译执行 分页的操作 总数
     * @param conne
     * @param mst
     * @param p
     * @return
     */
    public static int caculatePagerTotalCount(Connection conne, MappedStatement mst, Map<String, Object> p) {

        PreparedStatement countStmt = null;
        ResultSet rs = null;

        int totalCount = 0;

        try {
            BoundSql boundSql = mst.getBoundSql(p);

            String countSql = getSqlGenerator(p).sql_findAllCount(((MapperMethod.ParamMap) p.get("pobj")).get("param1"),
                    ((MapperMethod.ParamMap) p.get("pobj")).get("param2"));


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
                logger.error("Neo: Pager分页时查询总记录数出现了异常   " + e);
                e.printStackTrace();
            } finally {
                try {
                    if(rs != null) rs.close();
                    if(countStmt != null) countStmt.close();
                } catch (SQLException e) {
                    logger.error("Neo: Pager分页时查询总记录数连接关闭出现了异常   " + e);
                    e.printStackTrace();
                }
            }

        return totalCount;
    }

    /**
     * 对SQL参数(?)设值,参考org.apache.ibatis.executor.parameter.DefaultParameterHandler
     * @param ps
     * @param mappedStatement
     * @param boundSql
     * @param parameterObject
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

    //主要是为了解决 分页时 多次生成查询分页语句和总计数的查询语句时 缓存分页的查询语句
    public static ThreadLocal<String> myThreadLocal = new ThreadLocal<>();
}
