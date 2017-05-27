package com.jy.medusa.stuff.hotload;

import org.apache.ibatis.session.SqlSessionFactory;

/**
 * Created by neo on 2016/12/10.
 */
public class MyMapperRefresh {

    MyMapperRefresh(SqlSessionFactory sqlSessionFactory, String xmlPath, int seconds) {
        Thread getTokenThread = new Thread(new MyMapperReloaderThread(xmlPath, sqlSessionFactory, seconds));
        getTokenThread.start();
    }
}
