# medusa
Mybatis Mapper Plugin
<br/>
<br/>
这是一个mybatis的插件或者中间件,在mybatis上进行浅封装,程序结合使用拦截器实现具体的执行Sql,
完全保留了Mybatis原生特性,在其基础上只做增强.没有一个Mapper的xml配置文件,
但是却可以做到每个Mapper对应上百行xml才能完成的诸多功能,核心在于提高开发人员CRUD的效率.<br/>
<br/>
按功能可以分为三大块 :<br/><br/>
一. 代码生成模块<br/>
1.1 能够生成entity mapper xml service controller层 不需要再手动编写基础代码.<br/>
1.2 可根据自己DIY的FTL模版进行个性化生成代码文件.<br/>
1.3 重新生成代码时可自动保留上次标记代码及智能替换相应代码的功能(非FTL模版生成）.<br/>
<br/>
二. 通用mapper模块<br/>
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
三. 参数校验框架<br/>
3.1 aspectj jar完成aop对controller层的参数校验值.<br/>
3.2 aop即时返回校验的错误信息.<br/>
<br/>
<br/>
<br/>
<br/>
快速开始<br/>
<br/>
一. 使用步骤<br/>
1.1 新建一个空项目<br/>
<br/>
1.2 先将medusa的maven依赖加入pom<br/>
<br/>
1.3 需要新增yml文件里相关的medusa配置（源码resource目录中有exp）<br/>
<br/>
1.4 Main方法里执行 new Home().process();<br/>
<br/>
1.5 基础crud各层次代码便已经生成好了<br/>
<br/>
1.6 com.jy.medusa.gaze.interceptor.MyInterceptor需要添加到spring配置文件的org.mybatis.spring.SqlSessionFactoryBean的plugins属性里<br/>
<br/>
1.7 至此 基本的crud功能便能使用。<br/>
<br/>
<br/>
<br/>
详细说明请参考medusa具体使用文档<br/>
<br>
版本日志地址:<br>
https://github.com/SuperScorpion/medusa/wiki/Medusa-Version-Logs
<br>
<br>
如有问题请联系作者
