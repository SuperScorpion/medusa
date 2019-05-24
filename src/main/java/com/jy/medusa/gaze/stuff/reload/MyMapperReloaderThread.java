package com.jy.medusa.gaze.stuff.reload;

import com.jy.medusa.gaze.stuff.exception.MedusaException;
import com.jy.medusa.gaze.utils.MyCommonUtils;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

 class MyMapperReloaderThread implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(MyMapperReloaderThread.class);

    private SqlSessionFactory sqlSessionFactory;
    private String xmlPath;
    private int seconds = 3600;
    private MyHotspotReloader mr;

    MyMapperReloaderThread(String xmlPath, SqlSessionFactory sqlSessionFactory, int seconds){
        this.xmlPath = xmlPath;
        this.sqlSessionFactory = sqlSessionFactory;
        this.seconds = seconds;
    }

    public void run() {

        if(MyCommonUtils.isBlank(xmlPath)) throw new MedusaException("Medusa: Your mybatis xmlPath is null, please check it");

        while (true) {
            try {

                if(sqlSessionFactory == null) {
                    logger.debug("MyMapperReloaderThread wait for sqlSessionFactory and began to sleep {} seconds.", seconds);
                    Thread.sleep(seconds * 1000);
                } else {

                    mr = (mr == null) ? new MyHotspotReloader(xmlPath, sqlSessionFactory) : mr;

                    mr.refreshMapper();

                    logger.debug("MyMapperReloaderThread has completed the hot - loading and began to sleep {} seconds.", seconds);
                    Thread.sleep(seconds * 1000);
                }
            } catch (InterruptedException e) {
                logger.error("{MyMapperReloaderThread catch the InterruptedException}", e);
                e.printStackTrace();
                try {
                    Thread.sleep(seconds * 1000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            } catch (Exception e) {
            	logger.error("{MyMapperReloaderThread catch the Exception}", e);
                e.printStackTrace();
                try {
                    Thread.sleep(seconds * 1000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
			}
        }
    }
}