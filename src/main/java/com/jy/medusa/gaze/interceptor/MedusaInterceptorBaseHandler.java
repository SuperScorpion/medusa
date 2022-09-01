package com.jy.medusa.gaze.interceptor;

/**
 * Created by neo on 16/9/15.
 */

import org.apache.ibatis.plugin.Invocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;

public class MedusaInterceptorBaseHandler {

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
        } else {
            result = invocation.proceed();
        }
        return result;
    }
}