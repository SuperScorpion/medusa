package com.jy.medusa.gaze.interceptor;

/**
 * Created by SuperScorpion on 16/9/15.
 */

import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Invocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;

abstract class MedusaInterceptorBaseHandler {

    private static final Logger logger = LoggerFactory.getLogger(MedusaInterceptorBaseHandler.class);

    private Boolean devFlag = false;

    protected void setDevFlag(Boolean devFlag) {
        this.devFlag = devFlag;
    }

    protected Object invocationProceed(Invocation invocation) throws InvocationTargetException, IllegalAccessException {
        Object result;
        if(devFlag) {
            long startTime = System.nanoTime();
            result = invocation.proceed();
            long endTime = System.nanoTime();
            logger.debug("Medusa: SQL运行时间 - " + (endTime - startTime) + "ns" + " - " + (endTime - startTime)/1000000 + "ms");

            //大于10秒则记录为慢查询
            if((endTime - startTime)/1000000000 > 10) logger.warn("Medusa: 有慢查询出现 - sql - " + ((MappedStatement) invocation.getArgs()[0]).getSqlSource().getBoundSql(invocation.getArgs()[1]).getSql());
        } else {
            result = invocation.proceed();
        }
        return result;
    }
}