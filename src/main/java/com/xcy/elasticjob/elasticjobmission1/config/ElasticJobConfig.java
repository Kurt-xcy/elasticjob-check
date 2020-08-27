package com.xcy.elasticjob.elasticjobmission1.config;


import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.dangdang.ddframe.job.config.JobCoreConfiguration;
import com.dangdang.ddframe.job.config.simple.SimpleJobConfiguration;
import com.dangdang.ddframe.job.event.JobEventConfiguration;
import com.dangdang.ddframe.job.event.rdb.JobEventRdbConfiguration;
import com.dangdang.ddframe.job.lite.config.LiteJobConfiguration;
import com.dangdang.ddframe.job.lite.spring.api.SpringJobScheduler;
import com.dangdang.ddframe.job.reg.base.CoordinatorRegistryCenter;


import com.xcy.elasticjob.elasticjobmission1.job.FileCheckJob;
import com.xcy.elasticjob.elasticjobmission1.job.FileDivideJob;
import com.xcy.elasticjob.elasticjobmission1.job.FileImportJob;
import com.xcy.elasticjob.elasticjobmission1.job.FileSortJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;


/**
 * @author Administrator
 * @version 1.0
 **/
@Configuration
public class ElasticJobConfig {

    //分片数(分割的文件数)
    public static final int shardingTotalCount = 10;


    @Autowired
    private DataSource dataSource; //数据源已经存在，直接引入


    public static int dump = 9888;

    @Autowired
    FileDivideJob fileDivideJob;

    @Autowired
    FileSortJob fileSortJob;

    @Autowired
    FileCheckJob fileCheckJob;

    @Autowired
    FileImportJob fileImportJob;

    @Autowired
    CoordinatorRegistryCenter registryCenter;


    /**
     * 配置任务详细信息
     * @param jobClass 任务执行类
     * @param cron  执行策略
     * @param shardingTotalCount 分片数量
     * @param shardingItemParameters 分片个性化参数
     * @return
     */
    private LiteJobConfiguration createJobConfiguration(final Class<? extends SimpleJob> jobClass,
                                                        final String cron,
                                                        final int shardingTotalCount,
                                                        final String shardingItemParameters){
        //JobCoreConfigurationBuilder
        JobCoreConfiguration.Builder JobCoreConfigurationBuilder = JobCoreConfiguration.newBuilder(jobClass.getName(), cron, shardingTotalCount);
        //设置shardingItemParameters
        if(!StringUtils.isEmpty(shardingItemParameters)){
            JobCoreConfigurationBuilder.shardingItemParameters(shardingItemParameters);
        }
        JobCoreConfiguration jobCoreConfiguration = JobCoreConfigurationBuilder.build();
        //创建SimpleJobConfiguration
        SimpleJobConfiguration simpleJobConfiguration = new SimpleJobConfiguration(jobCoreConfiguration, jobClass.getCanonicalName());
        //创建LiteJobConfiguration
        LiteJobConfiguration liteJobConfiguration = LiteJobConfiguration.newBuilder(simpleJobConfiguration).overwrite(true)
                .monitorPort(dump++)//设置dump端口
                .build();
        return liteJobConfiguration;
    }


    @PostConstruct
    public void initSimpleElasticJob() {

        // 增加任务事件追踪配置
        JobEventConfiguration jobEventConfig = new JobEventRdbConfiguration(dataSource);

        //创建SpringJobScheduler

        new SpringJobScheduler(fileDivideJob, registryCenter,
                createJobConfiguration(fileDivideJob.getClass(), "0/8 * * * * ? ", shardingTotalCount, "0=0,1=1,2=2,3=3,4=4,5=5,6=6,7=7,8=8,9=9")
                , jobEventConfig).init();

        new SpringJobScheduler(fileCheckJob, registryCenter,
                createJobConfiguration(fileCheckJob.getClass(), "0/7 * * * * ? ", shardingTotalCount, "0=0,1=1,2=2,3=3,4=4,5=5,6=6,7=7,8=8,9=9")
                , jobEventConfig).init();

        new SpringJobScheduler(fileSortJob, registryCenter,
                createJobConfiguration(fileSortJob.getClass(), "0/6 * * * * ? ", shardingTotalCount, "0=0,1=1,2=2,3=3,4=4,5=5,6=6,7=7,8=8,9=9")
                , jobEventConfig).init();

        new SpringJobScheduler(fileImportJob, registryCenter,
                createJobConfiguration(fileImportJob.getClass(), "0/5 * * * * ? ", shardingTotalCount, "0=0,1=1,2=2,3=3,4=4,5=5,6=6,7=7,8=8,9=9")
                , jobEventConfig).init();
    }




}
