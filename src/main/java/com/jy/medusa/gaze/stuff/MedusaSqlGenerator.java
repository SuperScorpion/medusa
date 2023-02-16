package com.jy.medusa.gaze.stuff;


import com.jy.medusa.gaze.stuff.exception.MedusaException;
import com.jy.medusa.gaze.stuff.param.BaseRestrictions;
import com.jy.medusa.gaze.stuff.param.MedusaLambdaMap;
import com.jy.medusa.gaze.stuff.param.MedusaLambdaRestrictions;
import com.jy.medusa.gaze.stuff.param.base.BaseParam;
import com.jy.medusa.gaze.stuff.param.gele.*;
import com.jy.medusa.gaze.stuff.param.mix.*;
import com.jy.medusa.gaze.stuff.param.orand.AndModelClass;
import com.jy.medusa.gaze.stuff.param.orand.BaseModelClass;
import com.jy.medusa.gaze.stuff.param.orand.OrModelClass;
import com.jy.medusa.gaze.stuff.param.sort.BaseSortParam;
import com.jy.medusa.gaze.stuff.param.sort.GroupByParam;
import com.jy.medusa.gaze.stuff.param.sort.OrderByParam;
import com.jy.medusa.gaze.utils.MedusaCommonUtils;
import com.jy.medusa.gaze.utils.MedusaReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by SuperScorpion on 16/7/27.
 */
public class MedusaSqlGenerator {

    private static final Logger logger = LoggerFactory.getLogger(MedusaSqlGenerator.class);

    private Set<String>    columns;
    private String        columnsStr;
    private String        tableName;
    private String        pkName;
    private Map<String, String> currentColumnFieldNameMap;
    private Map<String, String> currentFieldColumnNameMap;
    private Map<String, String> currentFieldTypeNameMap;
    private Class<?> entityClass;


    public MedusaSqlGenerator(Map<String, String> cfMap, Map<String, String> ftMap, String tableName, String pkColumnName, Class<?> entityClass) {
        this.columns = cfMap.keySet();
        this.tableName = tableName;
        this.pkName = pkColumnName;
        this.columnsStr = MedusaCommonUtils.join(this.columns, ",");
        this.currentColumnFieldNameMap = cfMap;
        this.currentFieldColumnNameMap = MedusaSqlHelper.exchangeKeyValues(cfMap);
        this.currentFieldTypeNameMap = ftMap;///modify by SuperScorpion on 2016.12.15

        this.entityClass = entityClass;
    }

    public String getPkColumnName() {
        return this.pkName;
    }

    public String getPkPropertyName() {
        return currentColumnFieldNameMap.get(pkName);
    }

    /**
     * for mycat
     * 生成根据IDs批量新增的SQL not selective
     * @param t 参数
     * @param mycatSeq 参数
     * @param ps 参数
     * @return 返回值类型
     */
    public String sqlOfInsertBatchForMyCat(Object t, Object mycatSeq, Boolean flag, Object[] ps) {

        String paramColumn = reSolveColumn(ps, flag);

        if(paramColumn.equals("*")) paramColumn = columnsStr;

        String dynamicSqlForBatch = MedusaSqlHelper.concatInsertDynamicSqlForBatch(currentColumnFieldNameMap, currentFieldTypeNameMap, t, paramColumn, pkName, String.valueOf(mycatSeq));

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
    public String sqlOfInsertBatch(Object t, Boolean flag, Object[] ps) {

        if(flag != null && (ps == null || ps.length == 0))
            throw new MedusaException("Medusa: If you have isExclude then the batch method need paramColumns");//add by SuperScorpion on 20220927

        String paramColumn = reSolveColumn(ps, flag);

        if(paramColumn.equals("*")) paramColumn = columnsStr;

        String dynamicSqlForBatch = MedusaSqlHelper.concatInsertDynamicSqlForBatch(currentColumnFieldNameMap, currentFieldTypeNameMap, t, paramColumn, pkName, null);

        int sbbLength = paramColumn.length() + tableName.length() + dynamicSqlForBatch.length() + 33;

        StringBuilder sqlBuild = new StringBuilder(sbbLength);

        sqlBuild.append("INSERT INTO ").append(tableName).append("(")
                .append(paramColumn).append(") values ")
                .append(dynamicSqlForBatch);//modify by SuperScorpion on 2016.11.13

        String sql = sqlBuild.toString();

        logger.debug("Medusa: Generated SQL ^_^ " + sql);

        return sql;
    }

    /**
     * 生成新增的SQL not selective
     * @return 返回值类型
     */
    public String sqlOfInsert() {//modify by SuperScorpion on 2016.11.12 Object t

        String[] paraArray = MedusaSqlHelper.concatInsertDynamicSql(currentFieldTypeNameMap, currentFieldColumnNameMap, null, pkName);
        String insertColumn = paraArray[1], insertDynamicSql = paraArray[0];

        int sbbLength = insertColumn.length() + tableName.length() + insertDynamicSql.length() + 33;

        StringBuilder sqlBuild = new StringBuilder(sbbLength);
        sqlBuild.append("INSERT INTO ").append(tableName).append("(")
                .append(insertColumn).append(") values (")
                .append(insertDynamicSql).append(")");//modify by SuperScorpion on2016.11.12
        String sql = sqlBuild.toString();

        logger.debug("Medusa: Generated SQL ^_^ " + sql);

        return sql;
    }


    /**
     * 生成新增的SQL
     * @return 返回值类型
     * @param t 参数
     */
    public String sqlOfInsertSelective(Object t) {//modify by SuperScorpion on 20170117

        if(t == null) throw new MedusaException("Medusa: The entity param is null");

        String[] dynamicSqlForSelective = MedusaSqlHelper.concatInsertDynamicSql(currentFieldTypeNameMap, currentFieldColumnNameMap, t, pkName);
        String insertColumn = dynamicSqlForSelective[1], insertDynamicSql = dynamicSqlForSelective[0];

        int sbbLength = insertColumn.length() + tableName.length() + insertDynamicSql.length() + 33;

        StringBuilder sqlBuild = new StringBuilder(sbbLength);
        sqlBuild.append("INSERT INTO ").append(tableName).append("(")
                .append(insertColumn).append(") values (")
                .append(insertDynamicSql).append(")");//modify by SuperScorpion on2016.11.12
        String sql = sqlBuild.toString();

        logger.debug("Medusa: Generated SQL ^_^ " + sql);

        return sql;
    }

    /**
     * 生成根据ID删除的SQL
     * @return 返回值类型
     */
    public String sqlOfDeleteByPrimaryKey() {//modify by SuperScorpion on 2016.11.13 Object id

        int len = 30 + tableName.length() + pkName.length();

        StringBuilder sqlBuild = new StringBuilder(len);
        sqlBuild.append("DELETE FROM ").append(tableName)
                .append(" WHERE ").append(pkName).append(" = ").append("#{pobj}");//modify by SuperScorpion on 2016.11.12

        String sql = sqlBuild.toString();

        logger.debug("Medusa: Generated SQL ^_^ " + sql);

        return sql;
    }

    /**
     * 生成根据IDs批量删除的SQL
     * @param t 参数
     * @return 返回值类型
     */
    public String sqlOfDeleteBatch(Object t) {

        List<Object> ids = t instanceof List ? (ArrayList)t : new ArrayList<>();

        int l = ids.size(), i = 0;

        int len = 30 + tableName.length() + pkName.length() + l * 9;

        StringBuilder sqlBuild = new StringBuilder(len);
        sqlBuild.append("DELETE FROM ").append(tableName)
                .append(" WHERE ").append(pkName).append(" IN ( 0 ");

        for (; i < l; i++) {
            sqlBuild.append(",").append(ids.get(i));
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
    public String sqlOfDelete(Object t) {

        List<String> values = obtainColumnValuesForDeleteByCondition(t);

        int valuesLen = (values == null || values.isEmpty()) ? 0 : values.size();

        int len = 30 + tableName.length() + (valuesLen * 36);

        StringBuilder sqlBuild = new StringBuilder(len);
        sqlBuild.append("DELETE FROM ").append(tableName).append(" WHERE ");

        if (values == null || values.isEmpty()) {
            sqlBuild.append("1!=1");//modify by SuperScorpion on 2017 07
        } else {
            sqlBuild.append(MedusaCommonUtils.join(values, " AND "));
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

        if(t == null || t instanceof Object[]) return null;//modify by SuperScorpion on 2020.01.01 解决protostuff 序列化数组问题

        List<String> colVals = new ArrayList<>();
        for (String column : columns) {
            String fieldName = currentColumnFieldNameMap.get(column);//modify by SuperScorpion on 2016.11.13
            Object value = MedusaReflectionUtils.obtainFieldValue(t, fieldName);
            if (value != null) {
                colVals.add(column + "=" + "#{pobj." + fieldName + "}");///modify by SuperScorpion on 2016.11.12
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
    public String sqlOfUpdateByPrimaryKeyBatch(Object t, Boolean flag, Object[] ps) {

        if(flag != null && (ps == null || ps.length == 0))
            throw new MedusaException("Medusa: If you have isExclude then the batch method need paramColumns");//add by SuperScorpion on 20220927

        String paramColumn = reSolveColumn(ps, flag);

        if(paramColumn.equals("*")) paramColumn = columnsStr;

        boolean notContainPkName = !paramColumn.contains("," + pkName) || !paramColumn.startsWith(pkName + ",");

        if(paramColumn != columnsStr && notContainPkName) paramColumn = pkName + "," + paramColumn;/////modify by SuperScorpion on 2017.04.20

        String dynamicSqlForBatch = MedusaSqlHelper.concatUpdateDynamicSqlValuesForBatchPre(tableName, t, paramColumn, currentColumnFieldNameMap, pkName);

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
    public String sqlOfUpdateByPrimaryKeySelective(Object t) {

        List<String> values = obtainColumnValusForModify(t);

        if(values == null || values.isEmpty()) throw new MedusaException("Medusa: There is nothing to update");

        int len = 30 + tableName.length() + (pkName.length() << 1) + (values.size() * 30);

        StringBuilder sqlBuild = new StringBuilder(len);
        sqlBuild.append("UPDATE ").append(tableName).append(" SET ")
                .append(MedusaCommonUtils.join(values, ",")).append(" WHERE ")
                .append(pkName).append(" = ").append("#{pobj.").append(currentColumnFieldNameMap.get(pkName)).append("}");///modify by SuperScorpion on 2019.08.20

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

            String fieldName = currentColumnFieldNameMap.get(column);//modify by SuperScorpion on 2016.11.13

            Object value = MedusaReflectionUtils.invokeGetterMethod(t, fieldName);/// modify on 2016 11 21 by SuperScorpion cause:先查询出来对象了 再更新对象的话 会更新到动态代理的 对象类 就会抛出找不到属性的异常问题 改为反射执行get属性方法则可以

            if (value != null && !column.equalsIgnoreCase(pkName)) {
                colVals.add(column + "=" + "#{pobj." + fieldName + "}");///modify by SuperScorpion on 2016.11.12
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
    public String sqlOfUpdateByPrimaryKey(Object t, Object[] ps) {

        String paramColumn = reSolveColumn(ps);

        List<String> values = obtainColumnValsForModifyNull(Arrays.asList(paramColumn.split(",")));

        if(values == null || values.isEmpty()) return "";

        int len = 39 + tableName.length() + (pkName.length() << 1) + (values.size() * 30);

        StringBuilder sqlBuild = new StringBuilder(len);
        sqlBuild.append("UPDATE ").append(tableName).append(" SET ")
                .append(MedusaCommonUtils.join(values, ",")).append(" WHERE ")
                .append(pkName).append(" = ").append("#{param1.").append(currentColumnFieldNameMap.get(pkName)).append("}");///modify by SuperScorpion on 2020.02.13

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
            if (!column.equalsIgnoreCase(pkName) && parList.contains(column)) {
                String fieldName = currentColumnFieldNameMap.get(column);//modify by SuperScorpion on 2016.11.13
                colVals.add(column + "=" + "#{param1." + fieldName +"}");///modify by SuperScorpion on 2020.02.13
            }
        }

        if(parList.size() != columns.size() && parList.size() != colVals.size()) throw new MedusaException("Medusa: The update method failed. It might be a field spelling error!");

        return colVals;
    }



    /**
     * return "SELECT * FROM  users WHERE NAME = #{pobj.param1.name} limit 0,1";
     * 根据条件只查出一条符合的数据
     * @param t 参数
     * @param ps  参数
     * @return 返回值类型
     */
    public String sqlOfSelectOne(Object t, Object[] ps) {

        String paramColumn = reSolveColumn(ps);

        List<String> values = obtainColumnValusForSelectList(t);

        int valuesLen = values == null || values.isEmpty() ? 0 : values.size();

        int len = 39 + tableName.length() + paramColumn.length() + (valuesLen * 39);

        StringBuilder sqlBuild = new StringBuilder(len);

        sqlBuild.append("SELECT ").append(paramColumn).append(" FROM ").append(tableName).append(" WHERE ");

        if (values == null || values.isEmpty()) {
            sqlBuild.append("1=1");
        } else {
            sqlBuild.append(MedusaCommonUtils.join(values, " AND "));
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
    public String sqlOfSelectByPrimaryKey(Object id, Object[] ps) {///modify by SuperScorpion on 2016.11.21 Object id,这个 id 不能去掉的

        String paramColumn = reSolveColumn(ps);

        int len = 39 + tableName.length() + pkName.length() + paramColumn.length();

        StringBuilder sqlBuild = new StringBuilder(len);
        sqlBuild.append("SELECT ").append(paramColumn).append(" FROM ").append(tableName).append(" WHERE ").append(pkName).append(" = ").append("#{param1}");///modify by SuperScorpion on 2020.02.13

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
    public String sqlOfSelectByPrimaryKeyBatch(Object t, Object[] ps) {

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

    /**
     * 根据条件查出多条符合的数据
     * @param t 参数
     * @param ps 参数
     * @return 返回值类型
     */
    public String sqlOfSelect(Object t, Object[] ps) {

        String paramColumn = reSolveColumn(ps);

        List<String> values = obtainColumnValusForSelectList(t);

        int valuesLen = values == null || values.isEmpty() ? 0 : values.size();

        int len = 30 + tableName.length() + paramColumn.length() + (valuesLen * 39);

        StringBuilder sqlBuild = new StringBuilder(len);
        sqlBuild.append("SELECT ").append(paramColumn).append(" FROM ").append(tableName).append(" WHERE ");

        if (values == null || values.isEmpty()) {
            sqlBuild.append("1=1");
        } else {
            sqlBuild.append(MedusaCommonUtils.join(values, " AND "));
        }

        String sql = sqlBuild.toString();

        logger.debug("Medusa: Generated SQL ^_^ " + sql);

        return sql;
    }

    /**
     * 提供给selectList和selectOne使用的
     * @param t 参数
     * @return 返回值类型
     */
    private List<String> obtainColumnValusForSelectList(Object t) {

        if(t == null || t instanceof Object[]) return null;//modify by SuperScorpion on 2017.07.02 解决protostuff 序列化数组问题

        List<String> colVals = new ArrayList<>();
        for (String column : columns) {
            String fieldName = currentColumnFieldNameMap.get(column);//modify by SuperScorpion on 2016.11.13
            Object value = MedusaReflectionUtils.obtainFieldValue(t, fieldName);
            if (value != null) {
                colVals.add(column + "=" + "#{param1." + fieldName + "}");///modify by SuperScorpion on 2020.02.13
            }
        }
        return colVals;
    }



    /**
     * 处理传入的字段字符
     * @param ps        参数
     * @return 返回值类型
     * 如果参数用Object... ps接收 则会把传入的数组封装一层ps[0] 里才是真正的参数数组 所以还是用Object[]接收
     */
    private String reSolveColumn(Object[] ps) {

        boolean isValidColumn = (ps == null || ps.length == 0);//modify by SuperScorpion on 2020.01.17

        return isValidColumn ? columnsStr : MedusaSqlHelper.buildColumnNameForSelect(ps, currentFieldColumnNameMap);
    }

    /**
     * add by SuperScorpion on 20220913 for batch
     * 处理传入的字段字符
     * @param ps        参数
     * @return 返回值类型
     * 如果参数用Object... ps接收 则会把传入的数组封装一层ps[0] 里才是真正的参数数组 所以还是用Object[]接收
     */
    private String reSolveColumn(Object[] ps, Boolean flag) {

        boolean isValidColumn = (ps == null || ps.length == 0);//modify by SuperScorpion on 2020.01.17

        return isValidColumn ? columnsStr : MedusaSqlHelper.buildColumnNameForSelect(ps, currentFieldColumnNameMap, flag, columns);
    }

    /**
     * 生成查询所有的SQL
     * @param objParams 参数
     * @return 返回值类型
     */
    public String sqlOfSelectAll(Object[] objParams) {

        String paramColumn = (objParams == null || objParams.length == 0) ? columnsStr : MedusaSqlHelper.buildColumnNameForSelect(objParams, currentFieldColumnNameMap);

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
    public String sqlOfSelectCount(Object[] objParams) {

        //从缓存里拿到分页查询语句 必须清理掉缓存
        String cacheSq = MedusaSqlHelper.myThreadLocal.get();

        //1.处理sql为查询数量
        if (MedusaCommonUtils.isNotBlank(cacheSq)) {

            MedusaSqlHelper.myThreadLocal.remove();
            logger.debug("Medusa: Successfully cleared the query page in the cache");

            String countSq = cacheSq.replaceAll("SELECT\\b.*\\bFROM", "SELECT COUNT(1) FROM")
                    .replaceAll("\\bORDER BY\\s+\\w+\\s+[ASC|DESC]*", "")
                    .replaceAll("\\bLIMIT [0-9]*,[0-9]*", "");
            logger.debug("Medusa: Successfully returns the count sql of pages in the cache ^_^ " + countSq);

            return countSq;
        }

        //2.objParams 里的entity实体类型参数处理
//        List<String> values = obtainMedusaGazeS(objParams);

        //3.objParams 里的map类型参数处理
//        List<String> mps = obtainMedusaGazeByMap(objParams);

//        values.addAll(mps);///合并两个集合 modify by SuperScorpion on 2020.01.19

//        int valuesLen = values == null || values.isEmpty() ? 0 : values.size();

//        int len = 30 + tableName.length() + (valuesLen * 39) + 512;
        int len = 30 + tableName.length() + (10 * 39) + 512;

        StringBuilder sbb = new StringBuilder(len);
        sbb.append("SELECT COUNT(1) ").append(" FROM ").append(tableName).append(" WHERE ");

//        if (values == null || values.isEmpty()) {
            sbb.append("1=1");
//        } else {
//            sbb.append(MedusaCommonUtils.join(values, " AND "));
//        }

        //3.objParams 里的多条件查询类型参数处理
        if(objParams != null && objParams.length > 0) {

            /////处理各个参数 add by SuperScorpion on 2022.09.30
            processObjParams(sbb, objParams);
        }

        //处理 语句里1=1和1!=1 add by SuperScorpion on 20230113
        String sql = sbb.toString().replace("1!=1 OR", "").replace("1=1 AND", "").replace("1=1 OR", "");

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
    public String sqlOfSelectMedusaGaze(Object[] objParams) {//modify by SuperScorpion on 2016.12.23

        //获取到缓存中的分页查询语句 modify by SuperScorpion on 2016.11.16
        ///分页时先执行查询分页再执行查询分页 再执行总计数句 boundsql(因为)
        String cacheSq = MedusaSqlHelper.myThreadLocal.get();
        if (MedusaCommonUtils.isNotBlank(cacheSq)) {
            logger.debug("Medusa: Successfully returns the query page in the cache ^_^ " + cacheSq);
            return cacheSq;
        }

        //1.objParams 里的column字段名参数处理
        String paramColumn = (objParams == null || objParams.length == 0) ? columnsStr : MedusaSqlHelper.buildColumnNameForSelect(objParams, currentFieldColumnNameMap);

        //2.objParams 里的entity实体类型参数处理
//        List<String> values = obtainMedusaGazeS(objParams);

        //3.objParams 里的map类型参数处理
//        List<String> mps = obtainMedusaGazeByMap(objParams);

//        values.addAll(mps);///合并两个集合 modify by SuperScorpion on 2020.01.19

//        int valuesLen = values == null || values.isEmpty() ? 0 : values.size();

//        int len = 30 + tableName.length() + paramColumn.length() + (valuesLen * 39) + 512;
        int len = 30 + tableName.length() + paramColumn.length() + (10 * 39) + 512;

        StringBuilder sbb = new StringBuilder(len);
        sbb.append("SELECT ").append(paramColumn).append(" FROM ").append(tableName).append(" WHERE ");


//        if (values == null || values.isEmpty()) {
            sbb.append("1=1");
//        } else {
//            sbb.append(MedusaCommonUtils.join(values, " AND "));
//        }

        //4.objParams 里的多条件查询类型参数处理
        if(objParams != null && objParams.length > 0) {

            /////处理各个参数 add by SuperScorpion on 2022.09.30
            Pager pa = processObjParams(sbb, objParams);

            ///////分页开始
            if(pa != null) {

                //modify by SuperScorpion on 20220822 for lambda
                if(pa.getOrderByList() != null && pa.getOrderByList().size() > 0 && sbb.lastIndexOf("ORDER BY") == -1) {

                    sbb.append(" ORDER BY ");//modify by SuperScorpion 2016.10.12

                    int i = 0;
                    for(; i < pa.getOrderByList().size(); i++) {

                        //orderType 默认取desc
                        String orderType = pa.getOrderTypeList().get(i) == null ? Pager.SortTypeEnum.SORT_DESC.getCode() : (String) pa.getOrderTypeList().get(i);

                        sbb.append(pa.getOrderByList().get(i));
                        sbb.append(" ");
                        sbb.append(orderType);
                        sbb.append(",");
                    }

                    if(sbb.lastIndexOf(",") != -1) sbb.deleteCharAt(sbb.lastIndexOf(","));//去除最后的一个,
                }

                sbb.append(" LIMIT ");
                sbb.append(pa.getStartRecord());
                sbb.append(",");
                sbb.append(pa.getPageSize());

                //缓存了分页的查询语句
                MedusaSqlHelper.myThreadLocal.set(sbb.toString());
                logger.debug("Medusa: Successfully saved the page query statement to the cache ^_^ " + sbb.toString());
            }
        }

        logger.debug("Medusa: Generated SQL ^_^ " + sbb.toString());

        //处理 语句里1=1和1!=1 add by SuperScorpion on 20230113
        return sbb.toString().replace("1!=1 OR", "").replace("1=1 AND", "").replace("1=1 OR", "");
    }

    /**
     * 处理各参数条件 并且返回最后一个pager对象
     * @param sbb
     * @param objParams
     * @return
     */
    private Pager processObjParams(StringBuilder sbb, Object[] objParams) {

        Pager pa = null;

        short isd = 0;

        for (Object z : objParams) {///遍历各个参数

            if(z instanceof BaseRestrictions) {//modify by SuperScorpion on 2016.12.09  if(z instanceof BaseParam) {

                short v = 0;

                List<BaseParam> paramList = ((BaseRestrictions) z).getParamList();

                if(paramList != null && !paramList.isEmpty()) {

                    for (BaseParam x : paramList) {
                        baseParamHandler(sbb, x, isd, v, null, null, null);
                        v++;
                    }
                }

                //处理or and 条件的语句 add by SuperScorpion on 20230113
                if(z instanceof MedusaLambdaRestrictions) {
                    orAndParamHandler(sbb, z, isd);
                }
            } else if (z instanceof Pager) {

                pa = (Pager) z;//只要最后一个对象 pager
            }

            //modify by SuperScorpion on 2022.08.23 for 处理实体类型条件
            else if(entityClass.isInstance(z)) {
                paramEntityConditionHandler(sbb, z, isd);
            }
            //modify by SuperScorpion on 2022.08.23 for 处理普通map<String, Object>
            else if(z instanceof Map<?, ?> && !(z instanceof MedusaLambdaMap)) {
                paramMapConditionHandler(sbb, z, isd);
            }

            isd++;
        }

        return pa;
    }


    /**
     * select * from bac_logs where 1=1 AND (1!=1 OR user_id = 123 OR remark ="11111");
     * select * from bac_logs where 1!=1 OR (1=1 AND user_id = 123 AND remark ="11111");
     * @param sbb
     * @param z
     * @param isd
     */
    private void orAndParamHandler(StringBuilder sbb, Object z, short isd) {

        List<BaseModelClass> orModelList = ((MedusaLambdaRestrictions) z).getOrModelList();
        List<BaseModelClass> andModelList = ((MedusaLambdaRestrictions) z).getAndModelList();

        if(orModelList != null && !orModelList.isEmpty()) {
            orAndModelHandler(sbb, orModelList, isd, false);
        }
        if(andModelList != null && !andModelList.isEmpty()) {
            orAndModelHandler(sbb, andModelList, isd, true);
        }
    }

    private void orAndModelHandler(StringBuilder sbb, List<BaseModelClass> modelList, short isd, boolean isAndList) {

        String orAndStr = isAndList == false ? " OR " : " AND ";

        short m = 0;
        for (BaseModelClass bmc : modelList) {

            if(bmc instanceof OrModelClass) {
                List<BaseParam> omcParamList = bmc.getParamList();

                if(omcParamList != null && !omcParamList.isEmpty()) {

                    short n = 0;
                    sbb.append(orAndStr).append("(1!=1");
                    for (BaseParam x : omcParamList) {
                        baseParamHandler(sbb, x, isd, n, m, isAndList, false);
                        n++;
                    }
                    sbb.append(")");
                }
            }

            if(bmc instanceof AndModelClass) {
                List<BaseParam> omcParamList = bmc.getParamList();

                if(omcParamList != null && !omcParamList.isEmpty()) {

                    short n = 0;
                    sbb.append(orAndStr).append("(1=1");
                    for (BaseParam x : omcParamList) {
                        baseParamHandler(sbb, x, isd, n, m, isAndList, true);
                        n++;
                    }
                    sbb.append(")");
                }
            }

            m++;
        }
    }


    /**
     * 提供给selectMedusaGaze和sqlOfFindAllCount使用
     * 处理参数中的entity条件类型
     * @param sbb
     * @param z
     * @param isd
     */
    private void paramEntityConditionHandler(StringBuilder sbb, Object z, short isd) {
        for (String column : columns) {
            String fieldName = currentColumnFieldNameMap.get(column);//modify by SuperScorpion on 2016.11.13
            Object value = MedusaReflectionUtils.obtainFieldValue(z, fieldName);
            if (value != null && MedusaCommonUtils.isNotBlank(value.toString())) {//modify by SuperScorpion on 2020.01.19
//                            colVals.add(column + "=" + "#{array[" + i + "]." + fieldName + "}");///modify by SuperScorpion on 2020.02.13
                sbb.append(" AND ").append(column).append(" = ").append("#{array[").append(isd).append("].").append(fieldName).append("}");///modify by SuperScorpion on 2020.02.13
            }
        }
    }

    /**
     * 提供给selectMedusaGaze和sqlOfFindAllCount使用
     * 处理参数中的map条件类型
     * @param sbb
     * @param z
     * @param isd
     */
    private void paramMapConditionHandler(StringBuilder sbb, Object z, short isd) {

        Set<Map.Entry<Object, Object>> entrySet = ((Map)z).entrySet();
        Iterator<Map.Entry<Object, Object>> iter = entrySet.iterator();

        while(iter.hasNext()) {
            Map.Entry<Object, Object> entry = iter.next();
            if (entry != null && entry.getKey() instanceof String && entry.getValue() != null
                    && MedusaCommonUtils.isNotBlank(entry.getKey().toString()) && MedusaCommonUtils.isNotBlank(entry.getValue().toString())) {//modify by SuperScorpion on 2020.01.19
                String column = MedusaSqlHelper.buildColumnNameForAll(entry.getKey().toString(), currentFieldColumnNameMap);
//                            colVals.add(column + "=" + "#{array[" + i + "]." + entry.getKey() + "}");///modify by SuperScorpion on 2020.02.13
                sbb.append(" AND ").append(column).append(" = ").append("#{array[").append(isd).append("].").append(entry.getKey()).append("}");///modify by SuperScorpion on 2020.02.13
            }
        }
    }

    /**
     * modify by SuperScorpion on 2016.12.09 添加 .paramList[index]
     * isd 为(T entity, Object... param) param的index
     * ind 为MyRestrctions里paramList()的index.
     * @param sbb 参数
     * @param z             参数
     * @param isd           参数
     * @param ind           参数
     * @param imd           参数  //add by SuperScorpion on 20230113 for or and
     * @param isAndList     参数  //add by SuperScorpion on 20230113 for or and
     * @param isAndListElement    参数  //add by SuperScorpion on 20230113 for or and
     */
    public void baseParamHandler(StringBuilder sbb, Object z, Short isd, Short ind, Short imd, Boolean isAndList, Boolean isAndListElement) {

        if (z == null || MedusaCommonUtils.isBlank(((BaseParam) z).getColumn())) return;

        String orAndStr = isAndListElement == null || isAndListElement == true ? " AND " : " OR ";

        String modelListStr = isAndList == null ? "" : isAndList == false ? ".orModelList[" + imd + "]" : ".andModelList[" + imd + "]";

        //转换一下column的属性值 也许是数据库字段 也有可能是属性值
        String column = MedusaSqlHelper.buildColumnNameForAll(((BaseParam) z).getColumn(), currentFieldColumnNameMap);

        if (z instanceof BaseComplexParam) {

            if (z instanceof SingleParam) {//modify by SuperScorpion on 2016.11.17

                Object p = ((SingleParam) z).getValue();
                Boolean f = ((SingleParam) z).getNeq();

                if (p != null && MedusaCommonUtils.isNotBlank(p.toString()) && f != null) {
                    sbb.append(orAndStr).append(column);
                    if(f)
                        sbb.append(" != ");
                    else
                        sbb.append(" = ");
                    sbb.append("#{array[").append(isd).append("]").append(modelListStr).append(".paramList[").append(ind).append("].value}");///modify by SuperScorpion on 2020.02.13
                }
            } else if (z instanceof BetweenParam) {

                Object start = ((BetweenParam) z).getStart();
                Object end = ((BetweenParam) z).getEnd();

                if (start != null && end != null && MedusaCommonUtils.isNotBlank(start.toString()) && MedusaCommonUtils.isNotBlank(end.toString())) {
                    sbb.append(orAndStr).append(column).append(" BETWEEN ")
                            //.append("'").append(MedusaDateUtils.convertDateToStr(p.getEnd(), MedusaDateUtils.DATE_FULL_STR)).append("'")
                            .append("#{array[").append(isd).append("]").append(modelListStr).append(".paramList[").append(ind).append("].start}")///modify by SuperScorpion on 2020.02.13
                            .append(" AND ")
                            .append("#{array[").append(isd).append("]").append(modelListStr).append(".paramList[").append(ind).append("].end}");///modify by SuperScorpion on 2020.02.13
                }
            } else if (z instanceof NotInParam) {

                List p = ((NotInParam) z).getValue();
                Boolean f = ((NotInParam) z).getNotIn();

                if (p != null && p.size() > 0 && f != null) {
                    sbb.append(orAndStr).append(column);

                    if (f) {
                        sbb.append(" NOT IN (");
                    } else {
                        sbb.append(" IN (");
                    }

                    int k = 0;
                    while (k < p.size()) {
                        if (p.get(k) != null && MedusaCommonUtils.isNotBlank(p.get(k).toString())) {//add by SuperScorpion on 20220923
                            sbb.append("#{array[").append(isd).append("]").append(modelListStr).append(".paramList[").append(ind).append("].value[").append(k).append("]},");///modify by SuperScorpion on 2020.02.13
                            k += 1;
                        }
                    }

                    if (sbb.lastIndexOf(",") != -1) sbb.deleteCharAt(sbb.lastIndexOf(","));
                    sbb.append(")");
                }
            } else if (z instanceof LikeParam) {

                Object p = ((LikeParam) z).getValue();

                if (p != null && MedusaCommonUtils.isNotBlank(p.toString())) {
                    sbb.append(orAndStr).append(column).append(" LIKE ").append("CONCAT('%',#{array[").append(isd).append("]").append(modelListStr).append(".paramList[").append(ind).append("].value},'%')");///modify by SuperScorpion on 2020.02.13
                }
            } else if (z instanceof NotNullParam) {

                Boolean p = ((NotNullParam) z).getValue();

                if (p != null) {
                    if (p) {
                        sbb.append(orAndStr).append(column).append(" IS NOT NULL ");
                    } else {
                        sbb.append(orAndStr).append(column).append(" IS NULL ");
                    }
                }
            }
        } else if (z instanceof BaseGeLeParam) {

            Object p = ((BaseGeLeParam) z).getValue();

            if (p != null && MedusaCommonUtils.isNotBlank(p.toString())) {

                if (z instanceof GreatThanParam) {

                    sbb.append(orAndStr).append(column).append(" > ").append("#{array[").append(isd).append("]").append(modelListStr).append(".paramList[").append(ind).append("].value}");///modify by SuperScorpion on 2020.02.13
                } else if (z instanceof GreatEqualParam) {

                    sbb.append(orAndStr).append(column).append(" >= ").append("#{array[").append(isd).append("]").append(modelListStr).append(".paramList[").append(ind).append("].value}");///modify by SuperScorpion on 2020.02.13
                } else if (z instanceof LessThanParam) {

                    sbb.append(orAndStr).append(column).append(" < ").append("#{array[").append(isd).append("]").append(modelListStr).append(".paramList[").append(ind).append("].value}");///modify by SuperScorpion on 2020.02.13
                } else if (z instanceof LessEqualParam) {

                    sbb.append(orAndStr).append(column).append(" <= ").append("#{array[").append(isd).append("]").append(modelListStr).append(".paramList[").append(ind).append("].value");///modify by SuperScorpion on 2020.02.13
                }
            }
        } else if (z instanceof BaseSortParam) {

            if (z instanceof OrderByParam) {

                Pager.SortTypeEnum p = ((OrderByParam) z).getValue();

                if (sbb.lastIndexOf("ORDER BY") != -1) {
                    sbb.insert(sbb.lastIndexOf(" ORDER BY ") + " ORDER BY ".length(), column.concat(" ").concat(p.getCode()).concat(","));
                } else {
                    sbb.append(" ORDER BY ").append(column).append(" ").append(p.getCode());
                }
            } else if (z instanceof GroupByParam) {
                if (sbb.lastIndexOf("GROUP BY") != -1) {
                    sbb.insert(sbb.lastIndexOf(" GROUP BY ") + " GROUP BY ".length(), column.concat(","));
                } else {
                    sbb.append(" GROUP BY ").append(column);
                }
            }
        }
    }
}

