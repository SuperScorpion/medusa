# medusa
mybatis mapper

程序结合使用拦截器实现具体的执行Sql,完全使用原生的Mybatis进行操作.没有一个Mapper的xml配置文件,但是却可以做到每个Mapper对应上百行xml才能完成的诸多功能.

一.代码生成模块
1.1 能够生成entity mapper xml service controller层 不需要再手动编写基础代码.
1.2 重新生成代码时可自动保留上次标记代码及智能替换相应代码的功能.
1.3 (自定义模版功能待完善)

二.通用mapper模块
只需要继承通用mapper即可拥有基础的crud功能.
2.1 支持原生mybatis 支持级联查询association.
2.2 除了普通的条件查询还新增了 like、 between、 is null、single、not in等字段复合查询.
2.3 增加每个查询功能的可选字段功能.
2.4 使用了concurrentHashmap作缓存.
2.5 内置page分页功能 解决现有pagehelper不支持最新版本mybatis jar版本.
2.6 增加批量insert以及批量update.
2.7 纯血统 只依赖mybatis一个jar.
2.8 支持mybatis xml热部署.

三.参数校验框架
3.1 aspectj jar完成aop对controller层的参数校验值.


<br/>
<br/>


通用mapper 使用说明文档
<br/>
一. 基本功能
新建一个空项目
<br/>
先将medusa的jar包添加至项目路径下
<br/>
medusa.properties 添加至resource文件夹下
<br/>
执行方法 new Home("medusa.properties").process();
<br/>
基础crud各层次代码便已经生成好了
<br/>
com.jy.medusa.interceptor.MyInterceptor需要添加到org.mybatis.spring.SqlSessionFactoryBean
<br/>
exp:
<br/>
<bean id="sqlSessionFactory" class="org.mybatis.spring.SqlSessionFactoryBean">
       <property name="typeAliasesPackage" value="com.jy.herms.entity" />
       <property name="dataSource" ref="dataSource" />
   <property name="mapperLocations">
      <array>
         <value>classpath:com/x/xx/xxx/xml/*.xml</value>
      </array>
   </property>
   <property name="plugins">
      <array>
         <bean class="com.jy.medusa.interceptor.MyInterceptor"/>
      </array>
   </property>
</bean>
<br/>
至此 基本的crud功能便能使用。
<br/>
二. 如果需要参数校验功能则需要加入下列两行代码至xml
<aop:aspectj-autoproxy proxy-target-class="true"/>
<bean class="com.jy.medusa.validator.AnnotationHandler"/>
<br/>
然后 controller 或者是 service 方法上 添加 注解 @ConParamValidator
exp:       @ConParamValidator(entityClass = Users.class)
Users类 属性记得加入@Length等标签.
<br/>
三. 在spring配置文件里添加 可使用热加载 mybatis xml 功能
<bean id="hotspotReloader" class="com.jy.medusa.stuff.hotload.MyMapperRefresh">
   <constructor-arg index="0" ref="sqlSessionFactory"/>
   <constructor-arg index="1" value="com.xxx.xxxx.persistence.xml"/>
   <constructor-arg index="2" value="3600"/>
</bean>
<br/>
第一个参数为 sessionfactory
第二个参数为 xml的包路径所在位
第三个参数为 刷新间隔时间秒
<br/>

Tips
再次生成的时候java、文件需要用//mark //mark保存你自己需要保存下来的代码 xml文件会自动地保留变动过的内容段
entity是必须生成的包 如果其他包不想生成可以不填写

<br/>
<br/>

新功能
<br/>
1.复合条件查询
exp:
<br/>
Users s = new Users();
s.setName("刚刚股份大股东");
<br/>
MyRestrictions mr = MyRestrictions.getMyRestrctions()
      .betweenParam("created_at", MyDateUtils.convertStrToDate("2016-07-01 12:12:13"), null)
      .betweenParam("updated_at", MyDateUtils.convertStrToDate("2016-07-01 12:12:13"), null)
      .likeParam("name", "xxx")
      .greatEqualParam("home_area", 70);
<br/>
Pager<Users> p = MyRestrictions.getPager().setPageSize(7);
<br/>
List<Users> z = bbbService.selectByCondition(s, "id, name, homeArea", p, mr);
<br/>
Tips:       betweenParam 后的参数不填写的话 默认为 new date();
<br/>
2.通过实体的某一字段来查询的
<br/>
Pager<Users> p = MyRestrictions.getPager().setPageSize(7);
<br/>
MyRestrictions mrp = MyRestrictions.getMyRestrctions()
      .singleParam("name", "xxx");
<br/>
List<Users> z = bbbService.selectByCondition("id, name, homeArea", p, mrp);
<br/>
...
<br/>
批量删除功能
List o = new ArrayList();
o.add(58);
o.add(62);
o.add(61);
int i4 = bbbService.deleteMulti(o);
<br/>
tips: 所有方法都可以只查询部分字段 可以用数据库字段名或者是属性的名称
其它的普通方法则跟现用的通用mapper一致 拥有原生的级联
<br/>
<br/>
<br/>
<br/>
<br/>
<br/>
medusa.properties参数参考
<br/>
#生成的根包路径
medusa.packagePath = com.jy.herms 
###需要生成的表名称 用逗号分隔
medusa.tableName = xx,xxx,xxxx 
###java文件中需要在下次生成时保留的代码段的起末标记 //mark … //mark
medusa.tag = mark
<br/>
###根路径下的实体包名
medusa.entitySuffix = entity 
###生成service的路径包名
medusa.serviceSuffix = service
###生成serviceImpl的路径包名
medusa.serviceImplSuffix = service.impl 
###生成的mapper的路径包名
medusa.mapperSuffix = persistence 
###生成的xml路径包名
medusa.xmlSuffix = persistence.xml 
<br/>
###controlJsonSuffix和controlMortalSuffix二选一即可 区别在于一个是json类型一个是页面跳转的类型
<br/>
###controller包的名称(二选一)
medusa.controlJsonSuffix = controller 
<br/>
###controller包的名称(二选一)
medusa.controlMortalSuffix = controller 
<br/>
<br/>
###文件生成时添加的作者名称
medusa.author = admins 
<br/>
###是否需要延迟加载级联属性的 为空则不启用它(一般不写)
medusa.lazyLoad = y 
<br/>
###是否生成基础的 不写则不启用它(只在第一次生成时写)
medusa.baseServiceSwitch = y 
<br/>
###是否在entity类上继承序列化接口 不写则不启用它(一般不写)
medusa.entitySerializable =  
<br/>
###生成关系关联属性字段(在需要级联功能才写)
medusa.associationColumn= user_id 
<br/>
###生成的关系关联属性表 是否需要添加复数后缀s(表名是复数命名则写s)
medusa.pluralAssociation = s
<br/>
###生成实体文件的后缀名 (一般不写)
medusa.entityNameSuffix =  
<br/>
###数据库四项配置
jdbc.driver=com.mysql.jdbc.Driver
jdbc.url=jdbc:mysql://localhost:3306/cms?useUnicode=true&characterEncoding=UTF-8
jdbc.username=root
jdbc.password=
