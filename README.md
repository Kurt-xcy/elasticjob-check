# 1.项目背景
在互联网金融行业日益发展的今天，越来越多的人通过快捷支付或各种第三方电子支付方式进行金钱交易，为确保交易的安全性，平台方（如支付宝、微信）的交易信息需要与银行方进行比对，以防止程序以及通信问题产生的误扣款、误转款等各类交易安全问题。对账的任务会在每天凌晨定时运行，需要进行任务调度，传统的方法Timer、ScheduledExecutor、Quartz均能实现基于开始时间与重复间隔的任务调度，然而，在面对较大数据量的任务调度时就显得效率低下。此时，迫切需要一种适用集群部署的任务调度方案，缓解单服务器的压力。但此时，对集群部署情况下对任务协调问题，如何做到不重不漏迫切需要一种新型的框架，elastic-job是当当网基quartz二次开发的弹性分布式任务调度系统，功能丰富强大，采用zookeeper实现分布式协调，实现任务高可用以及分片，能够较好地解决上述问题。
# 2.需求描述
## 2.1导出对账文件
需要对账的数据存放在数据库中，因此，需对数据库中的数据进行导出，导出为对账文件，对账文件中包含流水号、用户ID、卡号、交易时间、和转入转出等字段。该步分片为10片，每个分片导出1000条数据放在文件中。
名称	变量名	类型  
流水号	serialNumber	String  
用户ID	userId	String  
卡号	cardNumber	String  
交易时间	dealTime	String  
转入转出	InOrOut	Integer  
注：流水号为唯一值，生成文件中不生成重复流水号。交易时间的形式为（yyyyMMddHHmmssSSS）。转入转出为0或1，转入为1，转出为0。  
## 2.2对账文件分割
由于单个文件的数据量过大，直接进行对账会大大降低运行效率，因此需要对对账文件的分割，一般按照流水号尾号或卡号尾号进行分割，到不同的文件中。由于上一步10个分片导出10个文件，因此可以对10个文件进行读取，按照尾号分割，写入不同的文件中，每个文件中均为流水号尾号相同的数据。
## 2.3对账文件排序
排序操作主要为了提高文件比对时的效率，对流水号字段进行升序排序，同样采取分片数为10进行任务并行排序，在使用JAVA进行比对时，应该考虑将整个文件读入时由于文件过大可能产生的JVM虚拟机OOM（out of memory）问题。
## 2.4对账文件比对
对于对账文件比对，本项目截取关键的流水号、用户ID和卡号数据进行比对，以作示例。主要针对对账中出现的以下问题：  
1.数据不匹配  
2.数据遗漏  
3.数据多出或重复  
对于出现的如上问题，将错误信息写入数据库。  
数据库字段包括  
描述	字段名	类型  
主键	id	int(255)  
错误类型	type	varchar(255)  
错误文件1	wrongFile1	varchar(255)  
错误文件2	wrongFile2	varchar(255)  
文件1中的发生错误的行数	wrongLine1	int(255)  
文件2中的发生错误的行数	wrongLine2	int(255)  
错误描述	discrip	varchar(255)  
对账时间	generateTime	datetime  

# 3.技术选型
## 3.1 Elastic-job
Elastic-job具备分布式调度协调、弹性扩容缩容、失效转移、错过执行作业重触发、支持并行调度等功能，能够集群部署，也能并行执行任务，一个实例中执行失败的任务会自动在另一个实例中执行，对任务处理中出现的各种异常有较好的解决方案。
## 3.2 Zookeeper
ZooKeeper是一个分布式一致性协调服务，它是Apache Hadoop 的一个子项目，它主要是用来解决分布式应用中经常遇到的一些数据管理问题，如：统一命名服务、状态同步服务、集群管理、分布式应用配置项的管理等。  
Elastic-Job依赖ZooKeeper完成对执行任务信息的存储(如任务名称、任务参与实例、任务执行策略等)；  
Elastic-Job依赖ZooKeeper实现选举机制，在任务执行实例数量变化时(如在快速上手中的启动新实例或停止实例)，会触发选举机制来决定让哪个实例去执行该任务。
## 3.3 Mybatis
MyBatis 是支持定制化 SQL、存储过程以及高级映射的优秀的持久层框架。MyBatis 避免了几乎所有的 JDBC 代码和手动设置参数以及获取结果集。
## 3.4 Mysql
MySQL是一种关联数据库管理系统，关联数据库将数据保存在不同的表中，而不是将所有数据放在一个大仓库内，这样就增加了速度并提高了灵活性。
## 3.4 java.io
Java的核心库java.io提供了全面的IO接口。包括：文件读写、标准设备输出等。Java中IO是以流为基础进行输入输出的，所有数据被串行化写入输出流，或者从输入流读入。本项目中对文件的生成、分割、排序都大量用到了java.io包中的输入输出流。

# 4.创新点与难点
## 4.1任务的依赖
Elastic-job本身并不提供任务的依赖关系，因此，只能自己实现任务之间的依赖，项目运行后，所有的任务都会初始化，按照自身配置好的cron表达式定时执行，一种方法是凭经验，将任务进行时间上的分隔，但并不保险，本项目在数据库中定义了任务当前状态，当自身执行完毕，会自动修改数据库中当前任务状态，而依赖其的任务在得到上一任务执行完毕的数据库信息后才会开始执行。如下左图在为对账文件导入任务的状态需全为ready才可以继续执行  
![在这里插入图片描述](https://img-blog.csdnimg.cn/20200914201731355.png#pic_center)
![在这里插入图片描述](https://img-blog.csdnimg.cn/2020091420174019.png#pic_center)
          
注：在数据库中设计4个表分别对应为导入、分割、排序、对账的状态，每个表中有10条数据代表10个分片，因此，每个分片结束后可以操作自己对应表中对应的分片状态实现状态的更新。
下图FileImportJob中的Execute（）方法中导入文件结束将数据库中的状态修改为“finish”，如上右图所示  
![在这里插入图片描述](https://img-blog.csdnimg.cn/2020091420295020.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl80NDAwMTY4MQ==,size_16,color_FFFFFF,t_70#pic_center)

而排序FileDivideJob中的execute（）方法内需要未排序与分割完成才能继续执行  
![在这里插入图片描述](https://img-blog.csdnimg.cn/2020091420301571.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl80NDAwMTY4MQ==,size_16,color_FFFFFF,t_70#pic_center)

这样实现了任务间的相互依赖。
## 4.2 OOM问题
在对分割文件的排序时，需要将文件所有数据读入JVM内存中，如果文件太大，就会造成OOM问题，因此，本项目中，为避免出现OOM问题，在任务的排序时，可设置一个排序处理的行数阈值，超过该阈值，即进行文件的再分割，对再分割后的文件进行分步排序（避免一次性全部读入内存），再进行文件的合并，这样处理能够避免一次性读入太多数据，当然，这样的操作也更多的用到IO流，造成更多的时间开销。
![在这里插入图片描述](https://img-blog.csdnimg.cn/20200914203052911.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl80NDAwMTY4MQ==,size_16,color_FFFFFF,t_70#pic_center)
## 4.3 续作、重做、跳过
上文任务依赖中已经描述过，每一任务的开始与结束都会检查数据库中的上一步是否结束“finish”，以及自身状态是否为“ready”。因此，可以方便的实现续作、重做和跳过功能。  
续作：当此步骤异常结束，只需观察数据库任务执行到哪一步，对当前任务状态赋值为“ready”（若产生异常或错误，可能出现某些分片状态为“ready”未执行，某些分片状态为“finish”已经执行结束，故需全部修改为“ready”开始本步骤），则重新执行后，将会跳过已执行过的步骤（因为状态为“finish”）。  
重做：对数据库中状态进行复原，全部赋值为“ready”，重新执行即可重做。  
跳过：对需要跳过的步骤在数据库中所有分片状态赋值为“finish”，即可实现对一些步骤的跳过。  
注：此处续作、重做、跳过的操作均需要对数据库进行操作，调试过程中使用数据库管理软件Navicat进行操作，本项目也封装好了对数据库的操作的service，在控制器中调用对应的service，通过访问对应的url进行操作。  

当然，若是集群部署的话每个实例都含有控制器部分对数据库进行操作，这样难免感觉冗余，因此，可以把对数据库的控制独立出来成为单独的对任务状态操作的项目。该项目可见文件夹《elasticjobstate》，因此，本项目把web访问屏蔽，controller代码仅做展示。
![在这里插入图片描述](https://img-blog.csdnimg.cn/20200914203252599.png#pic_center)

## 4.4可视化界面
Elastic-job官方提供了后台管理界面，显示每个任务当前的分片数、状态、作业信息，可进行失效（暂停）、生效（继续）、终止等操作，可以方便的对任务进行操作以及监测。  
后台管理项目已打包好见文件夹《elastic-job-lite-console-2.1.4》，windows打开/bin目录下的startup.bat即可运行，访问地址localhost:8899登陆账号密码见《auth.properties》  
![在这里插入图片描述](https://img-blog.csdnimg.cn/20200914203334849.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl80NDAwMTY4MQ==,size_16,color_FFFFFF,t_70#pic_center)
## 4.5 超时报错
只需要在当前分片执行任务后开启新线程进行任务时间的监控，当超过预定时间，在日志报错。如下图所示，代码中仅在FileImportJob实现超时功能，以作示例。
![在这里插入图片描述](https://img-blog.csdnimg.cn/20200914203515265.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl80NDAwMTY4MQ==,size_16,color_FFFFFF,t_70#pic_center)

# 5.完成效果
## 5.1导出文件
从数据库中导出数据前，数据库需要有一定量的数据，运行test包下的writeDataToDatabase()方法向数据库中插入10万条数据。之后便可以启动项目，运行Springboot启动方法。
导出文件如图所示
![在这里插入图片描述](https://img-blog.csdnimg.cn/20200914203545356.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl80NDAwMTY4MQ==,size_16,color_FFFFFF,t_70#pic_center)  
![在这里插入图片描述](https://img-blog.csdnimg.cn/20200914203613403.png#pic_center)

每个文件中均有1000条数据，一共1w条数据。
## 5.2文件分割
文件分割将文件分割到10个文件中，每个文件中均只含流水号尾号为分片号的数据。如下图所示。
![在这里插入图片描述](https://img-blog.csdnimg.cn/20200914203646102.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl80NDAwMTY4MQ==,size_16,color_FFFFFF,t_70#pic_center)  
![在这里插入图片描述](https://img-blog.csdnimg.cn/20200914203655765.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl80NDAwMTY4MQ==,size_16,color_FFFFFF,t_70#pic_center)
## 5.3文件排序
文件排序将分割后的10个文件按照流水号排序，如下图所示

![在这里插入图片描述](https://img-blog.csdnimg.cn/2020091420380410.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl80NDAwMTY4MQ==,size_16,color_FFFFFF,t_70#pic_center)
![在这里插入图片描述](https://img-blog.csdnimg.cn/20200914203814475.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl80NDAwMTY4MQ==,size_16,color_FFFFFF,t_70#pic_center)

## 5.4文件对账
本项目中目前只生成了一组分割排序好的文件作为主机方文件，默认另一份平台方也已分割排序好在本地目录中。则该步骤文件对账针对如下三种错误进行验证
数据不匹配----->修改第一行卡号
![在这里插入图片描述](https://img-blog.csdnimg.cn/20200914203855558.png#pic_center)
数据遗漏----->删除该行
![在这里插入图片描述](https://img-blog.csdnimg.cn/20200914203917450.png#pic_center)
数据多出或重复----->复制一行
![在这里插入图片描述](https://img-blog.csdnimg.cn/20200914203945947.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl80NDAwMTY4MQ==,size_16,color_FFFFFF,t_70#pic_center)
则对账结果在数据库中为
![在这里插入图片描述](https://img-blog.csdnimg.cn/2020091420460718.png#pic_center)  

## 5.6部署
该项目为一个springboot项目，可以使用mvn install -DskipTests命令打包成jar包。
![在这里插入图片描述](https://img-blog.csdnimg.cn/20200914205221503.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl80NDAwMTY4MQ==,size_16,color_FFFFFF,t_70#pic_center)
![在这里插入图片描述](https://img-blog.csdnimg.cn/20200914205351574.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl80NDAwMTY4MQ==,size_16,color_FFFFFF,t_70#pic_center)
使用SFTP工具上传到服务器中用Java -jar即可运行jar包。
当然，elastic-job支持集群部署，但是，如果部署在同一环境中，注意修改端口号，否则会报错无法运行，尽量部署在不同的环境中。
