package com.jy.medusa.stuff.hotLoad;

import com.jy.medusa.utils.MyUtils;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



 class MyMapperReloaderThread implements Runnable {

    private static Logger log = LoggerFactory.getLogger(MyMapperReloaderThread.class);

    private SqlSessionFactory sqlSessionFactory;
    private String xmlPath;
    private short seconds = 3600;

    MyMapperReloaderThread(String xmlPath, SqlSessionFactory sqlSessionFactory, short seconds){
        this.xmlPath = xmlPath;
        this.sqlSessionFactory = sqlSessionFactory;
        this.seconds = seconds;
    }

    public void run() {

        if(MyUtils.isBlank(xmlPath)) throw new RuntimeException("your xmlPath is null");

        while (true) {
            try {

                if(sqlSessionFactory == null) {
                    log.debug("MyMapperReloaderThread wait for sqlSessionFactory and began to sleep {} seconds.", seconds);
                    Thread.sleep(seconds * 1000);
                    continue;
                }

                new MyHotspotReloader(xmlPath, sqlSessionFactory).refreshMapper();

                log.debug("MyMapperReloaderThread has completed the hot - loading and began to sleep {} seconds.", seconds);
                Thread.sleep(seconds * 1000);
            } catch (InterruptedException e) {
                log.error("{MyMapperReloaderThread 线程出现异常被打断！}", e);
                e.printStackTrace();
                try {
                    Thread.sleep(seconds * 1000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            } catch (Exception e) {
            	log.error("{MyMapperReloaderThread 出现异常情况！}", e);
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