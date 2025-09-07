package com.jy.medusa.gaze.stuff;

import org.apache.ibatis.builder.SqlSourceBuilder;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.scripting.xmltags.DynamicContext;
import org.apache.ibatis.scripting.xmltags.SqlNode;
import org.apache.ibatis.session.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 分页工具类 - 2025.09.07
 * @author SuperScorpion
 */
public class PagerHelper {

	private static final Logger logger = LoggerFactory.getLogger(PagerHelper.class);

	/**
	 * modify by SuperScorpion on 20250906
	 * 处理pager类的拼接 普通方法和medusa方法使用
	 * @param sbb 参数
	 * @param pa 参数
	 */
	public static void concatDynamicSqlForPager(StringBuilder sbb, Pager pa) {

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



//	public static class BoundSqlSqlSource implements SqlSource {
//		BoundSql boundSql;
//
//		public BoundSqlSqlSource(BoundSql boundSql) {
//			this.boundSql = boundSql;
//		}
//
//		@Override
//		public BoundSql getBoundSql(Object parameterObject) {
//			return boundSql;
//		}
//	}


	/**
	 * 用户自定义xml里面的方法只含有#{xxx}的语句
	 * RawSqlSource 里面的sqlSource 其实就是StaticSqlSource
	 * copy from {@link org.apache.ibatis.builder.StaticSqlSource}
	 */
	public static class MedusaStaticSqlSource implements SqlSource {
		private final String sql;
		private final List<ParameterMapping> parameterMappings;
		private final Configuration configuration;

		public MedusaStaticSqlSource(Configuration configuration, String sql) {
			this(configuration, sql, (List)null);
		}

		public MedusaStaticSqlSource(Configuration configuration, String sql, List<ParameterMapping> parameterMappings) {
			this.sql = sql;
			this.parameterMappings = parameterMappings;
			this.configuration = configuration;
		}

		@Override
		public BoundSql getBoundSql(Object parameterObject) {
			return new BoundSql(this.configuration, concatPagerSql(this.sql), this.parameterMappings, parameterObject);
		}
	}


	/**
	 * 用户自定义xml里面的方法 包含各种标签 where foreach ...
	 * copy from {@link org.apache.ibatis.scripting.xmltags.DynamicSqlSource}
	 */
	public static class MedusaDynamicSqlSource implements SqlSource {
		private final List<ParameterMapping> parameterMappings;
		private final Configuration configuration;
		private final SqlNode rootSqlNode;

		public MedusaDynamicSqlSource(Configuration configuration, List<ParameterMapping> parameterMappings, SqlNode rootSqlNode) {
			this.parameterMappings = parameterMappings;
			this.configuration = configuration;
			this.rootSqlNode = rootSqlNode;
		}

		@Override
		public BoundSql getBoundSql(Object parameterObject) {
			DynamicContext context = new DynamicContext(this.configuration, parameterObject);
			this.rootSqlNode.apply(context);
			SqlSourceBuilder sqlSourceParser = new SqlSourceBuilder(this.configuration);
			Class<?> parameterType = parameterObject == null ? Object.class : parameterObject.getClass();
			SqlSource sqlSource = sqlSourceParser.parse(concatPagerSql(context.getSql()), parameterType, context.getBindings());
			BoundSql boundSql = sqlSource.getBoundSql(parameterObject);
			Map<String, Object> var10000 = context.getBindings();
			Objects.requireNonNull(boundSql);
			var10000.forEach(boundSql::setAdditionalParameter);
			return boundSql;
		}
	}

	/**
	 * 获取原有的sql并拼接分页的sql - limit并塞进threadLocal 后续计算总数量要用
	 * @param sql 参数
	 * @return
	 */
	private static String concatPagerSql(String sql) {
		StringBuilder sbb = new StringBuilder(sql);
		Pager z = MedusaSqlHelper.myPagerThreadLocal.get();
		PagerHelper.concatDynamicSqlForPager(sbb, z);
		return sbb.toString();
	}

	/**
	 * 重新构建MappedStatement
	 * @param ms 参数
	 * @param newSqlSource 参数
	 * @return MappedStatement
	 */
	public static MappedStatement copyFromMappedStatement(MappedStatement ms, SqlSource newSqlSource) {
		MappedStatement.Builder builder = new MappedStatement.Builder(ms.getConfiguration(),
				ms.getId(), newSqlSource, ms.getSqlCommandType());
		builder.resource(ms.getResource());
		builder.fetchSize(ms.getFetchSize());
		builder.statementType(ms.getStatementType());
		builder.keyGenerator(ms.getKeyGenerator());
		if (ms.getKeyProperties() != null) {
			for (String keyProperty : ms.getKeyProperties()) {
				builder.keyProperty(keyProperty);
			}
		}
		builder.timeout(ms.getTimeout());
		builder.parameterMap(ms.getParameterMap());
		builder.resultMaps(ms.getResultMaps());
		builder.cache(ms.getCache());
		return builder.build();
	}

}