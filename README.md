# medusa
mybatis mapper
<br/>
<br/>
程序结合使用拦截器实现具体的执行Sql,完全使用原生的Mybatis进行操作,在基础上只做增强.没有一个Mapper的xml配置文件,但是却可以做到每个Mapper对应上百行xml才能完成的诸多功能,核心在于提高开发人员的效率.<br/>
<br/>
一.代码生成模块<br/>
1.1 能够生成entity mapper xml service controller层 不需要再手动编写基础代码.<br/>
1.2 重新生成代码时可自动保留上次标记代码及智能替换相应代码的功能.<br/>
<br/>
二.通用mapper模块<br/>
只需要继承通用mapper即可拥有基础的crud功能.<br/>
2.1 支持原生mybatis 支持级联查询association.<br/>
2.2 除了普通的条件查询还新增了 like、 between、 is null、single、not in等字段复合查询.<br/>
2.3 增加每个查询功能的可选字段功能.<br/>
2.4 使用了concurrentHashmap作缓存.<br/>
2.5 内置page分页功能 解决现有pagehelper不支持最新版本mybatis jar版本.<br/>
2.6 增加批量insert以及批量update.<br/>
2.7 纯血统 只依赖mybatis一个jar.<br/>
2.8 支持mybatis xml热部署.<br/>
<br/>
三.参数校验框架<br/>
3.1 aspectj jar完成aop对controller层的参数校验值.<br/>
<br/>
<br/>
<br/>
<br/>
通用mapper 使用说明文档<br/>
<br/>
<br/>
一. 使用步骤<br/>
1.1 新建一个空项目<br/>
<br/>
1.2 先将medusa的jar包添加至项目路径下<br/>
<br/>
1.3 medusa.properties 添加至resource文件夹下（源码resource目录中有exp）<br/>
<br/>
1.4 执行方法 new Home("medusa.properties").process();<br/>
<br/>
1.5 基础crud各层次代码便已经生成好了<br/>
<br/>
1.6 com.jy.medusa.interceptor.MyInterceptor需要添加到spring配置文件的org.mybatis.spring.SqlSessionFactoryBean的plugins属性里<br/>
<br/>
<br/>
1.7 至此 基本的crud功能便能使用。<br/>
<br/>
<br/>
二. 如果需要参数校验功能则需要加入下列两行代码至xml<br/>
< aop:aspectj-autoproxy /><br/>
< bean class="com.jy.medusa.validator.AnnotationHandler" /><br/>
<br/>
然后 controller 或者是 service 方法上 添加 注解 @ConParamValidator 方法参数添加相应的校验注解<br/>
exp:<br/>
@ConParamValidator<br/>
@RequestMapping(value = "/index.json", method = RequestMethod.GET)<br/>
@ResponseBody<br/>
public JSONObject index(@RequestParam @Length(max=1, message = "wtfuck") Integer uid, @Valid Users users, ErrorInfo info){...}<br/>
实体类属性参数记得加入@Valid标签.<br/>
<br/>
三. 在spring配置文件里添加 可使用热加载 mybatis xml 功能<br/>
< bean id="hotspotReloader" class="com.jy.medusa.stuff.reload.MyMapperRefresh">
   <constructor-arg index="0" ref="sqlSessionFactory"/>
   <constructor-arg index="1" value="com.xxx.xxxx.persistence.xml"/>
   <constructor-arg index="2" value="3600"/>
< /bean><br/>
第一个参数为 sessionfactory<br/>
第二个参数为 xml的包路径所在位<br/>
第三个参数为 刷新间隔时间秒<br/>
<br/>
Tips<br/>
再次生成的时候java、文件需要用//mark //mark保存你自己需要保存下来的代码 xml文件会自动地保留变动过的内容段<br/>
entity是必须生成的包 如果其他包不想生成可以不填写<br/>
<br/>
<br/>
<br/>
条件查询功能<br/>
<br/>
#not null<br/>
notNullParam(String c, Boolean v)<br/>
#单个字段的查询条件<br/>
singleParam(String c, Object v)<br/>
#小于<br/>
lessThanParam(String c, Object v)<br/>
#小于等于<br/>
lessEqualParam(String c, Object v)<br/>
#大于<br/>
greatThanParam(String c, Object v)<br/>
#大于等于<br/>
greatEqualParam(String c, Object v)<br/>
#between<br/>
betweenParam(String c, Object start, Object end)<br/>
#模糊查询<br/>
likeParam(String c, String v)<br/>
#not in<br/>
notInParam(String c, List v, Boolean p)<br/>
<br/>
exp:<br/>
1.复合条件查询<br/>
<br/>
Users s = new Users();<br/>
s.setName("xxx");<br/>
<br/>
MyRestrictions mr = MyRestrictions.getMyRestrctions()<br/>
      .betweenParam("created_at", MyDateUtils.convertStrToDate("2016-07-01 12:12:13"), null)<br/>
      .betweenParam("updated_at", MyDateUtils.convertStrToDate("2016-07-01 12:12:13"), null)<br/>
      .likeParam("name", "xxx").greatEqualParam("home_area", 70);<br/>
      <br/>
Pager<Users> p = MyRestrictions.getPager().setPageSize(7);<br/>
<br/>
List<Users> z = bbbService.selectByCondition(s, "id, name, homeArea", p, mr);<br/>
<br/>
Tips:       betweenParam 后的参数不填写的话 默认为 new date();<br/>
<br/>
2.通过实体的某一字段来查询的<br/>
Pager<Users> p = MyRestrictions.getPager().setPageSize(7);<br/>
MyRestrictions mrp = MyRestrictions.getMyRestrctions().singleParam("name", "xxx");<br/>
List<Users> z = userService.selectByGaze("id, name, home", p, mrp);<br/>
<br/>
...<br/>
<br/>
3.批量删除功能<br/>
List o = new ArrayList();<br/>
o.add(58);<br/>
o.add(62);<br/>
o.add(61);<br/>
int i = xxxService.deleteMulti(o);<br/>
<br/>
...<br/>
<br/>
tips: 所有方法都选择查询部分字段 可以用数据库字段名或者属性名 有容错机制<br/>
MyRestrictions是非线程安全的 提供clear方法可重复利用<br/>
Pager类为内部分页实现 可插拔式<br/>
<br/>
批量新增和批量更新可指定字段 并且批量新增可回写所有id<br/>
<br/>
其它的普通方法则跟现用的通用mapper一致 拥有原生的级联<br/>
<br/>
<br/>
<br/>
medusa.properties参数参考<br/>
<br>
#生成的根包路径<br/>
medusa.packagePath = com.jy.herms <br/>
<br/>
###需要生成的表名称 用逗号分隔 不填则生成所有标<br/>
medusa.tableName = xx,xxx,xxxx <br/>
<br/>
###根路径下的实体包名<br/>
medusa.entitySuffix = entity <br/>
<br/>
###生成service的路径包名<br/>
medusa.serviceSuffix = service<br/>
<br/>
###生成serviceImpl的路径包名<br/>
medusa.serviceImplSuffix = service.impl <br/>
<br/>
###生成的mapper的路径包名<br/>
medusa.mapperSuffix = persistence <br/>
<br/>
###生成的xml路径包名<br/>
medusa.xmlSuffix = persistence.xml <br/>
<br/>
###文件生成时添加的作者名称<br/>
medusa.author = admins <br/>
<br/>
###是否需要延迟加载级联属性的 为空则不启用它(一般不写)<br/>
medusa.lazyLoad = y <br/>
<br/>
###是否生成基础的 不写则不启用它(只在第一次生成时写)<br/>
medusa.baseServiceSwitch = y <br/>
<br/>
###是否在entity类上继承序列化接口 不写则不启用它(一般不写)<br/>
medusa.entitySerializable =  <br/>
<br/>
###生成关系关联属性字段(在需要级联功能才写)<br/>
medusa.associationColumn= user_id <br/>
<br/>
###生成的关系关联属性表 是否需要添加复数后缀s(表名是复数命名则写s)<br/>
medusa.pluralAssociation = s <br/>
<br/>
###生成实体文件的后缀名 (一般不写)<br/>
medusa.entityNameSuffix = <br/>
<br/>
###是否根据模版来生成 如果不为空则使用模版 如果找不到路径则使用默认模版<br/>
medusa.ftlDirPath = xxx<br/>
<br/>
###如果使用模版则不使用下面三个配置###<br/>
<br/>
###controlJsonSuffix和controlMortalSuffix二选一即可 区别在于一个是json类型一个是页面跳转的类型<br/>
<br/>
###controller包的名称(二选一)<br/>
medusa.controlJsonSuffix = controller <br/>
<br/>
###controller包的名称(二选一)<br/>
medusa.controlMortalSuffix = controller <br/>
<br/>
###java文件中需要在下次生成时保留的代码段的起末标记 //mark … //mark<br/>
medusa.tag = mark<br/>
<br/>
###数据库四项配置<br/>
jdbc.driver=com.mysql.jdbc.Driver<br/>
jdbc.url=jdbc:mysql://localhost:3306/cms?useUnicode=true&characterEncoding=UTF-8<br/>
jdbc.username=root<br/>
jdbc.password=
