package com.jy.medusa.generator;

/**
 * Created by neo on 2020/2/14.
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * 获取properties 属性值 并初始化
 */
public class DataBaseTools {

    private String driver;

    private String url;

    private String user;

    private String password;

    private Connection conn;

    public DataBaseTools() {
        loadProperties();
        //        loadProperties(fileName);
    }

    private void loadProperties() {
        //    private void loadProperties(String fileName) {

    /*        String resPaths = System.getProperty("user.dir") + Home.getProperPath() + fileName;

            Properties props = new Properties();
            try {
                props.load(new FileInputStream(resPaths));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }*/
    /*        this.driver = props.getProperty("jdbc.driver");
            this.url = props.getProperty("jdbc.url");
            this.user = props.getProperty("jdbc.username");
            this.password = props.getProperty("jdbc.password");*/

        this.driver = Home.jdbcDriver;
        this.url = Home.jdbcUrl;
        this.user = Home.jdbcUsername;
        this.password = Home.jdbcPassword;
    }


    public Connection openConnection() {
        try {
            if (conn != null && !conn.isClosed()) {
                return this.conn;
            } else {
                try {
                    Class.forName(driver);///初始化 并注册 driver
                    this.conn = DriverManager.getConnection(url, user, password);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return this.conn;
    }

    public void closeConnection(Connection conn, Statement st) {
        try {
            if (st != null) {
                st.close();
            }
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}