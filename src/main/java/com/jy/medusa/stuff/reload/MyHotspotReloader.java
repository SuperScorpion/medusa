package com.jy.medusa.stuff.reload;

/**
 * Created by neo on 2016/12/10.
 */

import com.jy.medusa.stuff.exception.MedusaException;
import com.jy.medusa.utils.MyReflectionUtils;
import com.jy.medusa.utils.MyUtils;
import com.jy.medusa.utils.SystemConfigs;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;

 class MyHotspotReloader {
     
     private static Logger log = LoggerFactory.getLogger(MyHotspotReloader.class);

    private SqlSessionFactory sqlSessionFactory;
    private String xmlPath;//用户配置的属性
    private List<File> mapperXmlFileList;//记录所有的 xml 文件
    private Map<String, Long> fileChangeMap = new HashMap<>();// 记录文件是否变化了
    private List<String> mappedStatementCacheKeyList = new ArrayList<>();//框架内部的方法名称 对应的 mapperstatement 的 key 记录


    MyHotspotReloader(String xmlPath, SqlSessionFactory sqlSessionFactory){
        this.xmlPath = xmlPath;
        this.sqlSessionFactory = sqlSessionFactory;
    }

     /**
      * 主方法热加载体
      */
    public void refreshMapper() {

        try {
            Configuration configuration = sqlSessionFactory.getConfiguration();

            String xmlAbsolutelyPath = MyHotspotReloader.class.getClassLoader().getResource("").toString() + xmlPath.replace(".", "/");
            xmlAbsolutelyPath = xmlAbsolutelyPath.replace("file:", "");

            // step.1 扫描文件
            try {
                scanMapperXml(xmlAbsolutelyPath);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }

            if(mapperXmlFileList == null || mapperXmlFileList.isEmpty()) return;

            if(isFirst()) {
                initCacheMapperStatement(configuration);
                return;
            }

            // step.2 判断是否有文件发生了变化

            String xmlParamName = null;
            boolean changed = false;///所有文件是否有 一个出现了改变
            for (File mapperXml : mapperXmlFileList) {

                if (isChanged(mapperXml)) {
                    changed = true;
                    xmlParamName = mapperXml.getName();
                    break;
                }
            }

            if(changed) {///如果有改变的 则删掉所有的配置项 只保留一部分 medusa 框架内部的 然后再 重新加载所有的 非框架内部方法

                // step.2.1 清理
                removeConfig(configuration);

                // step.2.2 重新加载
                for (File v : mapperXmlFileList) {
                    String res = "file [" + xmlAbsolutelyPath + "." + v.getName() + "]";
                    try {
                        XMLMapperBuilder xmlMapperBuilder = new XMLMapperBuilder(new FileInputStream(v), configuration, res, configuration.getSqlFragments());
                        xmlMapperBuilder.parse();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                log.debug("Because of xml file {} changed - all xml has been overloaded.", xmlParamName);
            }

            if(mapperXmlFileList.size() != 0) mapperXmlFileList.clear();//modify by neo on 2016.12.15

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 扫描xml文件所在的路径
     * @throws IOException
     */
    private void scanMapperXml(String xmlAbsolutelyPath) throws IOException {

        if(MyUtils.isBlank(xmlPath)) throw new MedusaException("Medusa: Your mybatis xmlPath is null!");

        File xmlDirs = new File(xmlAbsolutelyPath);

        if (xmlDirs.isDirectory()) {

            if(mapperXmlFileList == null) mapperXmlFileList = new ArrayList<>();

            String[] filelist = xmlDirs.list();

            for (int i = 0; i < filelist.length; i++) {

                File readfile = new File(xmlAbsolutelyPath + "/" + filelist[i]);

                if (!readfile.isDirectory()) mapperXmlFileList.add(readfile);
            }
        }
    }

    /**
     * 清空Configuration中几个重要的缓存
     * @param configuration
     * @throws Exception
     */
    private void removeConfig(Configuration configuration) throws Exception {

        if(configuration == null) throw new MedusaException("Medusa: The configuration param is null!");

        String[] p = new String[]{"mappedStatements", "caches", "resultMaps", "parameterMaps", "keyGenerators", "sqlFragments"};

        clearMaps(configuration, p);
    }

    @SuppressWarnings("rawtypes")
    private void clearMaps(Configuration configuration, String[] fieldName) throws Exception {


        for(String f : fieldName) {

            if (MyUtils.isNotBlank(f)) {

                Field field = MyReflectionUtils.obtainAccessibleField(configuration, f);
                Map<Object, Object> configMap = (Map) field.get(configuration);

                if (f.equals("mappedStatements")) {

                    List<String> paramList = new ArrayList<>();///////标记为非框架的 内部方法名

                    for (Object v : configMap.keySet()) {
                        if (!mappedStatementCacheKeyList.contains(v.toString())) paramList.add(v.toString());
                    }

                    for (String z : paramList) {
                        configMap.remove(z);
                    }
                } else {
                    configMap.clear();
                }
            }
        }
    }

    @SuppressWarnings("rawtypes")
    private void clearSets(Configuration configuration, String[] fieldName) throws Exception {

        for(String f : fieldName) {
            if(MyUtils.isNotBlank(f)) {
                Field field = MyReflectionUtils.obtainAccessibleField(configuration, f);
                Set setConfig = (Set) field.get(configuration);
                setConfig.clear();
            }
        }
    }

    /**
     * 判断文件是否发生了变化
     * @return
     * @throws IOException
     */
    private boolean isChanged(File resource) throws IOException, IllegalAccessException {

        String resourceName = resource.getName();

        long lastFrame = resource.lastModified();

        // 修改文件:判断文件内容是否有变化
        Long compareFrame = fileChangeMap.get(resourceName);

        boolean modifyFlag = compareFrame != null && compareFrame.longValue() != lastFrame;// 此为修改标识

        if(modifyFlag) {//修改时,存储文件
            fileChangeMap.put(resourceName, Long.valueOf(lastFrame));
            return true;
        }

        return false;
    }

     /**
      * 判断是否是第一次初始化容器
      * @return
      */
     private boolean isFirst() {
         return fileChangeMap.isEmpty() ? true : false;
     }


     /**
      * 把属于框架方法 medusa里的方法名 对应 mapperstatement 的 key 缓存下来待使用
      * @param configuration
      * @throws IllegalAccessException
      */
     private void initCacheMapperStatement(Configuration configuration) throws IllegalAccessException {

         for (File mapperXml : mapperXmlFileList) {

             String resourceName = mapperXml.getName();

             long lastFrame = mapperXml.lastModified();

             fileChangeMap.put(resourceName, Long.valueOf(lastFrame));
         }

         Field field = MyReflectionUtils.obtainAccessibleField(configuration, "mappedStatements");
         Map<String, Object> configMap = (Map) field.get(configuration);

         for(String k : configMap.keySet()) {
             for(String l : SystemConfigs.MY_ALL_METHOD_NANES_LIST) {
                 if(k.endsWith(l)) mappedStatementCacheKeyList.add(k);
             }
         }
     }
}