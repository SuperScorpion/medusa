package com.jy.medusa.gaze.stuff;


import com.jy.medusa.gaze.stuff.exception.MedusaException;
import com.jy.medusa.gaze.stuff.param.*;
import com.jy.medusa.gaze.stuff.param.gele.*;
import com.jy.medusa.gaze.utils.MyCommonUtils;
import com.jy.medusa.gaze.utils.MyDateUtils;
import com.jy.medusa.gaze.utils.MyReflectionUtils;
import com.jy.medusa.gaze.utils.SystemConfigs;
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
    private Class<?> entityClass;


    public MySqlGenerator(Map<String, String> cfMap, Map<String, String> ftMap, String tableName, String pkName, Class<?> entityClass) {
        this.columns = cfMap.keySet();
        this.tableName = tableName;
        this.pkName = pkName;
        this.columnsStr = MyCommonUtils.join(this.columns, ",");
        this.currentColumnFieldNameMap = cfMap;
        this.currentFieldColumnNameMap = MyHelper.exchangeKeyValues(cfMap);
        this.currentFieldTypeNameMap = ftMap;///modify by neo on 2016.12.15

        this.entityClass = entityClass;
    }

    /**
     * for mycat
     * 生成根据IDs批量新增的SQL not selective
     * @param t 参数
     * @param ps 参数
     * @return 返回值类型
     */
    public String sqlOfInsertBatchForMyCat(Object t, Object mycatSeq, Object... ps) {

        String paramColumn = reSolveColumn(ps);

        if(paramColumn.equals("*")) paramColumn = columnsStr;

        String dynamicSqlForBatch = MyHelper.concatInsertDynamicSqlForBatch(currentColumnFieldNameMap, currentFieldTypeNameMap, t, paramColumn, String.valueOf(mycatSeq));

        int sbbLength = paramColumn.length() + tableName.length() + dynamicSqlForBatch.length() + 33;

        StringBuilder sqlBuild = new StringBuilder(sbbLength);

        sqlBuild.append("INSERT INTO ").append(tableName).append("(")
                .append(paramColumn).append(") values ")
                .append(dynamicSqlForBatch);

        String sql = sqlBuild.toString();

        logger.debug("Medusa: Generated SQL ^_^ " + sql);

        return sql;
    }

    /**
     * 生成根据IDs批量新增的SQL not selective
     * @param t 参数
     * @param ps 参数
     * @return 返回值类型
     */
    public String sqlOfInsertBatch(Object t, Object... ps) {

        String paramColumn = reSolveColumn(ps);

        if(paramColumn.equals("*")) paramColumn = columnsStr;

        String dynamicSqlForBatch = MyHelper.concatInsertDynamicSqlForBatch(currentColumnFieldNameMap, currentFieldTypeNameMap, t, paramColumn, null);

        int sbbLength = paramColumn.length() + tableName.length() + dynamicSqlForBatch.length() + 33;

        StringBuilder sqlBuild = new StringBuilder(sbbLength);

        sqlBuild.append("INSERT INTO ").append(tableName).append("(")
                .append(paramColumn).append(") values ")
                .append(dynamicSqlForBatch);//modify by neo on 2016.11.13

        String sql = sqlBuild.toString();

        logger.debug("Medusa: Generated SQL ^_^ " + sql);

        return sql;
    }

    /**
     * 生成新增的SQL not selective
     * @return 返回值类型
     */
    public String sqlOfInsert() {//modify by neo on 2016.11.12 Object t
//        List<Object> values = obtainFieldValues(t);// modify by neo on 2016.11.15

        String[] paraArray = MyHelper.concatInsertDynamicSql(currentFieldTypeNameMap, currentFieldColumnNameMap, null);
        String insertColumn = paraArray[1], insertDynamicSql = paraArray[0];

        int sbbLength = insertColumn.length() + tableName.length() + insertDynamicSql.length() + 33;

        StringBuilder sqlBuild = new StringBuilder(sbbLength);
        sqlBuild.append("INSERT INTO ").append(tableName).append("(")
                .append(insertColumn).append(") values (")
                .append(insertDynamicSql).append(")");//modify by neo on2016.11.12
        String sql = sqlBuild.toString();

        logger.debug("Medusa: Generated SQL ^_^ " + sql);

        return sql;
    }


    /**
     * 生成新增的SQL
     * @return 返回值类型
     * @param t 参数
     */
    public String sqlOfInsertSelective(Object t) {//modify by neo on 20170117

        if(t == null) throw new MedusaException("Medusa: The entity param is null");

        String[] dynamicSqlForSelective = MyHelper.concatInsertDynamicSql(currentFieldTypeNameMap, currentFieldColumnNameMap, t);
        String insertColumn = dynamicSqlForSelective[1], insertDynamicSql = dynamicSqlForSelective[0];

        int sbbLength = insertColumn.length() + tableName.length() + insertDynamicSql.length() + 33;

        StringBuilder sqlBuild = new StringBuilder(sbbLength);
        sqlBuild.append("INSERT INTO ").append(tableName).append("(")
                .append(insertColumn).append(") values (")
                .append(insertDynamicSql).append(")");//modify by neo on2016.11.12
        String sql = sqlBuild.toString();

        logger.debug("Medusa: Generated SQL ^_^ " + sql);

        return sql;
    }

    /**
     * @deprecated
     * 提供给生成新增SQL 使用
     * @return 返回值类型
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
     * @return 返回值类型
     */
    private Object handleValue(Object value) {
        if (value instanceof String) {
            value = "\'" + value + "\'";
        } else if (value instanceof Date) {
            Date date = (Date) value;
            value = "\'" + MyDateUtils.convertDateToStr(date, SystemConfigs.DATE_FULL_STR) + "\'";
            //value = "TO_TIMESTAMP('" + dateStr + "','YYYY-MM-DD HH24:MI:SS.FF3')";
        } else if (value instanceof Boolean) {
            Boolean v = (Boolean) value;
            value = v ? 1 : 0;//TODO true 1 false 0
        }else if(null == value || MyCommonUtils.isBlank(value.toString())){
            value = "null";
        }
        return value;
    }

    /**
     * 生成根据ID删除的SQL
     * @return 返回值类型
     */
    public String sqlOfRemoveById() {//modify by neo on 2016.11.13 Object id

        //if(id == null) id = 0;//modify by neo on 2016.11.04

        int len = 30 + tableName.length() + pkName.length();

        StringBuilder sqlBuild = new StringBuilder(len);
        sqlBuild.append("DELETE FROM ").append(tableName)
                .append(" WHERE ").append(pkName).append(" = ").append("#{pobj}");//modify by neo on 2016.11.12

        String sql = sqlBuild.toString();

        logger.debug("Medusa: Generated SQL ^_^ " + sql);

        return sql;
    }

    /**
     * 生成根据IDs批量删除的SQL
     * @param t 参数
     * @return 返回值类型
     */
    public String sqlOfRemoveOfBatch(Object t) {

//        Boolean b = List.class.isAssignableFrom(t.getClass());

        List<Object> ids = t instanceof List ? (ArrayList)t : new ArrayList<>();

        /*List<Object> ids;
        if(t instanceof List)
            ids = (ArrayList)t;
        else
            return "";*/

        int l = ids.size(), i = 0;

        int len = 30 + tableName.length() + pkName.length() + l * 9;

        StringBuilder sqlBuild = new StringBuilder(len);
        sqlBuild.append("DELETE FROM ").append(tableName)
                .append(" WHERE ").append(pkName).append(" IN ( 0 ");

        for (; i < l; i++) {
            sqlBuild.append(",").append(ids.get(i));
            /*if (i > 0 && i % (AssConstant.DELETE_CRITICAL_VAL - 1) == 0) {
                sqlBuild.append(")").append(" OR ").append(pkName)
                        .append(" IN ( 0 ");
            }*/
        }
        sqlBuild.append(")");

        String sql = sqlBuild.toString();

        logger.debug("Medusa: Generated SQL ^_^ " + sql);

        return sql;
    }

    /**
     * 根据条件来删除
     * @param t 参数
     * @return 返回值类型
     */
    public String sqlOfRemoveByCondition(Object t) {

        List<String> values = obtainColumnValuesForDeleteByCondition(t);

        int valuesLen = (values == null || values.isEmpty()) ? 0 : values.size();

        int len = 30 + tableName.length() + (valuesLen * 36);

        StringBuilder sqlBuild = new StringBuilder(len);
        sqlBuild.append("DELETE FROM ").append(tableName).append(" WHERE ");

        if (values == null || values.isEmpty()) {
            sqlBuild.append("1!=1");//modify by neo on 2017 07
        } else {
            sqlBuild.append(MyCommonUtils.join(values, " AND "));
        }

        String sql = sqlBuild.toString();
        logger.debug("Medusa: Generated SQL ^_^ " + sql);
        return sql;
    }

    /**
     * 提供给
     * sqlOfRemoveByCondition
     * @param t 参数
     * @return 返回值类型
     */
    private List<String> obtainColumnValuesForDeleteByCondition(Object t) {

        if(t == null) throw new MedusaException("Medusa: The entity param is null");

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
     * 生成根据条件批量更新的语句
     * @param t 参数
     * @param ps 参数
     * @return 返回值类型
     */
    public String sqlOfModifyOfBatch(Object t, Object... ps) {

        String paramColumn = reSolveColumn(ps);

        if(paramColumn.equals("*")) paramColumn = columnsStr;

        boolean notContainPkName = !paramColumn.contains("," + pkName) || !paramColumn.startsWith(pkName + ",");

        if(paramColumn != columnsStr && notContainPkName) paramColumn = pkName + "," + paramColumn;/////modify by neo on 2017.04.20

        String dynamicSqlForBatch = MyHelper.concatUpdateDynamicSqlValuesForBatchPre(tableName, t, paramColumn, currentColumnFieldNameMap);

        logger.debug("Medusa: Generated SQL ^_^ " + dynamicSqlForBatch);

        return dynamicSqlForBatch;
    }

    /**
     * Selective
     * 生成更新的SQL
     * 不允许空值
     * @param t 参数
     * @return 返回值类型
     */
    public String sqlOfModify(Object t) {

        List<String> values = obtainColumnValusForModify(t);
        //Object id = MyReflectionUtils.obtainFieldValue(t, currentColumnFieldNameMap.get(pkName));

        //if(id == null) throw new MedusaException("Medusa: Update method incoming primary key value is null (selective)");//modify by neo on 2016.11.04

        //id = handleValue(id);///这是为了处理id不为 int 变成 string 时 modify by neo on 2016.11.2

        if(values == null || values.isEmpty()) throw new MedusaException("Medusa: There is nothing to update");

        int len = 30 + tableName.length() + (pkName.length() << 1) + (values.size() * 30);

        StringBuilder sqlBuild = new StringBuilder(len);
        sqlBuild.append("UPDATE ").append(tableName).append(" SET ")
                .append(MyCommonUtils.join(values, ",")).append(" WHERE ")
                .append(pkName).append(" = ").append("#{pobj." + pkName + "}");

        String sql = sqlBuild.toString();

        logger.debug("Medusa: Generated SQL ^_^ " + sql);

        return sql;
    }

    /**
     * 提供给生成更新SQL使用 不能为空值
     * @param t 参数
     * @return 返回值类型
     */
    private List<String>  obtainColumnValusForModify(Object t) {

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
     * not Selective
     * 生成更新的SQL
     * @param t 参数
     * @param ps 参数
     * @return 返回值类型
     */
    public String sqlOfModifyNull(Object t, Object... ps) {

        String paramColumn = reSolveColumn(ps);

        List<String> values = obtainColumnValsForModifyNull(Arrays.asList(paramColumn.split(",")));

        if(values == null || values.isEmpty()) return "";

        int len = 39 + tableName.length() + (pkName.length() << 1) + (values.size() * 30);

        StringBuilder sqlBuild = new StringBuilder(len);
        sqlBuild.append("UPDATE ").append(tableName).append(" SET ")
                .append(MyCommonUtils.join(values, ",")).append(" WHERE ")
                .append(pkName).append(" = ").append("#{pobj.param1." + pkName + "}");

        String sql = sqlBuild.toString();

        logger.debug("Medusa: Generated SQL ^_^ " + sql);

        return sql;
    }

    /**
     * 提供给生成更新SQL使用
     * 允许为空
     * @return 返回值类型
     */
    private List<String> obtainColumnValsForModifyNull(List<Object> parList) {
        List<String> colVals = new ArrayList<>();
        for (String column : columns) {
//            Object value = MyReflectionUtils.obtainFieldValue(t, fieldName);
            if (!column.equalsIgnoreCase(pkName) && parList.contains(column)) {
                String fieldName = currentColumnFieldNameMap.get(column);//modify by neo on 2016.11.13
//                colVals.add(column + "=" + handleValue(value));
                colVals.add(column + "=" + "#{pobj.param1." + fieldName +"}");
            }
        }

        if(parList.size() != columns.size() && parList.size() != colVals.size()) throw new MedusaException("Medusa: The update method failed. It might be a field spelling error!");

        return colVals;
    }



    /**
     * 根据条件只查出一条符合的数据
     * @param t 参数
     * @param ps  参数
     * @return 返回值类型
     */
//    return "SELECT * FROM  users WHERE NAME = #{pobj.param1.name} limit 0,1";
    public String sqlOfFindOne(Object t, Object... ps) {

        String paramColumn = reSolveColumn(ps);

        List<String> values = obtainColumnValusForSelectList(t);

        int valuesLen = values == null || values.isEmpty() ? 0 : values.size();

        int len = 39 + tableName.length() + paramColumn.length() + (valuesLen * 39);

        StringBuilder sqlBuild = new StringBuilder(len);

        sqlBuild.append("SELECT ").append(paramColumn).append(" FROM ").append(tableName).append(" WHERE ");

        if (values == null || values.isEmpty()) {
            sqlBuild.append("1=1");
        } else {
            sqlBuild.append(MyCommonUtils.join(values, " AND "));
        }

        sqlBuild.append(" limit 0,1");

        String sql = sqlBuild.toString();

        logger.debug("Medusa: Generated SQL ^_^ " + sql);

        return sql;
    }

    /**
     * 生成根据ID查询的SQL
     * @param id 参数
     * @param ps 参数
     * @return 返回值类型
     */
    public String sqlOfFindOneById(Object id, Object... ps) {///modify by neo on 2016.11.21 Object id,这个 id 不能去掉的

        String paramColumn = reSolveColumn(ps);

        int len = 39 + tableName.length() + pkName.length() + paramColumn.length();

        StringBuilder sqlBuild = new StringBuilder(len);
        sqlBuild.append("SELECT ").append(paramColumn).append(" FROM ").append(tableName).append(" WHERE " + pkName + " = " + "#{pobj.param1}");

        String sql = sqlBuild.toString();

        logger.debug("Medusa: Generated SQL ^_^ " + sql);

        return sql;
    }

    /**
     * 生成根据IDs批量查询的SQL
     * @param t 参数
     * @param ps 参数
     * @return 返回值类型
     */
    public String sqlOfFindBatchOfIds(Object t, Object... ps) {

        List<Object> ids = t instanceof List ? (ArrayList)t : new ArrayList<>();

        String paramColumn = reSolveColumn(ps);

        int l = ids.size(), i = 0;

        int len = 30 + paramColumn.length() + tableName.length() + pkName.length() + l * 9;

        StringBuilder sqlBuild = new StringBuilder(len);
        sqlBuild.append("SELECT ").append(paramColumn).append(" FROM ").append(tableName).append(" WHERE ")
                .append(pkName).append(" in (");

        for (; i < l; i++) {
            sqlBuild.append(ids.get(i)).append(",");
        }

        sqlBuild.append(")");

        if(sqlBuild.lastIndexOf(",") != -1) sqlBuild.deleteCharAt(sqlBuild.lastIndexOf(","));

        String sql = sqlBuild.toString();

        logger.debug("Medusa: Generated SQL ^_^ " + sql);

        return sql;
    }

    public String sqlOfFindListBy(Object t, Object... ps) {

        String paramColumn = reSolveColumn(ps);

        List<String> values = obtainColumnValusForSelectList(t);
//        if(values == null || valudes.isEmpty()) return null;

        int valuesLen = values == null || values.isEmpty() ? 0 : values.size();

        int len = 30 + tableName.length() + paramColumn.length() + (valuesLen * 39);

        StringBuilder sqlBuild = new StringBuilder(len);
        sqlBuild.append("SELECT ").append(paramColumn).append(" FROM ").append(tableName).append(" WHERE ");

        if (values == null || values.isEmpty()) {
            sqlBuild.append("1=1");
        } else {
            sqlBuild.append(MyCommonUtils.join(values, " AND "));
        }

        String sql = sqlBuild.toString();

        logger.debug("Medusa: Generated SQL ^_^ " + sql);

        return sql;
    }

    /**
     * 提供给selectList使用的
     * @param t 参数
     * @return 返回值类型
     */
    private List<String> obtainColumnValusForSelectList(Object t) {

        if(t == null || t instanceof Object[]) return null;//modify by neo on 2017.07.02 解决protostuff 序列化数组问题

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
     * 处理传入的字段字符
     * @param ps        参数
     * @return 返回值类型
     */
    private String reSolveColumn(Object... ps){

        boolean isValidColumn = (ps == null || ps.length != 1 || ps[0] == null || ((Object[])ps[0]).length == 0);

        return isValidColumn ? columnsStr : MyHelper.buildColumnNameForSelect((Object[])ps[0], currentFieldColumnNameMap);
    }

    /**
     * 生成查询所有的SQL
     * @return 返回值类型
     */
    public String sqlOfFindAll(Object[] objParams) {

        String paramColumn = (objParams == null || objParams.length == 0) ? columnsStr : MyHelper.buildColumnNameForSelect(objParams, currentFieldColumnNameMap);

        int len = 20 + tableName.length() + paramColumn.length();

        StringBuilder sqlBuild = new StringBuilder(len);
        sqlBuild.append("SELECT ").append(paramColumn).append(" FROM ").append(tableName);
        String sql = sqlBuild.toString();

        logger.debug("Medusa: Generated SQL ^_^ " + sql);

        return sql;
    }

    /**
     * 生成查询数量的SQL
     * @param objParams 参数
     * @return 返回值类型
     */
    public String sqlOfFindAllCount(Object[] objParams) {

        // 从缓存里拿到分页查询语句 必须清理掉缓存
        String cacheSq = MyHelper.myThreadLocal.get();
        if (MyCommonUtils.isNotBlank(cacheSq)) {
            MyHelper.myThreadLocal.remove();
            logger.debug("Medusa: Successfully cleared the query page in the cache");
            String countSq = cacheSq.replaceAll("SELECT\\b.*\\bFROM", "SELECT COUNT(1) FROM").replaceAll("\\border by\\b.*", "");
            logger.debug("Medusa: Successfully returns the count sql of pages in the cache ^_^ " + countSq);
            return countSq;
        }

        List<String> values = obtainMedusaGazeS(objParams);
//        if(values == null || values.isEmpty()) return null;

        int valuesLen = values == null || values.isEmpty() ? 0 : values.size();

        int len = 30 + tableName.length() + (valuesLen * 39) + 512;

        StringBuilder sbb = new StringBuilder(len);
        sbb.append("SELECT COUNT(1) ").append(" FROM ").append(tableName).append(" WHERE ");

        if (values == null || values.isEmpty()) {
            sbb.append("1=1");
        } else {
            sbb.append(MyCommonUtils.join(values, " AND "));
        }

        //多条件查询
        if(objParams != null && objParams.length > 0) {

            short isd = 0;

            for (Object z : objParams) {///先遍历条件like between类

                if(z instanceof MyRestrictions) {//modify by neo on 2016.12.09  if(z instanceof BaseParam) {

                    short v = 0;

                    List<BaseParam> paramList = ((MyRestrictions) z).getParamList();

                    if(paramList != null && !paramList.isEmpty()) {

                        for (BaseParam x : paramList) {

                            baseParamHandler(sbb, x, isd, v);
                            v++;
                        }
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
     * super me
     * find sql to build a multifunctional method
     * 通过多条件查询 like between 用and连接 查询出交集
     * @param objParams 参数
     * @return 返回值类型
     **/
    public String sqlOfFindMedusaGaze(Object[] objParams) {//modify by neo on 2016.12.23

        //获取到缓存中的分页查询语句 modify by neo on 2016.11.16
        ///分页时先执行查询分页再执行查询分页 再执行总计数句 boundsql(因为)
        String cacheSq = MyHelper.myThreadLocal.get();
        if (MyCommonUtils.isNotBlank(cacheSq)) {
            logger.debug("Medusa: Successfully returns the query page in the cache ^_^ " + cacheSq);
            return cacheSq;
        }

        String paramColumn = (objParams == null || objParams.length == 0) ? columnsStr : MyHelper.buildColumnNameForSelect(objParams, currentFieldColumnNameMap);

        List<String> values = obtainMedusaGazeS(objParams);

        int valuesLen = values == null || values.isEmpty() ? 0 : values.size();

        int len = 30 + tableName.length() + paramColumn.length() + (valuesLen * 39) + 512;

        StringBuilder sbb = new StringBuilder(len);
        sbb.append("SELECT ").append(paramColumn).append(" FROM ").append(tableName).append(" WHERE ");


        if (values == null || values.isEmpty()) {
            sbb.append("1=1");
        } else {
            sbb.append(MyCommonUtils.join(values, " AND "));
        }


        if(objParams != null && objParams.length > 0) {

            Pager pa = null;

            short isd = 0;

            for (Object z : objParams) {///先遍历条件like between类

                if(z instanceof MyRestrictions) {//modify by neo on 2016.12.09  if(z instanceof BaseParam) {

                    short v = 0;

                    List<BaseParam> paramList = ((MyRestrictions) z).getParamList();

                    if(paramList != null && !paramList.isEmpty()) {

                        for (BaseParam x : paramList) {

                            baseParamHandler(sbb, x, isd, v);
                            v++;
                        }
                    }
                } else if (z instanceof Pager) {

                    pa = (Pager) z;//只要最后一个对象 pager
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

                    if(sbb.lastIndexOf(",") != -1) sbb.deleteCharAt(sbb.lastIndexOf(","));//去除最后的一个,
                }

                sbb.append(" limit ");
                sbb.append(pa.getStartRecord());
                sbb.append(",");
                sbb.append(pa.getPageSize());

                //缓存了分页的查询语句
                MyHelper.myThreadLocal.set(sbb.toString());
                logger.debug("Medusa: Successfully saved the page query statement to the cache ^_^ " + sbb.toString());
            }
        }

        logger.debug("Medusa: Generated SQL ^_^ " + sbb.toString());

        return sbb.toString();
    }

    /**
     * 提供给selectMedusaGaze使用的
     * @return 返回值类型
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
                        if (value != null && !value.toString().equals("")) {
                            colVals.add(column + "=" + "#{pobj.array[" + i + "]." + fieldName + "}");///modify by neo on 2016.11.12
                        }
                    }
                    return colVals;
                }

                i++;
            }

            return null;
        } else {
            return null;
        }
    }

    /**
     * modify by neo on 2016.12.09 添加 .paramList[index]
     * isd 为(T entity, Object... param) param的index
     * ind 为MyRestrctions里paramList()的index.
     * @param sbb 参数
     * @param z             参数
     * @param isd           参数
     * @param ind           参数
     */
    public void baseParamHandler(StringBuilder sbb, Object z, short isd, short ind) {

        //转换一下column的属性值 也许是数据库字段 也有可能是属性值
        String column = MyHelper.buildColumnNameForMedusaGaze(((BaseParam) z).getColumn(), currentFieldColumnNameMap);

        if(MyCommonUtils.isBlank(column)) return;

        if (z instanceof BaseComplexParam) {

            if (z instanceof SingleParam) {//modify by neo on 2016.11.17

                Object p = ((SingleParam) z).getValue();

                if(p != null && MyCommonUtils.isNotBlank(p.toString())) {
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
            } else if (z instanceof NotInParam) {

                List p = ((NotInParam) z).getValue();
                Boolean f = ((NotInParam) z).getNotIn();

                if(p != null && p.size() > 0) {
                    sbb.append(" AND ").append(column);

                    if (f) {
                        sbb.append(" NOT IN (");
                    } else {
                        sbb.append(" IN (");
                    }

                    int k = 0;
                    while(k < p.size()) {
                        sbb.append("#{pobj.array[" + isd + "].paramList[" + ind + "].value[" + k + "]},");
                        k += 1;
                    }

                    if(sbb.lastIndexOf(",") != -1) sbb.deleteCharAt(sbb.lastIndexOf(","));
                    sbb.append(")");
                }
            } else if (z instanceof LikeParam) {

                Object p = ((LikeParam) z).getValue();

                if(p != null && MyCommonUtils.isNotBlank(p.toString())) {
                    sbb.append(" AND ").append(column).append(" LIKE ").append("CONCAT('%',#{pobj.array[" + isd + "].paramList[" + ind + "].value},'%')");
                }
            } else if (z instanceof NotNullParam) {

                Boolean p = ((NotNullParam) z).getValue();

                if(p != null) {
                    if (p) {
                        sbb.append(" AND ").append(column).append(" IS NOT NULL ");
                    } else {
                        sbb.append(" AND ").append(column).append(" IS NULL ");
                    }
                }
            }
        } else if (z instanceof BaseGeLeParam) {

            Object p = ((BaseGeLeParam) z).getValue();

            if(p != null) {

                if (z instanceof GreatThanParam) {

                    sbb.append(" AND ").append(column).append(" > ").append("#{pobj.array[" + isd + "].paramList[" + ind + "].value}");
                } else if (z instanceof GreatEqualParam) {

                    sbb.append(" AND ").append(column).append(" >= ").append("#{pobj.array[" + isd + "].paramList[" + ind + "].value}");
                } else if (z instanceof LessThanParam) {

                    sbb.append(" AND ").append(column).append(" < ").append("#{pobj.array[" + isd + "].paramList[" + ind + "].value}");
                } else if (z instanceof LessEqualParam) {

                    sbb.append(" AND ").append(column).append(" <= ").append("#{pobj.array[" + isd + "].paramList[" + ind + "].value");
                }
            }
        }
    }
}

