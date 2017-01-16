package com.jy.medusa.utils;


import com.jy.medusa.stuff.MyHelper;
import com.jy.medusa.stuff.Pager;
import com.jy.medusa.stuff.exception.MedusaException;
import com.jy.medusa.stuff.param.*;
import com.jy.medusa.stuff.param.gele.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by neo on 16/7/27.
 */
public class MySqlGenerator {

    private static final Logger logger = LoggerFactory.getLogger(MySqlGenerator.class);

    private Set<String>    columns;
    private String        columnsStr;
    private String        tableName;
    private String        pkName;
    private Map<String, String> currentColumnFieldNameMap;
    private Map<String, String> currentFieldColumnNameMap;
    private Map<String, String> currentFieldTypeNameMap;
    private String insertColumn;
    private String insertDynamicSql;
    private Class<?> entityClass;

    public MySqlGenerator(Map<String, String> cfMap, Map<String, String> ftMap, String tableName, String pkName, Class<?> entityClass) {
        this.columns = cfMap.keySet();
        this.tableName = tableName;
        this.pkName = pkName;
        this.columnsStr = MyUtils.join(this.columns, ",");
        this.currentColumnFieldNameMap = cfMap;
        this.currentFieldColumnNameMap = MyHelper.exchangeKeyValues(cfMap);

        String[] parArra = MyHelper.concatInsertDynamicSql(ftMap, currentFieldColumnNameMap);
        insertDynamicSql = parArra[0];
        insertColumn = parArra[1];

        this.currentFieldTypeNameMap = ftMap;///modify by neo on 2016.12.15

        this.entityClass = entityClass;
    }

    /**
     * 生成根据IDs批量删除的SQL
     * @param t
     * @return
     */
    public String sql_insertOfBatch(Object t) {

        String dynamicSqlForBatch = MyHelper.concatInsertDynamicSqlForBatch(currentFieldTypeNameMap, t);

        StringBuilder sql_build = new StringBuilder(512);
        sql_build.append("INSERT INTO ").append(tableName).append("(")
                .append(insertColumn).append(") values ")
                .append(dynamicSqlForBatch);//modify by neo on2016.11.13

        String sql = sql_build.toString();

        logger.debug("Medusa: Generated SQL ^_^ " + sql);

        return sql;
    }

    /**
     * 生成新增的SQL
     * @return
     */
    public String sql_create() {//modify by neo on2016.11.12 Object t
//        List<Object> values = obtainFieldValues(t);// modify by neo on 2016.11.15

        StringBuilder sql_build = new StringBuilder(256);
        sql_build.append("INSERT INTO ").append(tableName).append("(")
                .append(insertColumn).append(") values (")
                .append(insertDynamicSql).append(")");//modify by neo on2016.11.12
        String sql = sql_build.toString();

        logger.debug("Medusa: Generated SQL ^_^ " + sql);

        return sql;
    }

    /**
     * @deprecated
     * 提供给生成新增SQL 使用
     * @return
     */
    private List<Object> obtainFieldValues(Object t) {
        List<Object> values = new ArrayList<>();
        for (String column : columns) {
            Object value = MyReflectionUtils.obtainFieldValue(t, currentColumnFieldNameMap.get(column));
            value = handleValue(value);
            values.add(value);
        }
        return values;
    }

    /**
     * @deprecated
     * 处理value
     * @param value
     * @return
     */
    private Object handleValue(Object value) {
        if (value instanceof String) {
            value = "\'" + value + "\'";
        } else if (value instanceof Date) {
            Date date = (Date) value;
            value = "\'" + MyDateUtils.convertDateToStr(date, MyDateUtils.DATE_FULL_STR) + "\'";
            //value = "TO_TIMESTAMP('" + dateStr + "','YYYY-MM-DD HH24:MI:SS.FF3')";
        } else if (value instanceof Boolean) {
            Boolean v = (Boolean) value;
            value = v ? 1 : 0;//TODO true 1 false 0
        }else if(null == value || MyUtils.isBlank(value.toString())){
            value = "null";
        }
        return value;
    }

    /**
     * 生成根据ID删除的SQL
     * @return
     */
    public String sql_removeById() {//modify by neo on 2016.11.13 Object id

        //if(id == null) id = 0;//modify by neo on 2016.11.04

        StringBuilder sql_build = new StringBuilder(50);
        sql_build.append("DELETE FROM ").append(this.tableName)
                .append(" WHERE ").append(pkName).append(" = ").append("#{pobj}");//modify by neo on 2016.11.12

        String sql = sql_build.toString();

        logger.debug("Medusa: Generated SQL ^_^ " + sql);

        return sql;
    }

    /**
     * 生成根据IDs批量删除的SQL
     * @param t
     * @return
     */
    public String sql_removeOfBatch(Object t) {

//        Boolean b = List.class.isAssignableFrom(t.getClass());
        List<Object> ids;
        if(t != null && t instanceof List)
            ids = (ArrayList)t;
        else
            return "";

        StringBuilder sql_build = new StringBuilder(100);
        sql_build.append("DELETE FROM ").append(this.tableName)
                .append(" WHERE ").append(pkName).append(" IN ( 0 ");
        int len = ids.size(), i = 0;
        for (; i < len; i++) {
            Object id = ids.get(i);
            sql_build.append(",").append(id);
            /*if (i > 0 && i % (AssConstant.DELETE_CRITICAL_VAL - 1) == 0) {
                sql_build.append(")").append(" OR ").append(pkName)
                        .append(" IN ( 0 ");
            }*/
        }
        sql_build.append(")");

        String sql = sql_build.toString();

        logger.debug("Medusa: Generated SQL ^_^ " + sql);

        return sql;
    }

    /**
     * 根据条件来删除
     * @param t
     * @return
     */
    public String sql_removeByCondition(Object t) {

        List<String> values = obtainColumnValuesForDeleteByCondition(t);

        StringBuilder sql_build = new StringBuilder(256);
        sql_build.append("DELETE FROM ").append(this.tableName).append(" WHERE ");

        if(values == null || values.isEmpty())
            sql_build.append("1=1");
        else
            sql_build.append(MyUtils.join(values, " AND "));

        String sql = sql_build.toString();
        logger.debug("Medusa: Generated SQL ^_^ " + sql);
        return sql;
    }

    /**
     * 生成更新的SQL
     * 不允许空置
     * @param t
     * @return
     */
    public String sql_modify(Object t) throws MedusaException {

        List<String> values = obtainColumnValusForModify(t);
        //Object id = MyReflectionUtils.obtainFieldValue(t, currentColumnFieldNameMap.get(pkName));

        //if(id == null) throw new MedusaException("Medusa:  Update method incoming primary key value is null (selective)");//modify by neo on 2016.11.04

        //id = handleValue(id);///这是为了处理id不为 int 变成 string 时 modify by neo on 2016.11.2

        if(values == null || values.isEmpty()) return "";

        StringBuilder sql_build = new StringBuilder(256);
        sql_build.append("UPDATE ").append(tableName).append(" SET ")
                .append(MyUtils.join(values, ",")).append(" WHERE ")
                .append(pkName).append(" = ").append("#{pobj." + pkName + "}");

        String sql = sql_build.toString();

        logger.debug("Medusa: Generated SQL ^_^ " + sql);

        return sql;
    }

    /**
     * 生成更新的SQL
     * @param t
     * @return
     */
    public String sql_modify_null(Object t) throws MedusaException {

        List<String> values = obtainColumnValsForModifyNull(t);
        //Object id = MyReflectionUtils.obtainFieldValue(t, currentColumnFieldNameMap.get(pkName));

        //if(id == null) throw new MedusaException("Medusa:  Update method incoming primary key value is null (selectall)");//modify by neo on 2016.11.04

        //id = handleValue(id);///这是为了处理id不为 int 变成 string 时 modify by neo on 2016.11.2

        if(values == null || values.isEmpty()) return "";

        StringBuilder sql_build = new StringBuilder(256);
        sql_build.append("UPDATE ").append(tableName).append(" SET ")
                .append(MyUtils.join(values, ",")).append(" WHERE ")
                .append(pkName).append(" = ").append("#{pobj." + pkName + "}");

        String sql = sql_build.toString();

        logger.debug("Medusa: Generated SQL ^_^ " + sql);

        return sql;
    }

    /**
     * 提供给生成更新SQL使用
     * 允许为空
     * @param t
     * @return
     */
    private List<String> obtainColumnValsForModifyNull(Object t) {
        List<String> colVals = new ArrayList<>();
        for (String column : columns) {
            String fieldName = currentColumnFieldNameMap.get(column);//modify by neo on 2016.11.13
//            Object value = MyReflectionUtils.obtainFieldValue(t, fieldName);
            if (!column.equalsIgnoreCase(pkName)) {
//                colVals.add(column + "=" + handleValue(value));
                colVals.add(column + "=" + "#{pobj." + fieldName +"}");
            }
        }
        return colVals;
    }

    /**
     * 提供给生成更新SQL使用 不能为空值
     * @param t
     * @return
     */
    private List<String> obtainColumnValusForModify(Object t) {

        if(t == null) return null;

        List<String> colVals = new ArrayList<>();

        for (String column : columns) {

            String fieldName = currentColumnFieldNameMap.get(column);//modify by neo on 2016.11.13

            Object value = MyReflectionUtils.invokeGetterMethod(t, fieldName);/// modify on 2016 11 21 by neo cause:先查询出来对象了 再更新对象的话 会更新到动态代理的 对象类 就会抛出找不到属性的异常问题 改为反射执行get属性方法则可以

//            Object value = MyReflectionUtils.obtainFieldValue(t, fieldName);

            if (value != null && !column.equalsIgnoreCase(pkName)) {
//                colVals.add(column + "=" + handleValue(value));
                colVals.add(column + "=" + "#{pobj." + fieldName + "}");///modify by neo on 2016.11.12
            }
        }
        return colVals;
    }

    /**
     * 提供给selectList使用的
     * @param t
     * @return
     */
    private List<String> obtainColumnValusForSelectList(Object t) {

        if(t == null) return null;

        List<String> colVals = new ArrayList<>();
        for (String column : columns) {
            String fieldName = currentColumnFieldNameMap.get(column);//modify by neo on 2016.11.13
            Object value = MyReflectionUtils.obtainFieldValue(t, fieldName);
            if (value != null) {
//                colVals.add(column + "=" + handleValue(value));
                colVals.add(column + "=" + "#{pobj.param1." + fieldName + "}");///modify by neo on 2016.11.12
            }
        }
        return colVals;
    }


    /**
     * 提供给selectMedusaGaze使用的
     * @return
     */
    private List<String> obtainMedusaGazeS(Object[] psArray) {

        if(psArray != null && psArray.length != 0) {

            short i = 0;
            for(Object o : psArray) {

                if(entityClass.isInstance(o)) {

                    List<String> colVals = new ArrayList<>();
                    for (String column : columns) {
                        String fieldName = currentColumnFieldNameMap.get(column);//modify by neo on 2016.11.13
                        Object value = MyReflectionUtils.obtainFieldValue(o, fieldName);
                        if (value != null) {
                            colVals.add(column + "=" + "#{pobj.array[" + i + "]." + fieldName + "}");///modify by neo on 2016.11.12
                        }
                    }
                    return colVals;
                }

                i++;
            }
        }

        return null;
    }

    /**
     * 提供给
     * sql_removeByCondition
     * @param t
     * @return
     */
    private List<String> obtainColumnValuesForDeleteByCondition(Object t) {

        if(t == null) return null;

        List<String> colVals = new ArrayList<>();
        for (String column : columns) {
            String fieldName = currentColumnFieldNameMap.get(column);//modify by neo on 2016.11.13
            Object value = MyReflectionUtils.obtainFieldValue(t, fieldName);
            if (value != null) {
//                colVals.add(column + "=" + handleValue(value));
                colVals.add(column + "=" + "#{pobj." + fieldName + "}");///modify by neo on 2016.11.12
            }
        }
        return colVals;
    }


    /**
     * 根据条件只查出一条符合的数据
     * @param t
     * @param ps
     * @return
     */
//    return "SELECT * FROM  users WHERE NAME = #{pobj.param1.name} limit 0,1";
    public String sql_findOne(Object t, Object... ps) {

        String paramColumn = (ps == null || ps.length != 1 || ((Object[])ps[0]).length == 0) ? columnsStr : MyHelper.buildColumnName2((Object[])ps[0], currentFieldColumnNameMap);

        StringBuilder sql_build = new StringBuilder(256);

        sql_build.append("SELECT ").append(paramColumn).append(" FROM ").append(tableName).append(" WHERE ");

        List<String> values = obtainColumnValusForSelectList(t);

        if(values == null || values.isEmpty())
            sql_build.append("1=1");
        else
            sql_build.append(MyUtils.join(values, " AND "));

        sql_build.append(" limit 0,1");

        String sql = sql_build.toString();

        logger.debug("Medusa: Generated SQL ^_^ " + sql);

        return sql;
    }



    /**
     * 生成根据ID查询的SQL
     * @return
     */
    public String sql_findOneById(Object id, Object... ps) {///modify by neo on 2016.11.21 Object id,这个 id 不能去掉的

        String paramColumn = (ps == null || ps.length != 1 || ((Object[])ps[0]).length == 0) ? columnsStr : MyHelper.buildColumnName2((Object[])ps[0], currentFieldColumnNameMap);

        StringBuilder sql_build = new StringBuilder(256);
        sql_build.append("SELECT ").append(paramColumn).append(" FROM ").append(tableName).append(" WHERE " + pkName + " = " + "#{pobj.param1}");

        String sql = sql_build.toString();

        logger.debug("Medusa: Generated SQL ^_^ " + sql);

        return sql;
    }

    public String sql_findListBy(Object t, Object... ps) {

        String paramColumn = (ps == null || ps.length != 1 || ((Object[])ps[0]).length == 0) ? columnsStr : MyHelper.buildColumnName2((Object[])ps[0], currentFieldColumnNameMap);

        List<String> values = obtainColumnValusForSelectList(t);
//        if(values == null || valudes.isEmpty()) return null;

        StringBuilder sql_build = new StringBuilder(256);
        sql_build.append("SELECT ").append(paramColumn).append(" FROM ").append(tableName).append(" WHERE ");

        if(values == null || values.isEmpty())
            sql_build.append("1=1");
        else
            sql_build.append(MyUtils.join(values, " AND "));

        String sql = sql_build.toString();

        logger.debug("Medusa: Generated SQL ^_^ " + sql);

        return sql;
    }

    /**
     * 生成查询所有的SQL
     * @return
     */
    public String sql_findAll(Object[] objParams) {

        String paramColumn = (objParams == null || objParams.length == 0) ? columnsStr : MyHelper.buildColumnName2(objParams, currentFieldColumnNameMap);

        StringBuilder sql_build = new StringBuilder(100);
        sql_build.append("SELECT ").append(paramColumn).append(" FROM ").append(this.tableName);
        String sql = sql_build.toString();

        logger.debug("Medusa: Generated SQL ^_^ " + sql);

        return sql;
    }

    /**
     * 生成查询数量的SQL
     * @return
     */
    public String sql_findAllCount(Object[] objParams) {

        // 从缓存里拿到分页查询语句 必须清理掉缓存
        String cacheSq = MyHelper.myThreadLocal.get();
        if(MyUtils.isNotBlank(cacheSq)) {
            MyHelper.myThreadLocal.remove();
            logger.debug("Medusa:  Successfully cleared the query page in the cache");
            String countSq = cacheSq.replaceAll("SELECT\\b.*\\bFROM", "SELECT COUNT(1) FROM").replaceAll("\\border by\\b.*", "");
            logger.debug("Medusa:  Successfully returns the count sql of pages in the cache ^_^ " + countSq);
            return countSq;
        }

        List<String> values = obtainMedusaGazeS(objParams);
//        if(values == null || values.isEmpty()) return null;

        StringBuilder sbb = new StringBuilder(512);
        sbb.append("SELECT COUNT(1) ").append(" FROM ").append(this.tableName).append(" WHERE ");

        if(values == null || values.isEmpty())
            sbb.append("1=1");
        else
            sbb.append(MyUtils.join(values, " AND "));

        //多条件查询
        if(objParams != null && objParams.length > 0) {

            short isd = 0;

            for (Object z : objParams) {///先遍历条件like between类

                if(z instanceof MyRestrictions) {//modify by neo on 2016.12.09  if(z instanceof BaseParam) {

                    short v = 0;

                    for(BaseParam x : ((MyRestrictions) z).getParamList()) {

                        baseParamHandler(sbb, x, isd, v);
                        v++;
                    }
                }

                isd++;
            }
        }

        String sql = sbb.toString();

        logger.debug("Medusa: Generated SQL ^_^ " + sql);

        return sql;
    }


    /**
     * 多字段模糊查询 like
     */
   /* public String sql_searchLike(Object po, Object... ps) {

        List<String> values = obtainColumnValusForSelectList(po);

        if(values == null || values.isEmpty()) return "";

        String paramColumn = (ps == null || ps.length != 1 || ((Object[])ps[0]).length == 0) ? columnsStr : MyHelper.buildColumnName2((Object[])ps[0], currentFieldColumnNameMap);

        StringBuilder sbb = new StringBuilder(256);
        sbb.append("SELECT ").append(paramColumn).append(" FROM ").append(this.tableName).append(" WHERE 1=2 ");

        for(String p : values) {

            if(MyUtils.isBlank(p)) continue;

            String[] k = p.split("=");

            if(k != null && k.length == 2) {
                k[1] = k[1].replace("'", "");//p : name='baba'
                sbb.append(" OR ").append(k[0]).append(" LIKE ").append("'%").append(k[1]).append("%'");
            }
        }

        logger.debug("Medusa: Generated SQL ^_^ " + sql);

        return sbb.toString();
    }*/

    /**
     * 某个时间段内的记录
     * between >= <=
     */
/*    public String sql_betweenTime(Object po, Object... ps) {

        List<String> values = obtainColumnValusForSelectList(po);

        String paramColumn = (ps == null || ps.length != 1 || ((Object[])ps[0]).length == 0) ? columnsStr : MyHelper.buildColumnName2((Object[])ps[0], currentFieldColumnNameMap);

        StringBuilder sbb = new StringBuilder(256);
        sbb.append("SELECT ").append(paramColumn).append(" FROM ").append(tableName).append(" WHERE ");

        if(values == null || values.isEmpty())
            sbb.append("1=1");
        else
            sbb.append(MyUtils.join(values, " AND "));

        for(Object z : (Object[])ps[0]) {
            if(z instanceof BetweenParam) {

                BetweenParam p = (BetweenParam)z;

                sbb.append(" AND ").append(p.getColumn()).append(" BETWEEN ")
                        .append("'").append(MyDateUtils.convertDateToStr(p.getStart(), MyDateUtils.DATE_FULL_STR)).append("'")
                        .append(" AND ")
                        .append("'").append(MyDateUtils.convertDateToStr(p.getEnd(), MyDateUtils.DATE_FULL_STR)).append("'");
            }
        }

        logger.debug("Medusa: Generated SQL ^_^ " + sql);

        return sbb.toString();
    }*/


    /**
     * super me
     * find sql to build a multifunctional method
     * 通过多条件查询 like between 用and连接 查询出交集
     */
    public String sql_findMedusaGaze(Object[] objParams) {//modify by neo on 2016.12.23

        //获取到缓存中的分页查询语句 modify by neo on 2016.11.16
        ///分页时先执行查询分页再执行查询分页 再执行总计数句 boundsql(因为)
        String cacheSq = MyHelper.myThreadLocal.get();
        if(MyUtils.isNotBlank(cacheSq)) {
            logger.debug("Medusa:  Successfully returns the query page in the cache ^_^ " + cacheSq);
            return cacheSq;
        }

        String paramColumn = (objParams == null || objParams.length == 0) ? columnsStr : MyHelper.buildColumnName2(objParams, currentFieldColumnNameMap);

        StringBuilder sbb = new StringBuilder(512);
        sbb.append("SELECT ").append(paramColumn).append(" FROM ").append(tableName).append(" WHERE ");

        List<String> values = obtainMedusaGazeS(objParams);

        if(values == null || values.isEmpty())
            sbb.append("1=1");
        else
            sbb.append(MyUtils.join(values, " AND "));


        if(objParams != null && objParams.length > 0) {

            Pager pa = null;

            short isd = 0;

            for (Object z : objParams) {///先遍历条件like between类

                if(z instanceof MyRestrictions) {//modify by neo on 2016.12.09  if(z instanceof BaseParam) {

                    short v = 0;

                    for(BaseParam x : ((MyRestrictions) z).getParamList()) {

                        baseParamHandler(sbb, x, isd, v);
                        v++;
                    }

                } else if (z instanceof Pager) {

                    pa = (Pager) z;
                }

                isd++;
            }

            ///////分页开始
            if(pa != null) {

                if(pa.getOrderBy() != null && pa.getOrderType() != null && pa.getOrderBy().length > 0 && pa.getOrderBy().length == pa.getOrderType().length) {

                    sbb.append(" order by ");//modify by neo 2016.10.12

                    int i = 0;
                    for(; i < pa.getOrderBy().length ; i++) {

                        sbb.append(pa.getOrderBy()[i]);
                        sbb.append(" ");
                        sbb.append(pa.getOrderType()[i]);
                        sbb.append(",");
                    }

                    sbb.deleteCharAt(sbb.lastIndexOf(","));//去除最后的一个,
                }

                sbb.append(" limit ");
                sbb.append(pa.getStartRecord());
                sbb.append(",");
                sbb.append(pa.getPageSize());

                //缓存了分页的查询语句
                MyHelper.myThreadLocal.set(sbb.toString());
                logger.debug("Medusa:  Successfully saved the page query statement to the cache ^_^ " + sbb.toString());
            }
        }

        logger.debug("Medusa: Generated SQL ^_^ " + sbb.toString());

        return sbb.toString();
    }

    /**
     * modify by neo on 2016.12.09 添加 .paramList[index]
     * isd 为(T entity, Object... param) param的index
     * ind 为MyRestrctions里paramList()的index.
     * @param sbb
     * @param z
     * @param isd
     * @param ind
     */
    public void baseParamHandler(StringBuilder sbb, Object z, short isd, short ind) {

        //转换一下column的属性值 也许是数据库字段 也有可能是属性值
        String column = MyHelper.buildColumnName3(((BaseParam) z).getColumn(), currentFieldColumnNameMap);

        if(MyUtils.isBlank(column)) return;

        if (z instanceof BaseComplexParam) {

            if (z instanceof SingleParam) {//modify by neo on 2016.11.17

                Object p = ((SingleParam) z).getValue();

                if(p != null && MyUtils.isNotBlank(p.toString())) {
                    sbb.append(" AND ").append(column).append(" = ").append("#{pobj.array[" + isd + "].paramList[" + ind + "].value}");
                }

            } else if (z instanceof BetweenParam) {

                Object start = ((BetweenParam) z).getStart();

                if(start != null) {
                    sbb.append(" AND ").append(column).append(" BETWEEN ")
                            //.append("'").append(MyDateUtils.convertDateToStr(p.getEnd(), MyDateUtils.DATE_FULL_STR)).append("'")
                            .append("#{pobj.array[" + isd + "].paramList[" + ind + "].start}")
                            .append(" AND ")
                            .append("#{pobj.array[" + isd + "].paramList[" + ind + "].end}");
                }
            } else if (z instanceof LikeParam) {

                Object p = ((LikeParam) z).getValue();

                if(p != null && MyUtils.isNotBlank(p.toString())) {
                    sbb.append(" AND ").append(column).append(" LIKE ").append("CONCAT('%',#{pobj.array[" + isd + "].paramList[" + ind + "].value},'%')");
                }
            }
        } else if (z instanceof BaseGeLeParam) {

            Object p = ((BaseGeLeParam) z).getValue();

            if(p != null && MyUtils.isNotBlank(p.toString())) {

                if (z instanceof GreatThanParam) {

                    //                            GreatThanParam p = (GreatThanParam) z;

                    sbb.append(" AND ").append(column).append(" > ").append("#{pobj.array[" + isd + "].paramList[" + ind + "].value}");
                } else if (z instanceof GreatEqualParam) {

                    //                            GreatEqualParam p = (GreatEqualParam) z;

                    sbb.append(" AND ").append(column).append(" >= ").append("#{pobj.array[" + isd + "].paramList[" + ind + "].value}");
                } else if (z instanceof LessThanParam) {

                    //                            LessThanParam p = (LessThanParam) z;

                    sbb.append(" AND ").append(column).append(" < ").append("#{pobj.array[" + isd + "].paramList[" + ind + "].value}");
                } else if (z instanceof LessEqualParam) {

                    //                            LessEqualParam p = (LessEqualParam) z;

                    sbb.append(" AND ").append(column).append(" <= ").append("#{pobj.array[" + isd + "].paramList[" + ind + "].value");
                }
            }
        }

    }
}

