package com.jy.medusa.generator.ftl;

import com.jy.medusa.generator.Home;
import com.jy.medusa.generator.MyGenUtils;
import com.jy.medusa.generator.ftl.vo.XmlAssociVo;
import com.jy.medusa.utils.MyDateUtils;
import com.jy.medusa.utils.MyUtils;
import com.jy.medusa.utils.SystemConfigs;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by neo on 16/7/27.
 */
public class GenXmlFtl {

    private String[] colSqlNames;//数据库列名数组
    private String[] colNames; // 列名数组
    private String[] colTypes; // 列名类型数组
    private String[] colTypesSql;//mysql 对应的类型数组
    private Integer[] colSizes; // 列名大小数组

    private String packagePath;//mapper
    private String mapperPath;//xml
    private String tableName;
//    private String propertyFilename;
    private String entityPath;//entity
    private String entityName;

    private List<String> markXmlList;
    private String tag;//标记 mark

    private List<String> associationColumn;
    private String pluralAssociation;//映射关系字段的后缀名 一般为s



    public GenXmlFtl(String mapperPath, String packagePath, String entityPath, String tableName, String tag, String ignorAssociation, String pluralAssociation) {
        this.packagePath = packagePath;
        this.mapperPath = mapperPath;
        this.tableName = tableName;
//        this.propertyFilename = propertyFilename;
        this.entityPath = entityPath;
        this.entityName = MyGenUtils.upcaseFirst(tableName);
        this.tag = tag;
        this.associationColumn = Arrays.asList(ignorAssociation.split(","));
        this.pluralAssociation = pluralAssociation;
//        this.markXmlList = MyGenUtils.genTagStrList(entityName + "Mapper.xml", packagePath, tag, "xml");
    }

    private void changeTypes(String[] colTypes, String[] colTypesSql){//TODO

        for(int i=0; i < colTypesSql.length ;i++) {
            if (MyUtils.isNotBlank(colTypesSql[i])) {
                switch (colTypesSql[i]) {
                    case "INT":
                        colTypes[i] = "INTEGER";
                        break;
                    case "DATETIME":
                        colTypes[i] = "TIMESTAMP";
                        break;
                    default:
                        colTypes[i] = colTypesSql[i];
                        break;
                }
            }
        }
    }

    public void process() {

        Map<String, Object[]> resultMap = genAllKindTypes(tableName);

        colSqlNames = (String[]) resultMap.get("colSqlNames");
        colNames = (String[]) resultMap.get("colNames");
        colTypes = (String[]) resultMap.get("colTypes");
        colSizes = (Integer[]) resultMap.get("colSizes");
        colTypesSql = (String[]) resultMap.get("colTypesSql");

        try {
            Map<String, Object> map = parse();
            String path = System.getProperty("user.dir") + "/src/main/java/" + packagePath.replaceAll("\\.", "/");
            File file = new File(path);
            if(!file.exists()){
                file.mkdirs();
            }
            String resPath = path + "/" + entityName + "Mapper.xml";


            Configuration cfg = new Configuration(Configuration.VERSION_2_3_22);

            if(!Home.checkIsFtlAvailable()) {

                cfg.setClassLoaderForTemplateLoading(this.getClass().getClassLoader(), "/template");
            } else {

                cfg.setDirectoryForTemplateLoading(new File(Home.ftlDirPath));
            }


            Template temp = cfg.getTemplate("xml.ftl");//TODO

            FileOutputStream fos = new FileOutputStream(new File(resPath));

            Writer out = new BufferedWriter(new OutputStreamWriter(fos, "utf-8"), 10240);

            if(temp != null) temp.process(map, out);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (TemplateException e) {
            e.printStackTrace();
        }
    }

    public Map<String, Object[]> genAllKindTypes(String tableName) {

        Map<String, Object[]> resultMap = new HashMap<>();

        String[] colSqlNames = null,colNames = null,colTypes = null,colTypesSql = null;
        Integer[] colSizes = null;

        GenEntityFtl.DataBaseTools dataBaseTools = new GenEntityFtl().new DataBaseTools();

        Connection conn = dataBaseTools.openConnection(); // 得到数据库连接
        PreparedStatement pstmt = null;
        String strsql = "select * from " + tableName;
        try {
            pstmt = conn.prepareStatement(strsql);
            ResultSetMetaData rsmd = pstmt.getMetaData();
            int size = rsmd.getColumnCount(); // 共有多少列
            colSqlNames = new String[size];
            colNames = new String[size];
            colTypes = new String[size];
            colSizes = new Integer[size];
            colTypesSql = new String[size];

            for (int i = 0; i < rsmd.getColumnCount(); i++) {

                colSqlNames[i] = rsmd.getColumnName(i + 1);
                colNames[i] = MyGenUtils.getCamelStr(rsmd.getColumnName(i + 1));
                colTypesSql[i] = rsmd.getColumnTypeName(i + 1);
                colSizes[i] = rsmd.getColumnDisplaySize(i + 1);
            }

            changeTypes(colTypes, colTypesSql);//处理mybatis类型 和 sql类型不一致

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            dataBaseTools.closeConnection(conn, pstmt);
        }

        resultMap.put("colSqlNames", colSqlNames);
        resultMap.put("colNames", colNames);
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

            if (colNames[i].trim().equalsIgnoreCase(SystemConfigs.PRIMARY_KEY)) {
                resultMapStrList.add("<id column=\"id\" jdbcType=\"INTEGER\" property=\"id\" />");
            } else {
                resultMapStrList.add("<result column=\"" + colSqlNames[i] + "\" jdbcType=\"" + colTypes[i] + "\" property=\"" + colNames[i] + "\" />");
            }

        }

        //association 等到最后才生成
        for (int i = 0; i < colSqlNames.length; i++) {

            //外间关联
            if(MyUtils.isNotBlank(colSqlNames[i]) && colSqlNames[i].endsWith("_id") && associationColumn.contains(colSqlNames[i])) {

                String p = colSqlNames[i].trim().replace("_id", "").trim();
                if(MyUtils.isNotBlank(pluralAssociation) && !p.endsWith(pluralAssociation)) {///modify by neo on 2016.11.25
                    p = p.concat(pluralAssociation);
                }

                String bigStr = MyGenUtils.upcaseFirst(p);
                String smallStr = MyGenUtils.getCamelStr(p);

                String param = "<association property=\"" + smallStr + "\" column=\"" + colSqlNames[i] + "\" select=\"find" + bigStr + "ById\" " + Home.lazyLoad + "/>";
                resultMapStrList.add(param);
            }
        }


        List<XmlAssociVo> xaList = new ArrayList<>();

        //外间关联表
        for (int i = 0; i < colSqlNames.length; i++) {

            //外间关联sss
            if(MyUtils.isNotBlank(colSqlNames[i]) && colSqlNames[i].endsWith("_id") && associationColumn.contains(colSqlNames[i])) {

                String p = colSqlNames[i].trim().replace("_id", "").trim();

                if(MyUtils.isNotBlank(pluralAssociation) && !p.endsWith(pluralAssociation)) {///modify by neo on 2016.11.25
                    p = p.concat(pluralAssociation);
                }

                Map<String, Object[]> resultMap = genAllKindTypes(p);

                String[] colSqlNames = (String[]) resultMap.get("colSqlNames");
                String[] colNames = (String[]) resultMap.get("colNames");

                StringBuilder sbb = new StringBuilder();

                for (int j = 0; j < colSqlNames.length; j++) {

                    if(colSqlNames[j].equals(colNames[j])) {
                        sbb.append(colSqlNames[j]);
                        sbb.append(",");
                    } else {
                        sbb.append(colSqlNames[j] + " ");
                        sbb.append(colNames[j]);
                        sbb.append(",");
                    }
                }

                if(sbb.indexOf(",") != -1) sbb.deleteCharAt(sbb.lastIndexOf(","));//去掉最后一个,

                String bigStr = MyGenUtils.upcaseFirst(p);

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
        map.put("now_time", MyDateUtils.convertDateToStr(new Date(), null));

        map.put("base_column_list", String.join(",", colSqlNames));

        map.put("resultMapStrList", resultMapStrList);
        map.put("xaList", xaList);

//        map.put("specificId", "#{id}");

        return map;
    }

}
