package com.jy.medusa.generator.gen;

import com.jy.medusa.gaze.utils.MedusaCommonUtils;
import com.jy.medusa.gaze.utils.MedusaDateUtils;
import com.jy.medusa.generator.Home;
import com.jy.medusa.generator.MedusaGenUtils;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * Created by SuperScorpion on 16/7/27.
 * @deprecated
 */
public class GenMapper {

    private String entityPath;
    private String mapperPath;

    private String entityName;

    private String mixMapper = Home.mixMapper;

    private List<String> markServiceList;
    private List<String> markServiceImplList;
    private List<String> markMapperList;
    private String tag;//标记 mark

    public GenMapper(String tableName, String entityPath, String mapperPath) {
        this.entityPath = entityPath;
        this.mapperPath = mapperPath;
        this.entityName = MedusaGenUtils.upcaseFirst(tableName);

        this.tag = Home.tag;

        this.markMapperList = MedusaGenUtils.genTagStrList(entityName + "Mapper.java", mapperPath, tag, "mapper");
    }

    public void process() {

        try {
            //写入service 和 impl

            File file;

            //mapper
            String pathmm = Home.proJavaPath + mapperPath.replaceAll("\\.", "/");
            file = new File(pathmm);
            if(!file.exists()) {file.mkdirs();}
            String resPath3 = pathmm + "/" + entityName + "Mapper.java";
            //如果目标文件已存在 则跳过 add by SuperScorpion on 20230221
            File resPathFile3 = new File(resPath3);
            if(resPathFile3.exists()) {
                System.out.println("Medusa: " + entityName + "Mapper.java" + " 文件已存在 已跳过生成...");
                return;
            }
            MedusaCommonUtils.writeString2File(resPathFile3, process3(), "UTF-8");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * mapper
     * @return 返回值类型
     */
    private String process3() {

        StringBuilder sbb = new StringBuilder();

        sbb.append("package " + mapperPath + ";\r\n\r\n");

        sbb.append("import " + entityPath + "." + entityName + Home.entityNameSuffix + ";\r\n");
        sbb.append("import " + mixMapper + ";\r\n\r\n");//TODO

        //添加作者
        sbb.append("/**\r\n");
        sbb.append(" * Created by " + Home.author + " on " + MedusaDateUtils.convertDateToStr(new Date(), null) + "\r\n");
        sbb.append(" */\r\n");

        sbb.append("public interface " + entityName + "Mapper extends Mapper<" + entityName + Home.entityNameSuffix + "> {\r\n");

        MedusaGenUtils.processAllRemains(markMapperList, sbb, tag, "mapper");

        sbb.append("}");

        return sbb.toString();
    }
}
