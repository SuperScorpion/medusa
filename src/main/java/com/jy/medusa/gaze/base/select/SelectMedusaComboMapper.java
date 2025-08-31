
package com.jy.medusa.gaze.base.select;

import com.jy.medusa.gaze.provider.BaseSelectProvider;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.SelectProvider;

import java.io.Serializable;
import java.util.List;

/**
 * 通用mapper
 * 这是个大招 万能查询方法 根据多条件查询数据
 * @param <T> 实体类泛型
 * @author SuperScorpion
 */
public interface SelectMedusaComboMapper<T> {

    /**
     * 这是个大招 万能查询方法 根据多条件查询数据
     * <p>
     * tips:    mixParams参数可传入多个多种类型的对象
     * </p>
     * <ul>
     *     <li>
     *          实体 类型参数<br>
     *          效果同 select()方法
     *      </li>
     *      <li>
     *          <p>
     *          String 类型参数<br>
     *          如果查询语句为 select xxx from A 则该类型参数为中间的xxx
     *          </p>
     *      </li>
     *      <li>
     *          HashMap 类型参数<br>
     *          只能接受 key(String) value(Object) 其中的key为列sql名称或列属性名称<br>
     *          解析后为 where 列名1 = 列名1的值 and 列名2 = 列名2的值...
     *      </li>
     *      <li>
     *          MedusaLambdaMap 类型参数 {@link com.jy.medusa.gaze.stuff.param.MedusaLambdaMap}<br>
     *          只能接受 key(A::getXxx) value(Object) 由于HashMap的限制不能传入双冒号的key<br>
     *          所以使用该map 解析后为 where 列名1 = 列名1的值 and 列名2 = 列名2的值...
     *      </li>
     *      <li>
     *          MedusaLambdaRestrictions 类型参数 {@link com.jy.medusa.gaze.stuff.param.MedusaLambdaRestrictions}<br>
     *          构建各种查询条件 包含了 eq查询 like查询 between查询 等等 exp: mrs.eqParam(A::getXxx, 1) 会被解析为 where xxx = 1<br>
     *          只做了判空过滤处理 exp: xxx = null时 该条件会被过滤<br>
     *          也包含 or和and 连接条件 (复杂sql不建议使用 版本3.2.3以上)<br>
     *          exp: mrs.and(mrs.orModel().eqParam(A::getXxx, 1).eqParam(A::getZzz, 2)) 会被解析为 ... and(xxx = 1 or zzz = 2)<br>
     *          暂时只支持简单的or和and条件拼接 有效条件为最后一个orModel或andModle 后的各种基础条件项<br>
     *          exp: mrs.and(mrs.orModel().eqParam(A::getXxx, 1).eqParam(A::getZzz, 2).orModel().eqParam(A::getXxx, 3).eqParam(A::getZzz, 4)) 会被解析为 ... and(xxx = 3 or zzz = 4)<br>
     *          也包含 order by 和 group by 条件<br>
     *          exp: mrs.orderByDescParam(A::getXxx)
     *      </li>
     *      <li>
     *          MedusaLambdaColumns 类型参数 {@link com.jy.medusa.gaze.stuff.param.MedusaLambdaColumns}<br>
     *          查询出结果集的哪些可选列（一个自定义list 由于java语法限制 mixParams参数不能存在双冒号类型）<br>
     *          如果没有该参数则查询的是所有表字段
     *      </li>
     *      <li>
     *          Pager 类型参数 {@link com.jy.medusa.gaze.stuff.Pager}<br>
     *          分页时使用 一般情况传入 pageSize和pageNumber即可<br>
     *          也支持order by条件<br>
     *          方法执行完成后会回写list结果集到该pager的list属性
     *       </li>
     * </ul>
     *
     * 如果什么参数都没有那该方法会退化成 selectAll()
     *
     * @param mixParams     各种类型的参数
     *                      包含 实体类型 String HashMap MedusaLambdaMap MedusaLambdaRestrictions MedusaLambdaColumns Pager
     * @return              返回实体对象的list结果
     */
    @SelectProvider(type = BaseSelectProvider.class, method = "selectMedusaCombo")
    @ResultMap("BaseResultMap")
    List<T> selectMedusaCombo(Serializable... mixParams);
}