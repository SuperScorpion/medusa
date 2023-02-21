package com.jy.medusa.generator.gen;

import com.jy.medusa.gaze.utils.MedusaDateUtils;
import com.jy.medusa.gaze.utils.MedusaCommonUtils;
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
public class GenService {

    private String entityPath;
    private String servicePath;
    private String serviceImplPath;
    private String mapperPath;

    private String entityName;

    private String mixMapper = Home.mixMapper;

    private List<String> markServiceList;
    private List<String> markServiceImplList;
    private List<String> markMapperList;
    private String tag;//标记 mark

    public GenService(String tableName, String entityPath, String servicePath, String serviceImplPath, String mapperPath) {
        this.entityPath = entityPath;
        this.servicePath = servicePath;
        this.serviceImplPath = serviceImplPath;
        this.mapperPath = mapperPath;
        this.entityName = MedusaGenUtils.upcaseFirst(tableName);

        this.tag = Home.tag;

        this.markServiceList = MedusaGenUtils.genTagStrList(entityName + "Service.java", servicePath, tag, "service");
        this.markServiceImplList = MedusaGenUtils.genTagStrList(entityName + "ServiceImpl.java", serviceImplPath, tag, "serviceImpl");
    }

    public void process() {

        try {
            //写入service 和 impl

            File file;


            //service
            String path = Home.proJavaPath + servicePath.replaceAll("\\.", "/");
            file = new File(path);
            if(!file.exists()) {file.mkdirs();}
            String resPath1 = path + "/" + entityName + "Service.java";

            //如果目标文件已存在 则跳过 add by SuperScorpion on 20230221
            File resPathFile1 = new File(resPath1);
            if(resPathFile1.exists()) {
                System.out.println("Medusa: " + entityName + "Service.java" + " 文件已存在 已跳过生成...");
                return;
            }
            MedusaCommonUtils.writeString2File(resPathFile1, process1(), "UTF-8");


            //serviceImpl
            String pathImp = Home.proJavaPath + serviceImplPath.replaceAll("\\.", "/");
            file = new File(pathImp);
            if(!file.exists()) {file.mkdirs();}
            String resPath2 = pathImp + "/" + entityName + "ServiceImpl.java";

            //如果目标文件已存在 则跳过 add by SuperScorpion on 20230221
            File resPathFile2 = new File(resPath2);
            if(resPathFile2.exists()) {
                System.out.println("Medusa: " + entityName + "ServiceImpl.java" + " 文件已存在 已跳过生成...");
                return;
            }
            MedusaCommonUtils.writeString2File(resPathFile2, process2(), "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * service
     * @return 返回值类型
     */
    private String process1() {

        String medusaStarterServicePacName = Home.checkBaseServiceSwitch() ? "" : "import com.ysl.medusa.base.BaseService;\n";

        StringBuilder sbb = new StringBuilder();

        sbb.append("package " + servicePath + ";\r\n\r\n");

        sbb.append("import " + entityPath + "." + entityName + Home.entityNameSuffix + ";\r\n\r\n");

        sbb.append(medusaStarterServicePacName);
        /*sbb.append("import java.util.List;\r\n");*/
        //sbb.append("public interface "+ entityName +"Service {\r\n");

        //添加作者
        sbb.append("/**\r\n");
        sbb.append(" * Created by " + Home.author + " on " + MedusaDateUtils.convertDateToStr(new Date(), null) + "\r\n");
        sbb.append(" */\r\n");

        sbb.append("public interface "+ entityName +"Service extends BaseService<" + entityName+ Home.entityNameSuffix + "> {\r\n");

/*        sbb.append("\t" + entityName + " selectById(" + entityName + " entity);\r\n\r\n");
        sbb.append("\tvoid save" + entityName + "(" + entityName + " entity);\r\n\r\n");
        sbb.append("\tvoid update" + entityName + "(" + entityName + " entity);\r\n\r\n");
        sbb.append("\tvoid delete" + entityName + "(" + entityName + " entity);\r\n\r\n");
        sbb.append("\tvoid deleteMulti" + entityName + "(List<Integer> ids, Class<" + entityName + "> t);\r\n");*/

        MedusaGenUtils.processAllRemains(markServiceList, sbb, tag, "service");

        sbb.append("}");

        return sbb.toString();
    }

    /**
     * serviceImpl
     * @return 返回值类型
     */
    private String process2() {

        String medusaStarterServiceImplPacName = Home.checkBaseServiceSwitch() ? "" : "import com.ysl.medusa.base.BaseServiceImpl;\n";

        StringBuilder sbb = new StringBuilder();

        sbb.append("package " + serviceImplPath + ";\r\n\r\n");

        sbb.append("import " + entityPath + "." + entityName + Home.entityNameSuffix + ";\r\n");
        sbb.append("import javax.annotation.Resource;\r\n");
        sbb.append("import " + mapperPath + "." + entityName + "Mapper;\r\n");
        sbb.append("import " + servicePath + "." + entityName + "Service;\r\n\r\n");
//        sbb.append("import java.util.List;\r\n");
//        sbb.append("import " + baseMapperPath + ";\r\n\r\n");
        sbb.append(medusaStarterServiceImplPacName);
        sbb.append("import " + "org.springframework.stereotype.Service" + ";\r\n\r\n");


        //添加作者
        sbb.append("/**\r\n");
        sbb.append(" * Created by " + Home.author + " on " + MedusaDateUtils.convertDateToStr(new Date(), null) + "\r\n");
        sbb.append(" */\r\n");

        sbb.append("@Service\r\n");

        sbb.append("public class " + entityName + "ServiceImpl extends BaseServiceImpl<" + entityName + Home.entityNameSuffix + "> implements " + entityName + "Service {\r\n\r\n");

        sbb.append("\t@Resource\r\n");
        sbb.append("\tprivate " + entityName + "Mapper " + MedusaGenUtils.lowcaseFirst(entityName) + "Mapper;\r\n\r\n");
        /*sbb.append("\t@Resource\r\n");
        sbb.append("\tprivate Mapper<" + entityName + ",Integer> superMapper;\r\n\r\n");

        sbb.append("\t@Override\r\n");
        sbb.append("\tpublic " + entityName + " selectById(" + entityName + " entity) {\r\n");
        sbb.append("\t\treturn superMapper.selectOneById(entity);\r\n");
        sbb.append("\t}\r\n\r\n");

        sbb.append("\t@Override\r\n");
        sbb.append("\tpublic void save" + entityName + "(" + entityName + " entity) {\r\n");
        sbb.append("\t\tsuperMapper.create(entity);\r\n");
        sbb.append("\t}\r\n\r\n");

        sbb.append("\t@Override\r\n");
        sbb.append("\tpublic void update" + entityName + "(" + entityName + " entity) {\r\n");
        sbb.append("\t\tsuperMapper.modify(entity);\r\n");
        sbb.append("\t}\r\n\r\n");

        sbb.append("\t@Override\r\n");
        sbb.append("\tpublic void delete" + entityName + "(" + entityName + " entity) {\r\n");
        sbb.append("\t\tsuperMapper.removeById(entity);\r\n");
        sbb.append("\t}\r\n\r\n");

        sbb.append("\t@Override\r\n");
        sbb.append("\tpublic void deleteMulti" + entityName + "(List<Integer> ids, Class<" + entityName + "> t) {\r\n");
        sbb.append("\t\tsuperMapper.removeOfBatch(ids, t);\r\n");
        sbb.append("\t}\r\n");*/

        MedusaGenUtils.processAllRemains(markServiceImplList, sbb, tag, "serviceImpl");

        sbb.append("}");

        return sbb.toString();
    }
}
