package com.jy.medusa.generator.ftl.gen;

import com.jy.medusa.gaze.utils.MedusaCommonUtils;
import com.jy.medusa.gaze.utils.MedusaDateUtils;
import com.jy.medusa.gaze.utils.SystemConfigs;
import com.jy.medusa.generator.DataBaseTools;
import com.jy.medusa.generator.Home;
import com.jy.medusa.generator.MedusaGenUtils;
import com.jy.medusa.generator.ftl.vo.XmlAssociVo;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.*;
import java.sql.*;
import java.util.Date;
import java.util.*;

/**
 * Created by SuperScorpion on 16/7/27.
 */
public class GenXmlFtl {

    private String[] colSqlNames;//数据库列名数组
    private String[] colFieldNames; // 列名数组
    private String[] colTypes; // 列名类型数组
    private String[] colTypesSql;//mysql 对应的类型数组
    private Integer[] colSizes; // 列名大小数组

    private String xmlPath;//xml
    private String mapperPath;//mapper
    private String tableName;
//    private String propertyFilename;
    private String entityPath;//entity
    private String entityName;

    private List<String> markXmlList;
    private String tag;//标记 mark

    private List<String> associationColumn;
    private String pluralAssociation;//映射关系字段的后缀名 一般为s

    private String primaryKey = SystemConfigs.PRIMARY_KEY;//默认主键字段名



    public GenXmlFtl(String mapperPath, String xmlPath, String entityPath, String tableName) {
        this.xmlPath = xmlPath;
        this.mapperPath = mapperPath;
        this.tableName = tableName;
//        this.propertyFilename = propertyFilename;
        this.entityPath = entityPath;
        this.entityName = MedusaGenUtils.upcaseFirst(tableName);
        this.tag = Home.tag;
        this.associationColumn = Arrays.asList(Home.associationColumn.split(","));
        this.pluralAssociation = Home.pluralAssociation;
//        this.markXmlList = MedusaGenUtils.genTagStrList(entityName + "Mapper.xml", xmlPath, tag, "xml");
    }

    //TODO 待添加 但是xml的jdbcType没用了 所以此方法没意义了
    private void changeTypes(String[] colTypes, String[] colTypesSql) {

        for(int i=0; i < colTypesSql.length ;i++) {
            if (MedusaCommonUtils.isNotBlank(colTypesSql[i])) {
                String sqlTypeNoUnsigned = colTypesSql[i].toLowerCase().trim().replace(" unsigned", "");//add by SuperScorpion on 20210120
                switch (sqlTypeNoUnsigned) {
                    case "INT":
                        colTypes[i] = "INTEGER";
                        break;
                    case "DATETIME":
                        colTypes[i] = "TIMESTAMP";
                        break;
                    default:
                        colTypes[i] = sqlTypeNoUnsigned;
                        break;
                }
            }
        }
    }

    public Boolean process() {

        Map<String, Object[]> resultMap = genAllKindTypes(tableName);

        colSqlNames = (String[]) resultMap.get("colSqlNames");
        colFieldNames = (String[]) resultMap.get("colFieldNames");
        colTypes = (String[]) resultMap.get("colTypes");
        colSizes = (Integer[]) resultMap.get("colSizes");
        colTypesSql = (String[]) resultMap.get("colTypesSql");

        try {
            Map<String, Object> map = parse();

            //modify by SuperScorpion on 2020.02.15
            String path;
            if(Home.xmlSuffix.matches("^classpath.*:.*")) {
                path = Home.proResourcePath + Home.xmlSuffix.replaceFirst("^classpath.*:", "");
            } else {
                path = Home.proJavaPath + xmlPath.replaceAll("\\.", "/");
            }

            File file = new File(path);
            if(!file.exists()) {
                file.mkdirs();
            }
            String resPath = path + "/" + entityName + "Mapper.xml";

            Configuration cfg = Home.cfg;

            if(!Home.checkIsFtlAvailable()) {

                cfg.setClassLoaderForTemplateLoading(this.getClass().getClassLoader(), "/template");
            } else {

                cfg.setDirectoryForTemplateLoading(new File(Home.ftlDirPath));
            }

            Template temp = cfg.getTemplate("xml.ftl");//TODO

            //如果目标文件已存在 则跳过 add by SuperScorpion on 20230221
            File resPathFile = new File(resPath);
            if(resPathFile.exists()) {
                System.out.println("Medusa: " + entityName + "Mapper.xml" + " 文件已存在 将跳过生成...");
                return false;
            }
            FileOutputStream fos = new FileOutputStream(resPathFile);

            Writer out = new BufferedWriter(new OutputStreamWriter(fos, "utf-8"), 10240);

            if(temp != null) temp.process(map, out);

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public Map<String, Object[]> genAllKindTypes(String tableName) {

        Map<String, Object[]> resultMap = new HashMap<>();

        String[] colSqlNames = null,colFieldNames = null,colTypes = null,colTypesSql = null;
        Integer[] colSizes = null;

        DataBaseTools dataBaseTools = Home.staticDataBaseTools;

        Connection conn = dataBaseTools.openConnection(); // 得到数据库连接
        PreparedStatement pstmt = null;
        String strsql = "select * from " + tableName;
        try {
            pstmt = conn.prepareStatement(strsql);
            ResultSetMetaData rsmd = pstmt.getMetaData();
            int size = rsmd.getColumnCount(); // 共有多少列
            colSqlNames = new String[size];
            colFieldNames = new String[size];
            colTypes = new String[size];
            colSizes = new Integer[size];
            colTypesSql = new String[size];

            for (int i = 0; i < rsmd.getColumnCount(); i++) {

                colSqlNames[i] = rsmd.getColumnName(i + 1);
                colFieldNames[i] = MedusaGenUtils.getCamelStr(rsmd.getColumnName(i + 1));
                colTypesSql[i] = rsmd.getColumnTypeName(i + 1);
                colSizes[i] = rsmd.getColumnDisplaySize(i + 1);
            }

            changeTypes(colTypes, colTypesSql);//处理mybatis类型 和 sql类型不一致

            //modify by SuperScorpion on 2019.08.17
            //get current primary key from table
            DatabaseMetaData dbmd = conn.getMetaData();
            ResultSet resultSet = dbmd.getPrimaryKeys(null, null, tableName);
            while(resultSet.next()) {
                if(!resultSet.isLast()) {
                    System.out.println("注意: " + tableName + " 表拥有多个主键 - 已跳过生成!");
                    break;
                }
                primaryKey = resultSet.getObject(4).toString();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            dataBaseTools.closeConnection(conn, pstmt);
        }

        resultMap.put("colSqlNames", colSqlNames);
        resultMap.put("colFieldNames", colFieldNames);
        resultMap.put("colTypes", colTypes);
        resultMap.put("colSizes", colSizes);
        resultMap.put("colTypesSql", colTypesSql);

        return resultMap;
    }



    /**
     * 解析处理(生成实体类主体代码)
     */
    private Map<String, Object> parse() {

        List<String> resultMapStrList = new ArrayList<>();

        for (int i = 0; i < colSqlNames.length; i++) {

            if (colSqlNames[i].trim().equalsIgnoreCase(primaryKey)) {
//                resultMapStrList.add("<id column=\"" + colSqlNames[i] + "\" jdbcType=\""+ colTypes[i] +"\" property=\""+ primaryKey +"\" />");//modify by SuperScorpion on 2020.02.15
                resultMapStrList.add("<id column=\"" + colSqlNames[i] + "\" property=\""+ colFieldNames[i] +"\" />");//modify by SuperScorpion on 2021.05.22
            } else {
//                resultMapStrList.add("<result column=\"" + colSqlNames[i] + "\" jdbcType=\"" + colTypes[i] + "\" property=\"" + colFieldNames[i] + "\" />");
                resultMapStrList.add("<result column=\"" + colSqlNames[i] + "\" property=\"" + colFieldNames[i] + "\" />");
            }
        }

        //association 等到最后才生成
        for (int i = 0; i < colSqlNames.length; i++) {

            //外间关联
            if(MedusaCommonUtils.isNotBlank(colSqlNames[i]) && colSqlNames[i].endsWith("_id") && associationColumn.contains(colSqlNames[i])) {

                String p = colSqlNames[i].trim().replace("_id", "").trim();
                if(MedusaCommonUtils.isNotBlank(pluralAssociation) && !p.endsWith(pluralAssociation)) {///modify by SuperScorpion on 2016.11.25
                    p = p.concat(pluralAssociation);
                }

                String bigStr = MedusaGenUtils.upcaseFirst(p);
                String smallStr = MedusaGenUtils.getCamelStr(p);

                String param = "<association property=\"" + smallStr + "\" column=\"" + colSqlNames[i] + "\" select=\"find" + bigStr + "ById\" " + Home.lazyLoadSwitch + "/>";
                resultMapStrList.add(param);
            }
        }


        List<XmlAssociVo> xaList = new ArrayList<>();

        //外间关联表
        for (int i = 0; i < colSqlNames.length; i++) {

            //外间关联sss
            if(MedusaCommonUtils.isNotBlank(colSqlNames[i]) && colSqlNames[i].endsWith("_id") && associationColumn.contains(colSqlNames[i])) {

                String p = colSqlNames[i].trim().replace("_id", "").trim();

                if(MedusaCommonUtils.isNotBlank(pluralAssociation) && !p.endsWith(pluralAssociation)) {///modify by SuperScorpion on 2016.11.25
                    p = p.concat(pluralAssociation);
                }

                Map<String, Object[]> resultMap = genAllKindTypes(p);

                String[] colSqlNames = (String[]) resultMap.get("colSqlNames");
                String[] colFieldNames = (String[]) resultMap.get("colFieldNames");

                StringBuilder sbb = new StringBuilder();

                for (int j = 0; j < colSqlNames.length; j++) {

                    if(colSqlNames[j].equals(colFieldNames[j])) {
                        sbb.append(colSqlNames[j]);
                        sbb.append(",");
                    } else {
                        sbb.append(colSqlNames[j] + " ");
                        sbb.append(colFieldNames[j]);
                        sbb.append(",");
                    }
                }

                if(sbb.indexOf(",") != -1) sbb.deleteCharAt(sbb.lastIndexOf(","));//去掉最后一个,

                String bigStr = MedusaGenUtils.upcaseFirst(p);

                XmlAssociVo xav = new XmlAssociVo();

                xav.setLowwerName(p);
                xav.setUpperName(bigStr);
                xav.setParamSql(sbb.toString());

                xaList.add(xav);

            }
        }



        Map<String, Object> map = new HashMap<>();

        map.put("mapperPath", mapperPath);
        map.put("entityName", entityName);
        map.put("entityPath", entityPath);
        map.put("entityNameSuffix", Home.entityNameSuffix);

        map.put("author", Home.author);
        map.put("now_time", MedusaDateUtils.convertDateToStr(new Date(), null));

        map.put("base_column_list", String.join(",", colSqlNames));

        map.put("resultMapStrList", resultMapStrList);
        map.put("xaList", xaList);

//        map.put("specificId", "#{id}");

        return map;
    }

}
