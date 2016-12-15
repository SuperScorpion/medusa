package com.jy.medusa.stuff.hotload;

import com.jy.medusa.utils.MyUtils;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

 class MyMapperReloaderThread implements Runnable {

    private static Logger log = LoggerFactory.getLogger(MyMapperReloaderThread.class);

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

        if(MyUtils.isBlank(xmlPath)) throw new RuntimeException("your xmlPath is null");

        while (true) {
            try {

                if(sqlSessionFactory == null) {
                    log.debug("MyMapperReloaderThread wait for sqlSessionFactory and began to sleep {} seconds.", seconds);
                    Thread.sleep(seconds * 1000);
                    continue;
                }

                mr = mr == null ? new MyHotspotReloader(xmlPath, sqlSessionFactory) : mr;

                mr.refreshMapper();

                log.debug("MyMapperReloaderThread has completed the hot - loading and began to sleep {} seconds.", seconds);
                Thread.sleep(seconds * 1000);
            } catch (InterruptedException e) {
                log.error("{MyMapperReloaderThread catch the InterruptedException！}", e);
                e.printStackTrace();
                try {
                    Thread.sleep(seconds * 1000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            } catch (Exception e) {
            	log.error("{MyMapperReloaderThread catch the Exception！}", e);
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