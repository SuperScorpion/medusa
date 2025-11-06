package com.jy.medusa.gaze.interceptor;

/**
 * Created by SuperScorpion on 16/9/15.
 */

import com.jy.medusa.gaze.stuff.MedusaSqlHelper;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Statement;
import java.util.Properties;

@Intercepts({
        @Signature(method = "update", type = StatementHandler.class, args = {Statement.class}),
        @Signature(method = "query", type = Executor.class, args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
        @Signature(method = "update", type = Executor.class, args = {MappedStatement.class, Object.class})
})
public class MedusaInterceptor extends MedusaInterceptorExecutorHandler implements Interceptor {

    private static final Logger logger = LoggerFactory.getLogger(MedusaInterceptor.class);

    public MedusaInterceptor() {
    }

    public MedusaInterceptor(boolean devFlag) {
        super.setDevFlag(devFlag);
    }

    public Object intercept(Invocation invocation) throws Throwable {

        Object result;

        if (invocation.getTarget() instanceof Executor) {
            try {
                result = processExecutor(invocation);
            } finally {
                //防止异常情况发生后 tomcat线程池重用线程导致threadLocal污染下一个查询 add by SuperScorpion on 20251105
                if (MedusaSqlHelper.myPagerThreadLocal.get() != null) MedusaSqlHelper.myPagerThreadLocal.remove();
                if (MedusaSqlHelper.myThreadLocal.get() != null) MedusaSqlHelper.myThreadLocal.remove();
            }
        } else if (invocation.getTarget() instanceof StatementHandler) {
            //delete insert update 都会进来此拦截(medusa的或非medusa的)
            //processExecutor里的invocationProceed(invocation)嵌套进入
            result = processStatementHandler(invocation);
        } else {
            result = invocationProceed(invocation);
        }

        return result;
    }

    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    public void setProperties(Properties properties) {
/*        String prop1 = properties.getProperty("prop1");
        String prop2 = properties.getProperty("prop2");*/
    }
}