package com.jy.medusa.stuff.hotLoad;

import org.apache.ibatis.session.SqlSessionFactory;

/**
 * Created by neo on 2016/12/10.
 */
public class MyMapperRefresh {

/*    private SqlSessionFactory sqlSessionFactory;
    private String xmlPath;
    private short seconds;*/

/*    public void setSeconds(short seconds) {
        this.seconds = seconds;
    }

    public void setXmlPath(String xmlPath) {
        this.xmlPath = xmlPath;
    }

    public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
        this.sqlSessionFactory = sqlSessionFactory;
    }*/

    MyMapperRefresh(SqlSessionFactory sqlSessionFactory, String xmlPath, short seconds) {
        Thread getTokenThread = new Thread(new MyMapperReloaderThread(xmlPath, sqlSessionFactory, seconds));
        getTokenThread.start();
    }
}
