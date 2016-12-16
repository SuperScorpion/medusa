package com.jy.medusa.generator;

import com.jy.medusa.utils.MyUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by neo on 16/7/27.
 */
public class GenBaseService {

    private String servicePath;

    public GenBaseService(String servicePath, String tag){
        this.servicePath = servicePath;

        this.tag = tag;

        this.markServiceList = MyGenUtils.genTagStrList("BaseService.java", servicePath, tag, "service");
        this.markServiceImplList = MyGenUtils.genTagStrList("BaseServiceImpl.java", servicePath, tag, "serviceImpl");
    }

    public void process(){

        try {
            //写入service 和 impl
            String path = System.getProperty("user.dir") + "/src/main/java/" + servicePath.replaceAll("\\.", "/");
            File file = new File(path);
            if(!file.exists()){
                file.mkdirs();
            }
            String resPath1 = path + "/" + "BaseService.java";
            String resPath2 = path + "/" + "BaseServiceImpl.java";
            MyUtils.writeString2File(new File(resPath1), process1(), "UTF-8");
            MyUtils.writeString2File(new File(resPath2), process2(), "UTF-8");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * service
     * @return
     */
    private String process1() {

        StringBuilder sbb = new StringBuilder();

        sbb.append("package " + servicePath + ";\r\n\r\n");

        sbb.append("import java.util.List;\n" +
                "\n" +
                "public interface BaseService<T> {\n" +
                "\n" +
                "\tint selectCount(T entity, Object... ps);\n" +
                "\n" +
                "\tT selectOne(T entity, Object... ps);\n" +
                "\n" +
                "\tT selectById(Integer id, Object... ps);\n" +
                "\n" +
                "\tList<T> selectListBy(T entity, Object... ps);\n" +
                "\n" +
                "\tint saveOrUpdate(T entity);\n" +
                "\n" +
                "\tint save(T entity);\n" +
                "\n" +
                "\tint saveBatch(List<T> obs);\n" +
                "\n" +
                "\tint update(T entity);\n" +
                "\n" +
                "\tint updateSelective(T entity);\n" +
                "\n" +
                "\tint deleteById(Integer id);\n" +
                "\n" +
                "\tint deleteBatch(List<Integer> ids);\n" +
                "\n" +
                "\tint deleteBy(T entity);\n" +
                "\n" +
                "\tList<T> selectByGaze(T entity, Object... ps);\n" +
                "}");

        MyGenUtils.processAllRemains(markServiceList, sbb, tag, "service");

        return sbb.toString();
    }

    /**
     * serviceImpl
     * @return
     */
    private String process2() {

        StringBuilder sbb = new StringBuilder();

        sbb.append("package " + servicePath + ";\r\n\r\n");

        sbb.append("import " + Home.mixMapper + ";\n" +
                "import org.slf4j.Logger;\n" +
                "import org.slf4j.LoggerFactory;\n" +
                "import org.springframework.beans.factory.annotation.Autowired;\n" +
                "import org.springframework.stereotype.Service;\n" +
                "import java.util.List;\n" +
                "\n" +
                "@Service\n" +
                "public abstract class BaseServiceImpl<T> implements BaseService<T> {\n" +
                "\n" +
                "\tprivate static final Logger logger = LoggerFactory.getLogger(BaseServiceImpl.class);\n" +
                "\n" +
                "\t@Autowired\n" +
                "\tprotected Mapper<T> mapper;\n" +
                "\n" +
                "\tpublic int selectCount(T entity, Object... ps) {\n" +
                "\t\treturn mapper.selectCount(entity, ps);\n" +
                "\t}\n" +
                "\n" +
                "\tpublic T selectOne(T entity, Object... ps) {\n" +
                "\t\treturn mapper.selectOne(entity, ps);\n" +
                "\t}\n" +
                "\n" +
                "\tpublic T selectById(Integer id, Object... ps) {\n" +
                "\t\treturn mapper.selectByPrimaryKey(id, ps);\n" +
                "\t}\n" +
                "\n" +
                "\tpublic List<T> selectListBy(T entity, Object... ps) {\n" +
                "\t\treturn mapper.select(entity, ps);\n" +
                "\t}\n" +
                "\n" +
                "\tpublic int saveOrUpdate(T entity) {\n" +
                "\t\treturn 0;//TODO\n" +
                "\t}\n" +
                "\n" +
                "\tpublic int save(T entity) {\n" +
                "\t\treturn mapper.insertSelective(entity);\n" +
                "\t}\n" +
                "\n" +
                "\tpublic int saveBatch(List<T> obs) {\n" +
                "\t\treturn mapper.insertBatch(obs);\n" +
                "\t}\n" +
                "\n" +
                "\tpublic int update(T entity) {\n" +
                "\t\treturn mapper.updateByPrimaryKey(entity);\n" +
                "\t}\n" +
                "\n" +
                "\tpublic int updateSelective(T entity) {\n" +
                "\t\treturn mapper.updateByPrimaryKeySelective(entity);\n" +
                "\t}\n" +
                "\n" +
                "\tpublic int deleteById(Integer id) {\n" +
                "\t\treturn mapper.deleteByPrimaryKey(id);\n" +
                "\t}\n" +
                "\n" +
                "\tpublic int deleteBatch(List<Integer> ids) {\n" +
                "\t\treturn mapper.deleteBatch(ids);\n" +
                "\t}\n" +
                "\n" +
                "\tpublic int deleteBy(T entity) {\n" +
                "\t\treturn mapper.delete(entity);\n" +
                "\t}\n" +
                "\n" +
                "\tpublic List<T> selectByGaze(T entity, Object... ps) {\n" +
                "\t\treturn mapper.medusaGaze(entity, ps);\n" +
                "\t}\n" +
                "}");

        MyGenUtils.processAllRemains(markServiceImplList, sbb, tag, "serviceImpl");

        return sbb.toString();
    }

    private List<String> markServiceList;
    private List<String> markServiceImplList;
    private String tag;//标记 mark

}
