package com.jy.medusa.generator.gen;

import com.jy.medusa.gaze.utils.MedusaCommonUtils;
import com.jy.medusa.generator.Home;
import com.jy.medusa.generator.MedusaGenUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by SuperScorpion on 16/7/19.
 */
public class GenBaseServiceAndImpl {

    private List<String> markServiceList;
    private List<String> markServiceImplList;
    private String tag;//标记 mark

    private String servicePath;
    private String serviceImplPath;

    public GenBaseServiceAndImpl(String servicePath, String serviceImplPath) {
        this.servicePath = servicePath;
        this.serviceImplPath = serviceImplPath;

        this.tag = Home.tag;

        this.markServiceList = MedusaGenUtils.genTagStrList("BaseService.java", servicePath, tag, "service");
        this.markServiceImplList = MedusaGenUtils.genTagStrList("BaseServiceImpl.java", serviceImplPath, tag, "serviceImpl");
    }

    public Boolean process() {
        try {
            //写入service 和 impl
            return processService() && processServiceImpl();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }


    private Boolean processService() throws IOException {
        String path = Home.proJavaPath + servicePath.replaceAll("\\.", "/");

        File file1 = new File(path);
        if(!file1.exists()) file1.mkdirs();

        String resPath1 = path + "/" + "BaseService.java";
        //如果目标文件已存在 则跳过 add by SuperScorpion on 20230221
        File resPathFile1 = new File(resPath1);
        if(resPathFile1.exists()) {
            System.out.println("Medusa: " + "BaseService.java" + " 文件已存在 将跳过生成...");
            return false;
        }
        MedusaCommonUtils.writeString2File(resPathFile1, process11(), "UTF-8");

        return true;
    }

    private Boolean processServiceImpl() throws IOException {
        String pathImp = Home.proJavaPath + serviceImplPath.replaceAll("\\.", "/");

        File file2 = new File(pathImp);
        if(!file2.exists()) file2.mkdirs();

        String resPath2 = pathImp + "/" + "BaseServiceImpl.java";
        //如果目标文件已存在 则跳过 add by SuperScorpion on 20230221
        File resPathFile2 = new File(resPath2);
        if(resPathFile2.exists()) {
            System.out.println("Medusa: " + "BaseServiceImpl.java" + " 文件已存在 已跳过生成...");
            return false;
        }
        MedusaCommonUtils.writeString2File(resPathFile2, process22(), "UTF-8");

        return true;
    }


//    /**
//     * service
//     * @return 返回值类型
//     */
//    private String process1() {
//
//        StringBuilder sbb = new StringBuilder();
//
//        sbb.append("package " + servicePath + ";\r\n\r\n");
//
//        sbb.append("import java.util.List;\n" +
//                "\n" +
//                "import com.alibaba.fastjson.JSONObject;\n" +
//                "\n" +
//                "public interface BaseService<T> {\n" +
//                "\n" +
//                "\tint selectCount(Object... ps);\n" +
//                "\n" +
//                "\tList<T> selectAll(Object... ps);\n" +
//                "\n" +
//                "\tT selectOne(T entity, Object... ps);\n" +
//                "\n" +
//                "\tList<T> selectByIds(List<Object> ids, Object... ps);\n" +
//                "\n" +
//                "\tT selectById(Object id, Object... ps);\n" +
//                "\n" +
//                "\tList<T> selectListBy(T entity, Object... ps);\n" +
//                "\n" +
//                "\tint saveOrUpdate(T entity);\n" +
//                "\n" +
//                "\tint saveSelective(T entity);\n" +
//                "\n" +
//                "\tint save(T entity);\n" +
//                "\n" +
//                "\tint saveBatch(List<T> obs, Object... ps);\n" +
//                "\n" +
//                "\tint update(T entity, Object... ps);\n" +
//                "\n" +
//                "\tint updateSelective(T entity);\n" +
//                "\n" +
//                "\tint updateBatch(List<T> obs, Object... ps);\n" +
//                "\n" +
//                "\tint deleteById(Object id);\n" +
//                "\n" +
//                "\tint deleteBatch(List<Object> ids);\n" +
//                "\n" +
//                "\tint deleteBy(T entity);\n" +
//                "\n" +
//                "\tList<T> selectByGazeMagic(Object... ps);\n" +
//                "\n" +
//                "\tJSONObject resultSuccess(Object result, String msg, JSONObject json);\n" +
//                "\n" +
//                "\tJSONObject resultError(Object result, String msg, JSONObject json);\n" +
//                "}");
//
//        MedusaGenUtils.processAllRemains(markServiceList, sbb, tag, "service");
//
//        return sbb.toString();
//    }
//
//    /**
//     * serviceImpl
//     * @return 返回值类型
//     */
//    private String process2() {
//
//        StringBuilder sbb = new StringBuilder();
//
//        sbb.append("package " + serviceImplPath + ";\r\n\r\n");
//
//        sbb.append("import " + Home.mixMapper + ";\n" +
//                "import " + servicePath + ".BaseService;\n" +
//                "import com.alibaba.fastjson.JSONObject;\n" +
//                "import org.slf4j.Logger;\n" +
//                "import org.slf4j.LoggerFactory;\n" +
//                "import org.springframework.beans.factory.annotation.Autowired;\n" +
//                "import org.springframework.stereotype.Service;\n" +
//                "import java.util.List;\n" +
//                "\n" +
//                "@Service\n" +
//                "public abstract class BaseServiceImpl<T> implements BaseService<T> {\n" +
//                "\n" +
//                "\tprivate static final Logger logger = LoggerFactory.getLogger(BaseServiceImpl.class);\n" +
//                "\n" +
//                "\t@Autowired\n" +
//                "\tprotected Mapper<T> mapper;\n" +
//                "\n" +
//                "\tpublic int selectCount(Object... ps) {\n" +
//                "\t\treturn mapper.selectCount(ps);\n" +
//                "\t}\n" +
//                "\n" +
//                "\tpublic List<T> selectAll(Object... ps) {\n" +
//                "\t\treturn mapper.selectAll(ps);\n" +
//                "\t}\n" +
//                "\n" +
//                "\tpublic T selectOne(T entity, Object... ps) {\n" +
//                "\t\treturn mapper.selectOne(entity, ps);\n" +
//                "\t}\n" +
//                "\n" +
//                "\tpublic List<T> selectByIds(List<Object> ids, Object... ps) {\n" +
//                "\t\treturn mapper.selectByPrimaryKeyBatch(ids, ps);\n" +
//                "\t}\n" +
//                "\n" +
//                "\tpublic T selectById(Object id, Object... ps) {\n" +
//                "\t\treturn mapper.selectByPrimaryKey(id, ps);\n" +
//                "\t}\n" +
//                "\n" +
//                "\tpublic List<T> selectListBy(T entity, Object... ps) {\n" +
//                "\t\treturn mapper.select(entity, ps);\n" +
//                "\t}\n" +
//                "\n" +
//                "\tpublic int saveOrUpdate(T entity) {\n" +
//                "\t\treturn 0;//TODO\n" +
//                "\t}\n" +
//                "\n" +
//                "\tpublic int saveSelective(T entity) {\n" +
//                "\t\treturn mapper.insertSelective(entity);\n" +
//                "\t}\n" +
//                "\n" +
//                "\tpublic int save(T entity) {\n" +
//                "\t\treturn mapper.insert(entity);\n" +
//                "\t}\n" +
//                "\n" +
//                "\tpublic int saveBatch(List<T> obs, Object... ps) {\n" +
//                "\t\treturn mapper.insertBatch(obs, ps);\n" +
//                "\t}\n" +
//                "\n" +
//                "\tpublic int update(T entity, Object... ps) {\n" +
//                "\t\treturn mapper.updateByPrimaryKey(entity, ps);\n" +
//                "\t}\n" +
//                "\n" +
//                "\tpublic int updateSelective(T entity) {\n" +
//                "\t\treturn mapper.updateByPrimaryKeySelective(entity);\n" +
//                "\t}\n" +
//                "\n" +
//                "\tpublic int updateBatch(List<T> obs, Object... ps) {\n" +
//                "\t\treturn mapper.updateByPrimaryKeyBatch(obs, ps);\n" +
//                "\t}\n" +
//                "\n" +
//                "\tpublic int deleteById(Object id) {\n" +
//                "\t\treturn mapper.deleteByPrimaryKey(id);\n" +
//                "\t}\n" +
//                "\n" +
//                "\tpublic int deleteBatch(List<Object> ids) {\n" +
//                "\t\treturn mapper.deleteBatch(ids);\n" +
//                "\t}\n" +
//                "\n" +
//                "\tpublic int deleteBy(T entity) {\n" +
//                "\t\treturn mapper.delete(entity);\n" +
//                "\t}\n" +
//                "\n" +
//                "\tpublic List<T> selectByGazeMagic(Object... ps) {\n" +
//                "\t\treturn mapper.medusaGazeMagic(ps);\n" +
//                "\t}\n" +
//                "\n");
//
//        sbb.append("\tpublic JSONObject resultSuccess(Object result, String msg, JSONObject json) {\n" +
//                "\t\tjson = json == null ? new JSONObject() : json;\n" +
//                "\t\tjson.put(\"data\", result);\n" +
//                "\t\tjson.put(\"result\",0);\n" +
//                "\t\tjson.put(\"msg\", msg);\n" +
//                "\t\treturn json;\n" +
//                "\t}\n" +
//                "\n" +
//                "\tpublic JSONObject resultError(Object result, String msg, JSONObject json) {\n" +
//                "\t\tjson = json == null ? new JSONObject() : json;\n" +
//                "\t\tjson.put(\"data\", result);\n" +
//                "\t\tjson.put(\"result\",1);\n" +
//                "\t\tjson.put(\"msg\", msg);\n" +
//                "\t\treturn json;\n" +
//                "\t}\n" +
//                "}");
//
//        MedusaGenUtils.processAllRemains(markServiceImplList, sbb, tag, "serviceImpl");
//
//        return sbb.toString();
//    }

    /**
     * BaseService
     * @return 返回值类型
     */
    private String process11() {

        StringBuilder sbb = new StringBuilder();

        sbb.append("package " + servicePath + ";\r\n\r\n");

        sbb.append("import com.alibaba.fastjson.JSONObject;\n" +
                "import com.jy.medusa.gaze.commons.BaseServiceMedusa;\n" +
                "\n" +
                "public interface BaseService<T> extends BaseServiceMedusa<T> {\n" +
                "\n" +
                "\tJSONObject resultSuccess(Object result, String msg, JSONObject json);\n" +
                "\n" +
                "\tJSONObject resultError(Object result, String msg, JSONObject json);\n" +
                "\n" +
                "\t//TODO 用户自定义接口\n" +
                "}");

        MedusaGenUtils.processAllRemains(markServiceList, sbb, tag, "service");

        return sbb.toString();
    }

    /**
     * BaseServiceImpl
     * @return 返回值类型
     */
    private String process22() {

        StringBuilder sbb = new StringBuilder();

        sbb.append("package " + serviceImplPath + ";\r\n\r\n");

        sbb.append("import com.alibaba.fastjson.JSONObject;\n" +
                "import com.jy.medusa.gaze.commons.BaseServiceImplMedusa;\n" +
//                "import " + Home.mixMapper + ";\n" +
                "import " + servicePath + ".BaseService;\n" +
//                "import org.slf4j.Logger;\n" +
//                "import org.slf4j.LoggerFactory;\n" +
//                "import org.springframework.beans.factory.annotation.Autowired;\n" +
                "\n" +
                "public abstract class BaseServiceImpl<T> extends BaseServiceImplMedusa<T> implements BaseService<T> {\n" +
//                "\n" +
//                "\tprivate static final Logger logger = LoggerFactory.getLogger(BaseServiceImpl.class);\n" +
//                "\n" +
//                "\t@Autowired\n" +
//                "\tprotected void init (Mapper<T> mapper) {\n" +
//                "\t\tsuper.initMapper(mapper);\n" +
//                "\t}\n" +
                "\n");
        sbb.append("\tpublic JSONObject resultSuccess(Object result, String msg, JSONObject json) {\n" +
                "\t\tjson = json == null ? new JSONObject() : json;\n" +
                "\t\tjson.put(\"data\", result);\n" +
                "\t\tjson.put(\"result\",0);\n" +
                "\t\tjson.put(\"msg\", msg);\n" +
                "\t\treturn json;\n" +
                "\t}\n" +
                "\n" +
                "\tpublic JSONObject resultError(Object result, String msg, JSONObject json) {\n" +
                "\t\tjson = json == null ? new JSONObject() : json;\n" +
                "\t\tjson.put(\"data\", result);\n" +
                "\t\tjson.put(\"result\",1);\n" +
                "\t\tjson.put(\"msg\", msg);\n" +
                "\t\treturn json;\n" +
                "\t}\n" +
                "\n" +
                "\t//TODO 用户自定义接口\n" +
                "}");

        MedusaGenUtils.processAllRemains(markServiceImplList, sbb, tag, "serviceImpl");

        return sbb.toString();
    }

}
